# chef-mgmt-mail order-placed notification — design

**Date:** 2026-04-25
**Branch:** lab09
**Scope:** `chef-mgmt-mail` (cleanup + new flow), `chef-mgmt-backend` (HTTP client + hook)

---

## 1. Goals

- Send a styled email to a chef whenever a new order is assigned to them.
- Bring `chef-mgmt-mail` up to the same Spring Boot 4.0.3 / Java 24 platform as `chef-mgmt-backend`.
- Replace placeholder string-substitution mail rendering with real Thymeleaf templating.
- Establish a modern, low-boilerplate, sync HTTP communication path from `chef-mgmt-backend` to `chef-mgmt-mail`, inspired by `chef-mgmt-backendV2`.
- Tighten the hex architecture in `chef-mgmt-mail`: drop dead modules and surfaces; keep the service single-purpose.

## 2. Non-goals

- No tests in this iteration (explicit decision).
- No async / queue / retry / saga. One sync HTTP attempt; failure is logged.
- No mail history / audit table. The User module is removed, not replaced.
- No i18n. English-only subject and body.
- No security on the mail endpoint (internal-only on localhost; flagged for future hardening).
- No frontend changes.

## 3. Architecture

### 3.1 — Topology

```
[ chef-mgmt-backend ]                                  [ chef-mgmt-mail ]
                                                      ┌────────────────────────┐
order saved in DB                                     │                        │
       │                                              │                        │
       ▼  (last action in OrderServiceBean.save)      │                        │
   MailClient (RestClient + @HttpExchange)            │                        │
       │                                              │                        │
       │   HTTP POST /api/v1/mails/orders             │                        │
       │   body: { chefId, orderId }                  │                        │
       └──────────────────────────────────────────────▶ OrderMailController    │
                                                      │   │                    │
                                                      │   ▼                    │
                                                      │ OrderMailServiceBean   │
                                                      │ (facade over 4 ports)  │
                                                      │   • load chef    (DB)  │
                                                      │   • load order   (DB)  │
                                                      │   • render Thymeleaf   │
                                                      │   • send via SMTP      │──▶ Mailtrap
                                                      │                        │
                                                      └────────────────────────┘
```

Both services point at the same `chef_mgmt_db` (`localhost:5432`); mail reads `CHEFS` and `ORDERS` directly. Backend is on **8777**, mail is on **8888**.

### 3.2 — `chef-mgmt-mail` hex layout

Modules unchanged; contents reshaped.

| Module | Role | Changes |
|---|---|---|
| `core-api` | Pure interfaces + domain records, no Spring. | Add `Chef`, `Order` records. Add `ChefService`, `OrderService` outbound ports. Add `OrderMailService` inbound port + `OrderMailResult` record. Reshape `MailCreationService` + `MailSenderService` to the new method shapes. Drop `UserService` interface, the old `MailService` interface, and the old `MailRequest` / `MailResponse` records. `SendingStatus` enum stays. |
| `core-impl` | Use-case layer. | Add `OrderMailServiceBean` facade. Delete the old `MailServiceBean`. |
| `persistence-adapter` | JPA-bound adapter. | Add read-only `ChefEntity`, `OrderEntity` + repos + port impls + mappers. Delete `UserEntity`, `Role`, `UserRepository`, `UserServiceBean`. |
| `mail-adapter` | Outbound mail adapter. | Add `spring-boot-starter-thymeleaf`. Add `templates/order-placed.html`. Reimplement `MailCreationServiceBean` using `SpringTemplateEngine`. Adapt `MailSenderServiceBean` to the new interface. Delete the old `email.html` placeholder template. |
| `rest-adapter` | Inbound HTTP adapter. | Add `OrderMailController` + DTOs + result mapper at `POST /api/v1/mails/orders`. Delete the old generic mail controller, request/response DTOs, and `SendingStatusDTO`. |
| `application` | Bootstrap. | Profile-aware config: `application.yaml` committed, `application-local.yaml` gitignored. |

### 3.3 — Coordinating bean classification

`OrderMailServiceBean` is a **facade** (linear: load → load → render → send; no branching, no retries, no compensation). The codebase has zero true orchestrators in scope. The remaining beans (`ChefServiceBean`, `OrderServiceBean`, `MailCreationServiceBean`, `MailSenderServiceBean`, `OrderMailControllerBean`) are thin adapters around exactly one underlying API call.

## 4. Detailed design

### 4.1 — `core-api` (no Spring)

```java
// en.sd.model.domain
public record Chef(UUID id, String name, String email) {}
public record Order(UUID id, String itemName, Double totalPrice, ZonedDateTime orderedAt, UUID chefId) {}

// en.sd.model.mail
public record OrderMailResult(UUID id, String to, SendingStatus status) {}
public enum SendingStatus { SUCCESS, FAILURE }

// en.sd.service.mail (inbound port)
public interface OrderMailService {
    OrderMailResult sendOrderMail(UUID chefId, UUID orderId);
}

// en.sd.service.mail (rendering port)
public interface MailCreationService {
    String renderOrderPlaced(Chef chef, Order order);
}

// en.sd.service.mail (sending port)
public interface MailSenderService {
    SendingStatus sendHtml(String to, String subject, String htmlBody);
}

// en.sd.service.persistence (outbound ports)
public interface ChefService  { Chef  getById(UUID id); }
public interface OrderService { Order getById(UUID id); }
```

Existing `DataNotFoundException` + `ExceptionCode` are reused; add `ORDER_NOT_FOUND` if absent.

### 4.2 — `core-impl`

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderMailServiceBean implements OrderMailService {

    private static final String ORDER_PLACED_SUBJECT = "You have a new order";

    private final ChefService chefService;
    private final OrderService orderService;
    private final MailCreationService mailCreationService;
    private final MailSenderService mailSenderService;

    @Override
    public OrderMailResult sendOrderMail(UUID chefId, UUID orderId) {
        Chef chef = chefService.getById(chefId);
        Order order = orderService.getById(orderId);
        String htmlBody = mailCreationService.renderOrderPlaced(chef, order);
        SendingStatus status = mailSenderService.sendHtml(chef.email(), ORDER_PLACED_SUBJECT, htmlBody);
        UUID correlationId = UUID.randomUUID();
        log.info("Order mail dispatched: id={} order={} to={} status={}", correlationId, orderId, chef.email(), status);
        return new OrderMailResult(correlationId, chef.email(), status);
    }
}
```

### 4.3 — `persistence-adapter`

Read-only JPA entities styled to match `chef-mgmt-backend/.../model/`: full Lombok set, `@Entity @Table(name = "UPPER_SNAKE")`, every field with `@Column(name = "UPPER_SNAKE")`. No `@GeneratedValue` (mail never inserts).

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CHEFS")
public class ChefEntity {

    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "BIRTH_DATE")
    private ZonedDateTime birthDate;

    @Column(name = "RATING")
    private double rating;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ORDERS")
public class OrderEntity {

    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "ITEM_NAME")
    private String itemName;

    @Column(name = "TOTAL_PRICE")
    private Double totalPrice;

    @Column(name = "ORDERED_AT")
    private ZonedDateTime orderedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHEF_ID", nullable = false)
    private ChefEntity chef;
}
```

Repositories:
```java
public interface ChefRepository  extends JpaRepository<ChefEntity,  UUID> {}
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {}
```

Port impls (one per port, MapStruct mapper between entity and domain record):
```java
@Mapper(componentModel = "spring")
public interface ChefMapper  { Chef  toDomain(ChefEntity entity); }

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "chef.id", target = "chefId")
    Order toDomain(OrderEntity entity);
}

@Service
@RequiredArgsConstructor
public class ChefServiceBean implements ChefService {

    private final ChefRepository chefRepository;
    private final ChefMapper chefMapper;

    @Override
    public Chef getById(UUID id) {
        ChefEntity entity = chefRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, id));
        return chefMapper.toDomain(entity);
    }
}
// OrderServiceBean: symmetric, throws ORDER_NOT_FOUND.
```

`OrderMapper.toDomain` projects `entity.getChef().getId()` into `Order.chefId`.

**Removed:** `UserEntity`, `Role`, `UserRepository`, `UserService` (interface in core-api), `UserServiceBean`.

### 4.4 — `mail-adapter`

Add dependency in this module's pom: `spring-boot-starter-thymeleaf`.

```java
@Service
@RequiredArgsConstructor
public class MailCreationServiceBean implements MailCreationService {

    private final SpringTemplateEngine templateEngine;

    @Override
    public String renderOrderPlaced(Chef chef, Order order) {
        Context context = new Context();
        context.setVariable("chef", chef);
        context.setVariable("order", order);
        return templateEngine.process("order-placed", context);
    }
}

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderServiceBean implements MailSenderService {

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Override
    public SendingStatus sendHtml(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            javaMailSender.send(message);
            return SendingStatus.SUCCESS;
        } catch (Exception e) {
            log.error("Failed to send mail to {}: {}", to, e.getMessage());
            return SendingStatus.FAILURE;
        }
    }
}
```

Template at `mail-adapter/src/main/resources/templates/order-placed.html`:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>New order</title>
</head>
<body style="font-family: Arial, sans-serif; background:#f4f4f4; padding:24px;">
<div style="max-width:600px;margin:0 auto;background:#ffffff;padding:24px;border-radius:8px;">
    <h2 style="color:#2c3e50;">Hey <span th:text="${chef.name()}">Chef</span>, you have a new order!</h2>

    <h3 style="margin-top:24px;color:#34495e;">Chef details</h3>
    <ul>
        <li><strong>Name:</strong> <span th:text="${chef.name()}"></span></li>
        <li><strong>Email:</strong> <span th:text="${chef.email()}"></span></li>
    </ul>

    <h3 style="margin-top:24px;color:#34495e;">Order details</h3>
    <ul>
        <li><strong>Order ID:</strong> <span th:text="${order.id()}"></span></li>
        <li><strong>Item:</strong> <span th:text="${order.itemName()}"></span></li>
        <li><strong>Price:</strong> <span th:text="${order.totalPrice()}"></span></li>
        <li><strong>Placed at:</strong> <span th:text="${#temporals.format(order.orderedAt(), 'yyyy-MM-dd HH:mm')}"></span></li>
    </ul>

    <p style="margin-top:24px;color:#7f8c8d;font-size:12px;">
        This is an automated message from chef-mgmt. Please do not reply.
    </p>
</div>
</body>
</html>
```

**Removed:** the old `templates/email.html` and any placeholder-substitution helper (`MailUtils` if it served only that purpose).

### 4.5 — `rest-adapter`

```java
public record SendOrderMailRequestDTO(@NotNull UUID chefId, @NotNull UUID orderId) {}

public record SendOrderMailResponseDTO(UUID id, String to, String status) {}

@Mapper(componentModel = "spring")
public interface OrderMailResultMapper {
    SendOrderMailResponseDTO toResponse(OrderMailResult result);
}

@RequestMapping("/v1/mails/orders")
public interface OrderMailController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    SendOrderMailResponseDTO send(@Valid @RequestBody SendOrderMailRequestDTO request);
}

@RestController
@RequiredArgsConstructor
public class OrderMailControllerBean implements OrderMailController {

    private final OrderMailService orderMailService;
    private final OrderMailResultMapper orderMailResultMapper;

    @Override
    public SendOrderMailResponseDTO send(SendOrderMailRequestDTO request) {
        OrderMailResult result = orderMailService.sendOrderMail(request.chefId(), request.orderId());
        return orderMailResultMapper.toResponse(result);
    }
}
```

**Removed:** `MailController`, `MailControllerBean`, `MailRequestDTO`, `MailResponseDTO`, `SendingStatusDTO`, and their mappers.

### 4.6 — `chef-mgmt-backend` HTTP client

Declarative client backed by `RestClient` + `@HttpExchange`. No `webflux` dependency.

```java
// service/mailing/client/MailClient.java
@HttpExchange("/v1/mails")
public interface MailClient {

    @PostExchange("/orders")
    SendOrderMailResponseDTO sendOrderMail(@RequestBody SendOrderMailRequestDTO request);
}

// service/mailing/client/SendOrderMailRequestDTO.java
public record SendOrderMailRequestDTO(UUID chefId, UUID orderId) {}

// service/mailing/client/SendOrderMailResponseDTO.java
public record SendOrderMailResponseDTO(UUID id, String to, String status) {}

// config/MailClientConfig.java
@Configuration
public class MailClientConfig {

    @Bean
    public MailClient mailClient(@Value("${chef-mgmt-mail.base-url}") String baseUrl) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(RestClient.builder().baseUrl(baseUrl).build()))
                .build()
                .createClient(MailClient.class);
    }
}
```

A MapStruct mapper builds the request from `(UUID chefId, UUID orderId)`:

```java
@Mapper(componentModel = "spring")
public interface SendOrderMailRequestMapper {

    SendOrderMailRequestDTO toRequest(UUID chefId, UUID orderId);
}
```

### 4.7 — `chef-mgmt-backend` `OrderServiceBean.save()` hook

Direct call, no events. Mail call is the last action; failure is caught and logged inline.

```java
// add to OrderServiceBean fields
private final MailClient mailClient;
private final SendOrderMailRequestMapper mailRequestMapper;

@Override
@Transactional
public OrderResponseDTO save(UUID chefId, OrderRequestDTO orderRequestDTO) {
    ChefEntity chef = chefRepository.findById(chefId)
            .orElseThrow(() -> new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, chefId));
    OrderEntity toAdd = orderMapper.convertRequestDtoToEntity(orderRequestDTO, chef);
    OrderEntity saved = orderRepository.save(toAdd);
    OrderResponseDTO response = orderMapper.convertEntityToResponseDto(saved);

    SendOrderMailRequestDTO mailRequest = mailRequestMapper.toRequest(chef.getId(), saved.getId());
    try {
        SendOrderMailResponseDTO mailResponse = mailClient.sendOrderMail(mailRequest);
        log.info("Order mail sent: order={} to={} status={}", saved.getId(), mailResponse.to(), mailResponse.status());
    } catch (Exception e) {
        log.error("Order mail failed: order={} chef={}: {}", saved.getId(), chef.getId(), e.getMessage());
    }

    return response;
}
```

`MailingService` / `MailingServiceBean` are intentionally not introduced — the indirection added no value beyond what's already inline here.

## 5. Configuration

### 5.1 — `chef-mgmt-mail/application/src/main/resources/application.yaml` (committed)

```yaml
spring:
  application:
    name: chef-mgmt-mail
  profiles:
    active: local
  datasource:
    url: jdbc:postgresql://localhost:5432/chef_mgmt_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false

server:
  port: 8888
  servlet:
    context-path: '/api'

app:
  mail:
    from: noreply@chefmgmt.com
```

### 5.2 — `chef-mgmt-mail/application/src/main/resources/application-local.yaml` (gitignored)

```yaml
spring:
  mail:
    host: sandbox.smtp.mailtrap.io
    port: 2525
    username: <your-mailtrap-username>
    password: <your-mailtrap-password>
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

A committed `application-local.yaml.example` documents the same shape with placeholder values.

### 5.3 — `chef-mgmt-mail/.gitignore`

```
**/application-local.yaml
**/application-local.yml
```

### 5.4 — `chef-mgmt-backend/src/main/resources/application.yaml`

Add:
```yaml
chef-mgmt-mail:
  base-url: http://localhost:8888/api
```

## 6. Dependency bump (chef-mgmt-mail)

| Property / dep | From | To |
|---|---|---|
| `spring-boot.version` | `3.4.4` | `4.0.3` |
| `java.version` | `23` | `24` |
| `maven.compiler.source` / `target` | `23` | `24` |
| `org.postgresql:postgresql` | `42.7.5` | `42.7.8` |
| `org.projectlombok:lombok` | `1.18.36` | `1.18.38` |
| `org.mapstruct:mapstruct` (+ processor) | `1.5.5.Final` | `1.6.3` |
| `io.swagger.core.v3:swagger-annotations` | `2.2.28` | `2.2.31` |
| `springdoc-openapi-starter-webmvc-ui` | `2.8.6` | (keep) |

New: `spring-boot-starter-thymeleaf` in `mail-adapter`.

No Flyway. Mail does not own the schema; `ddl-auto: validate` only.

## 7. Verification & acceptance

### 7.1 — Manual end-to-end

1. `chef-mgmt-backend` running on **8777**, DB up.
2. `chef-mgmt-mail` running on 8888 with `application-local.yaml` filled with real Mailtrap creds.
3. Use a chef whose `EMAIL` column is a real address.
4. POST a new order via `POST /api/chefs/{chefId}/orders`.
5. Mailtrap inbox: mail from `noreply@chefmgmt.com` titled "You have a new order" with chef + order details.
6. Stop `chef-mgmt-mail`; POST another order → order still saves; backend logs `Order mail failed: ...`.

### 7.2 — Acceptance checklist

- [ ] `chef-mgmt-mail` builds and starts on Spring Boot 4.0.3 / Java 24.
- [ ] User module removed from `persistence-adapter`.
- [ ] Old generic mail flow + DTOs + placeholder template removed.
- [ ] `POST /api/v1/mails/orders { chefId, orderId }` returns 201 and triggers a Mailtrap delivery.
- [ ] Saving an order triggers a mail send; mail-service down ⇒ order still saves.
- [ ] Email body is Thymeleaf-rendered with chef + order details and a greeting.
- [ ] `application-local.yaml` is gitignored; `application-local.yaml.example` is committed.

## 8. Style rules in force

- Each annotation on its own line above the field/method/parameter — never `@Annotation private ...` on one line.
- No `var` — explicit types throughout.
- DTOs/records are not constructed inline as method-call arguments; they are declared on a typed local on the line above. (Returns and fluent builders are exempt.)
- Conversion between types goes through MapStruct mappers (`@Mapper(componentModel = "spring")`).
- JPA entities mirror the `chef-mgmt-backend` style exactly: full Lombok set, `@Entity @Table(name = "UPPER_SNAKE")`, `@Column(name = "UPPER_SNAKE")` on every field, `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "...", nullable = ...)` for relations.

## 9. Implementation order

1. `chef-mgmt-mail` deps bump + module cleanup (rip User module, delete old generic flow).
2. `chef-mgmt-mail` chef + order read side (entities, repos, mappers, port impls).
3. `chef-mgmt-mail` Thymeleaf + sender adapter (template, render bean, send bean, `app.mail.from`).
4. `chef-mgmt-mail` facade + controller + DTOs (`/v1/mails/orders`).
5. `application-local.yaml` + `.gitignore` + example file.
6. `chef-mgmt-backend` `MailClient` + `MailClientConfig`.
7. `chef-mgmt-backend` `OrderServiceBean.save()` hook (inject + call + try/catch).
8. End-to-end run against Mailtrap; fix anything that flares.
