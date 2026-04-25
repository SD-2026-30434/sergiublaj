# chef-mgmt-mail Order Notification Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** When `chef-mgmt-backend` saves a new order for a chef, it calls `chef-mgmt-mail` over HTTP with `(chefId, orderId)`; mail looks up the chef and order from the shared DB, renders a Thymeleaf email, and sends it via Mailtrap SMTP.

**Architecture:** Hex-architected `chef-mgmt-mail` (core-api / core-impl / persistence-adapter / mail-adapter / rest-adapter). New `OrderMailServiceBean` facade in `core-impl` coordinates four ports: chef lookup, order lookup, Thymeleaf render, JavaMail send. Backend talks to mail via a declarative `RestClient` + `@HttpExchange` interface. Old generic mail flow and dead User module are deleted.

**Tech Stack:** Spring Boot 4.0.3, Java 24, Spring Data JPA + PostgreSQL, Spring Mail (JavaMailSender), Thymeleaf, MapStruct 1.6.3, Lombok, springdoc-openapi.

**Testing:** **No automated tests in this iteration** (explicit user decision — see spec §2 Non-goals). Each task has a build-and-run verification step instead.

**Spec:** `docs/superpowers/specs/2026-04-25-chef-mgmt-mail-order-notification-design.md`

**Style rules in force** (from spec §8):
- Each annotation on its own line; never `@Annotation private ...` on one line.
- No `var`; explicit types throughout.
- DTOs/records not constructed inline as method-call arguments — use a typed local on the line above (returns and fluent builders are exempt).
- Conversions go through MapStruct mappers (`@Mapper(componentModel = "spring")`).
- JPA entities mirror `chef-mgmt-backend/.../model/` exactly: `@Getter @Setter @NoArgsConstructor @AllArgsConstructor`, `@Entity @Table(name = "UPPER_SNAKE")`, every field `@Column(name = "UPPER_SNAKE")`, lazy `@ManyToOne` + `@JoinColumn`.

---

## File Map

### chef-mgmt-mail — files to delete

```
core-api/.../model/mail/MailRequest.java
core-api/.../model/mail/MailResponse.java
core-api/.../model/mapper/DtoMapper.java
core-api/.../service/mail/MailService.java
core-api/.../service/persistence/UserService.java
core-impl/.../service/MailServiceBean.java
mail-adapter/.../service/MailCreationServiceBean.java     (will be replaced)
mail-adapter/.../service/MailSenderServiceBean.java       (will be replaced)
mail-adapter/.../util/MailUtils.java
mail-adapter/.../resources/templates/email.html
persistence-adapter/.../entity/Role.java
persistence-adapter/.../entity/UserEntity.java
persistence-adapter/.../repository/UserRepository.java
persistence-adapter/.../service/UserServiceBean.java
rest-adapter/.../controller/mail/MailController.java
rest-adapter/.../controller/mail/MailControllerBean.java
rest-adapter/.../model/mail/MailRequestDTO.java
rest-adapter/.../model/mail/MailResponseDTO.java
rest-adapter/.../model/mail/SendingStatusDTO.java
rest-adapter/.../model/mapper/MailRequestDTOMapper.java
rest-adapter/.../model/mapper/MailResponseDTOMapper.java
```

### chef-mgmt-mail — files to create

```
core-api/.../model/domain/Chef.java                            record
core-api/.../model/domain/Order.java                           record
core-api/.../model/mail/OrderMailResult.java                   record
core-api/.../service/mail/OrderMailService.java                interface
core-api/.../service/persistence/ChefService.java              interface
core-api/.../service/persistence/OrderService.java             interface

core-impl/.../service/OrderMailServiceBean.java                facade

persistence-adapter/.../entity/ChefEntity.java                 read-only @Entity
persistence-adapter/.../entity/OrderEntity.java                read-only @Entity
persistence-adapter/.../repository/ChefRepository.java
persistence-adapter/.../repository/OrderRepository.java
persistence-adapter/.../mapper/ChefMapper.java                 MapStruct
persistence-adapter/.../mapper/OrderMapper.java                MapStruct
persistence-adapter/.../service/ChefServiceBean.java
persistence-adapter/.../service/OrderServiceBean.java

mail-adapter/.../resources/templates/order-placed.html         Thymeleaf

rest-adapter/.../controller/mail/OrderMailController.java
rest-adapter/.../controller/mail/OrderMailControllerBean.java
rest-adapter/.../model/mail/SendOrderMailRequestDTO.java
rest-adapter/.../model/mail/SendOrderMailResponseDTO.java
rest-adapter/.../model/mapper/OrderMailResultMapper.java       MapStruct

application/src/main/resources/application-local.yaml.example
.gitignore                                                     (chef-mgmt-mail root)
```

### chef-mgmt-mail — files to modify

```
pom.xml                                            parent: bump Boot 4.0.3, Java 24, deps
application/pom.xml                                java.version → 24
core-api/pom.xml                                   java.version → 24
core-impl/pom.xml                                  java.version → 24
mail-adapter/pom.xml                               java.version → 24, add Thymeleaf
persistence-adapter/pom.xml                        java.version → 24
rest-adapter/pom.xml                               java.version → 24

core-api/.../model/exception/ExceptionCode.java    add ORDER_NOT_FOUND, CHEF_NOT_FOUND
core-api/.../service/mail/MailCreationService.java reshape signature
core-api/.../service/mail/MailSenderService.java   reshape signature

mail-adapter/.../service/MailCreationServiceBean.java   reimplement (Thymeleaf)
mail-adapter/.../service/MailSenderServiceBean.java     reimplement (helper, html, from-config)

application/src/main/resources/application.yaml         active profile, ddl-auto, app.mail.from
```

### chef-mgmt-backend — files to create

```
src/main/java/en/sd/chefmgmt/service/mailing/client/MailClient.java
src/main/java/en/sd/chefmgmt/service/mailing/client/SendOrderMailRequestDTO.java
src/main/java/en/sd/chefmgmt/service/mailing/client/SendOrderMailResponseDTO.java
src/main/java/en/sd/chefmgmt/config/MailClientConfig.java
src/main/java/en/sd/chefmgmt/mapper/SendOrderMailRequestMapper.java
```

### chef-mgmt-backend — files to modify

```
src/main/java/en/sd/chefmgmt/service/order/OrderServiceBean.java   inject + call mail
src/main/resources/application.yaml                                add chef-mgmt-mail.base-url
```

---

## Phase A — Track and align platform

### Task A1: Track chef-mgmt-mail in git as a baseline

**Files:**
- Create: `chef-mgmt-mail/.gitignore`

**Why:** `chef-mgmt-mail/` is currently untracked. Tracking it as-is first means subsequent diffs show *just our changes*. Without this baseline commit, the first edit-commit would dump the entire directory as an unreviewable blob.

- [ ] **Step 1: Create `chef-mgmt-mail/.gitignore`**

```gitignore
# Maven build output
target/

# IDE
.idea/
*.iml
*.iws
*.ipr
.vscode/

# OS
.DS_Store

# Local-only configuration with secrets
**/application-local.yaml
**/application-local.yml
```

- [ ] **Step 2: Stage and commit the baseline**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
git add chef-mgmt-mail/
git status
```

Expected: a long list of new files under `chef-mgmt-mail/` (but no `target/`, no `.idea/`).

```bash
git commit -m "chore(mail): track chef-mgmt-mail baseline before lab09 work"
```

- [ ] **Step 3: Verify build still works on the existing platform** (sanity check before bumping)

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail
./mvnw -q -DskipTests clean compile
```

Expected: BUILD SUCCESS.

---

### Task A2: Bump parent pom to Spring Boot 4.0.3 / Java 24

**Files:**
- Modify: `chef-mgmt-mail/pom.xml`

- [ ] **Step 1: Replace the `<parent>` and `<properties>` blocks**

In `chef-mgmt-mail/pom.xml`, replace lines 22–43 (the `<parent>` and `<properties>` blocks) with:

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.3</version>
        <relativePath/>
    </parent>

    <properties>
        <jackson.version>2.15.0</jackson.version>
        <java.version>24</java.version>
        <commons-io.version>2.19.0</commons-io.version>
        <lombok-mapstruct.version>0.2.0</lombok-mapstruct.version>
        <lombok.version>1.18.38</lombok.version>
        <mapstruct.version>1.6.3</mapstruct.version>
        <maven-compiler-plugin.version>3.14.0</maven-compiler-plugin.version>
        <maven.compiler.source>24</maven.compiler.source>
        <maven.compiler.target>24</maven.compiler.target>
        <postgresql.version>42.7.8</postgresql.version>
        <spring-boot.version>4.0.3</spring-boot.version>
        <swagger-annotations.version>2.2.31</swagger-annotations.version>
        <swagger-openapi.version>2.8.6</swagger-openapi.version>
    </properties>
```

- [ ] **Step 2: Add `spring-boot-starter-thymeleaf` to dependencyManagement**

After the existing `spring-boot-starter-mail` `<dependency>` block (around line 102 in the original) and before the `postgresql` block, add:

```xml
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-thymeleaf</artifactId>
                <version>${spring-boot.version}</version>
            </dependency>
```

- [ ] **Step 3: Verify build**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail
./mvnw -q -DskipTests clean compile
```

Expected: BUILD SUCCESS. (If a Spring Boot 4 / Spring 7 deprecation surfaces in the existing code, that's expected — it'll be cleaned up when we delete the old flow in Phase B.)

If the build fails on a dependency resolution error, double-check `<version>4.0.3</version>` is exact and run `./mvnw -U -DskipTests clean compile` to refresh.

- [ ] **Step 4: Commit**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
git add chef-mgmt-mail/pom.xml
git commit -m "chore(mail): bump parent to Spring Boot 4.0.3 / Java 24"
```

---

### Task A3: Bump module poms to Java 24

**Files:**
- Modify: `chef-mgmt-mail/application/pom.xml`
- Modify: `chef-mgmt-mail/core-api/pom.xml`
- Modify: `chef-mgmt-mail/core-impl/pom.xml`
- Modify: `chef-mgmt-mail/mail-adapter/pom.xml`
- Modify: `chef-mgmt-mail/persistence-adapter/pom.xml`
- Modify: `chef-mgmt-mail/rest-adapter/pom.xml`

Each module pom currently has its own `<maven.compiler.source>23</maven.compiler.source>` / `<target>23</target>` overrides. Bump them all to 24.

- [ ] **Step 1: Edit every module pom**

In each of the six files above, replace:

```xml
        <maven.compiler.source>23</maven.compiler.source>
        <maven.compiler.target>23</maven.compiler.target>
```

with:

```xml
        <maven.compiler.source>24</maven.compiler.source>
        <maven.compiler.target>24</maven.compiler.target>
```

- [ ] **Step 2: Verify build**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail
./mvnw -q -DskipTests clean compile
```

Expected: BUILD SUCCESS.

- [ ] **Step 3: Commit**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
git add chef-mgmt-mail/*/pom.xml
git commit -m "chore(mail): bump all modules to Java 24"
```

---

## Phase B — Delete dead code

### Task B1: Delete the User module

**Files:**
- Delete: `chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/entity/Role.java`
- Delete: `chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/entity/UserEntity.java`
- Delete: `chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/repository/UserRepository.java`
- Delete: `chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/service/UserServiceBean.java`
- Delete: `chef-mgmt-mail/core-api/src/main/java/en/sd/service/persistence/UserService.java`

**Note:** `core-impl/.../MailServiceBean.java` imports `UserService`, so the project will not compile after this delete alone. That's OK — Task B2 deletes that file too. We commit both deletes together at the end of Phase B.

- [ ] **Step 1: Delete the five files**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
rm chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/entity/Role.java
rm chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/entity/UserEntity.java
rm chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/repository/UserRepository.java
rm chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/service/UserServiceBean.java
rm chef-mgmt-mail/core-api/src/main/java/en/sd/service/persistence/UserService.java
```

(Don't compile yet — proceed to Task B2.)

---

### Task B2: Delete the old generic mail flow

**Files:**
- Delete: `chef-mgmt-mail/core-impl/src/main/java/en/sd/service/MailServiceBean.java`
- Delete: `chef-mgmt-mail/core-api/src/main/java/en/sd/service/mail/MailService.java`
- Delete: `chef-mgmt-mail/core-api/src/main/java/en/sd/model/mail/MailRequest.java`
- Delete: `chef-mgmt-mail/core-api/src/main/java/en/sd/model/mail/MailResponse.java`
- Delete: `chef-mgmt-mail/core-api/src/main/java/en/sd/model/mapper/DtoMapper.java`
- Delete: `chef-mgmt-mail/mail-adapter/src/main/java/en/sd/service/MailCreationServiceBean.java`
- Delete: `chef-mgmt-mail/mail-adapter/src/main/java/en/sd/service/MailSenderServiceBean.java`
- Delete: `chef-mgmt-mail/mail-adapter/src/main/java/en/sd/util/MailUtils.java`
- Delete: `chef-mgmt-mail/mail-adapter/src/main/resources/templates/email.html`
- Delete: `chef-mgmt-mail/rest-adapter/src/main/java/en/sd/controller/mail/MailController.java`
- Delete: `chef-mgmt-mail/rest-adapter/src/main/java/en/sd/controller/mail/MailControllerBean.java`
- Delete: `chef-mgmt-mail/rest-adapter/src/main/java/en/sd/model/mail/MailRequestDTO.java`
- Delete: `chef-mgmt-mail/rest-adapter/src/main/java/en/sd/model/mail/MailResponseDTO.java`
- Delete: `chef-mgmt-mail/rest-adapter/src/main/java/en/sd/model/mail/SendingStatusDTO.java`
- Delete: `chef-mgmt-mail/rest-adapter/src/main/java/en/sd/model/mapper/MailRequestDTOMapper.java`
- Delete: `chef-mgmt-mail/rest-adapter/src/main/java/en/sd/model/mapper/MailResponseDTOMapper.java`

**Why this scope:** the old `MailService` interface and its implementations, request/response records, all old DTOs, the old controller, the old placeholder template, and the old mappers — every artifact that existed only to serve the deleted generic flow.

`MailCreationServiceBean` and `MailSenderServiceBean` will be re-created from scratch in Phase D against the reshaped interfaces — easier than editing in place.

`SendingStatus` (in `core-api/.../model/mail/`) **stays** — it's reused by the new flow.

- [ ] **Step 1: Delete the files**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
rm chef-mgmt-mail/core-impl/src/main/java/en/sd/service/MailServiceBean.java
rm chef-mgmt-mail/core-api/src/main/java/en/sd/service/mail/MailService.java
rm chef-mgmt-mail/core-api/src/main/java/en/sd/model/mail/MailRequest.java
rm chef-mgmt-mail/core-api/src/main/java/en/sd/model/mail/MailResponse.java
rm chef-mgmt-mail/core-api/src/main/java/en/sd/model/mapper/DtoMapper.java
rm chef-mgmt-mail/mail-adapter/src/main/java/en/sd/service/MailCreationServiceBean.java
rm chef-mgmt-mail/mail-adapter/src/main/java/en/sd/service/MailSenderServiceBean.java
rm chef-mgmt-mail/mail-adapter/src/main/java/en/sd/util/MailUtils.java
rm chef-mgmt-mail/mail-adapter/src/main/resources/templates/email.html
rm chef-mgmt-mail/rest-adapter/src/main/java/en/sd/controller/mail/MailController.java
rm chef-mgmt-mail/rest-adapter/src/main/java/en/sd/controller/mail/MailControllerBean.java
rm chef-mgmt-mail/rest-adapter/src/main/java/en/sd/model/mail/MailRequestDTO.java
rm chef-mgmt-mail/rest-adapter/src/main/java/en/sd/model/mail/MailResponseDTO.java
rm chef-mgmt-mail/rest-adapter/src/main/java/en/sd/model/mail/SendingStatusDTO.java
rm chef-mgmt-mail/rest-adapter/src/main/java/en/sd/model/mapper/MailRequestDTOMapper.java
rm chef-mgmt-mail/rest-adapter/src/main/java/en/sd/model/mapper/MailResponseDTOMapper.java
```

- [ ] **Step 2: Reshape `MailCreationService` interface**

Replace the entire content of `chef-mgmt-mail/core-api/src/main/java/en/sd/service/mail/MailCreationService.java` with:

```java
package en.sd.service.mail;

import en.sd.model.domain.Chef;
import en.sd.model.domain.Order;

public interface MailCreationService {

    String renderOrderPlaced(Chef chef, Order order);
}
```

(The imports reference `Chef` and `Order` records that don't exist yet — they're created in Task C1. Compile failure here is expected and resolves at the end of Phase C.)

- [ ] **Step 3: Reshape `MailSenderService` interface**

Replace the entire content of `chef-mgmt-mail/core-api/src/main/java/en/sd/service/mail/MailSenderService.java` with:

```java
package en.sd.service.mail;

import en.sd.model.mail.SendingStatus;

public interface MailSenderService {

    SendingStatus sendHtml(String to, String subject, String htmlBody);
}
```

- [ ] **Step 4: DO NOT compile** — the project is intentionally broken (missing `Chef`, `Order`, `OrderMailService`, etc.). Phase C will restore compilation. Continue to Task B3.

---

### Task B3: Update ExceptionCode for the new flow

**Files:**
- Modify: `chef-mgmt-mail/core-api/src/main/java/en/sd/model/exception/ExceptionCode.java`

`USER_NOT_FOUND` is no longer referenced (the only consumer was the deleted `MailServiceBean`). Replace it with `CHEF_NOT_FOUND` and `ORDER_NOT_FOUND`. Drop `SYNC_MAIL_ERROR` (unused now).

- [ ] **Step 1: Replace the file content**

Full new content of `chef-mgmt-mail/core-api/src/main/java/en/sd/model/exception/ExceptionCode.java`:

```java
package en.sd.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    // Validation & Constraint Violations
    VALIDATION_ERROR("Validation failed.", "ERR_1001"),

    // Domain Errors
    CHEF_NOT_FOUND("Chef with id %s not found.", "ERR_3001"),
    ORDER_NOT_FOUND("Order with id %s not found.", "ERR_3002"),

    // Server Errors
    SERVER_ERROR("Internal server error.", "ERR_5000");

    private final String message;
    private final String code;
}
```

- [ ] **Step 2: Commit Phase B as a single deletion-and-prep commit**

The project does not compile yet. Commit anyway — Phase C will restore green. (Bisecting later is easier when each commit's intent is clear; mixing "delete dead code" with "add new code" produces a noisy diff.)

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
git add -A chef-mgmt-mail/
git status
```

Expected: deletions of all the files listed in B1 and B2, plus modifications to `MailCreationService.java`, `MailSenderService.java`, `ExceptionCode.java`.

```bash
git commit -m "refactor(mail): drop User module + old generic mail flow

Removes UserEntity/UserRepository/UserService and the legacy
MailService/MailRequest/MailResponse generic flow with all its DTOs and
controllers. Reshapes MailCreationService and MailSenderService to the
new (Chef, Order) signature shapes ahead of the order-placed flow.

Project intentionally does not compile after this commit; Phase C
introduces the new domain types that close the gap."
```

---

## Phase C — Add domain types and ports

### Task C1: Add `Chef` and `Order` domain records

**Files:**
- Create: `chef-mgmt-mail/core-api/src/main/java/en/sd/model/domain/Chef.java`
- Create: `chef-mgmt-mail/core-api/src/main/java/en/sd/model/domain/Order.java`

- [ ] **Step 1: Create `Chef.java`**

```java
package en.sd.model.domain;

import java.util.UUID;

public record Chef(UUID id, String name, String email) {
}
```

- [ ] **Step 2: Create `Order.java`**

```java
package en.sd.model.domain;

import java.time.ZonedDateTime;
import java.util.UUID;

public record Order(
        UUID id,
        String itemName,
        Double totalPrice,
        ZonedDateTime orderedAt,
        UUID chefId
) {
}
```

- [ ] **Step 3: Compile core-api alone (project as a whole still won't compile)**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail
./mvnw -q -pl core-api -am -DskipTests clean compile
```

Expected: core-api compiles. (Whole-project compile would still fail — other modules depend on artifacts not yet created.)

---

### Task C2: Add `OrderMailResult` record and `OrderMailService` port

**Files:**
- Create: `chef-mgmt-mail/core-api/src/main/java/en/sd/model/mail/OrderMailResult.java`
- Create: `chef-mgmt-mail/core-api/src/main/java/en/sd/service/mail/OrderMailService.java`

- [ ] **Step 1: Create `OrderMailResult.java`**

```java
package en.sd.model.mail;

import java.util.UUID;

public record OrderMailResult(UUID id, String to, SendingStatus status) {
}
```

- [ ] **Step 2: Create `OrderMailService.java`**

```java
package en.sd.service.mail;

import en.sd.model.mail.OrderMailResult;

import java.util.UUID;

public interface OrderMailService {

    OrderMailResult sendOrderMail(UUID chefId, UUID orderId);
}
```

- [ ] **Step 3: Compile core-api**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail
./mvnw -q -pl core-api -am -DskipTests clean compile
```

Expected: BUILD SUCCESS.

---

### Task C3: Add `ChefService` and `OrderService` outbound ports

**Files:**
- Create: `chef-mgmt-mail/core-api/src/main/java/en/sd/service/persistence/ChefService.java`
- Create: `chef-mgmt-mail/core-api/src/main/java/en/sd/service/persistence/OrderService.java`

- [ ] **Step 1: Create `ChefService.java`**

```java
package en.sd.service.persistence;

import en.sd.model.domain.Chef;

import java.util.UUID;

public interface ChefService {

    Chef getById(UUID id);
}
```

- [ ] **Step 2: Create `OrderService.java`**

```java
package en.sd.service.persistence;

import en.sd.model.domain.Order;

import java.util.UUID;

public interface OrderService {

    Order getById(UUID id);
}
```

- [ ] **Step 3: Compile core-api**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail
./mvnw -q -pl core-api -am -DskipTests clean compile
```

Expected: BUILD SUCCESS.

- [ ] **Step 4: Commit**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
git add chef-mgmt-mail/core-api/
git commit -m "feat(mail/core-api): add Chef/Order domain + OrderMailService + persistence ports"
```

---

## Phase D — Implement adapters

### Task D1: Persistence adapter — entities

**Files:**
- Create: `chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/entity/ChefEntity.java`
- Create: `chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/entity/OrderEntity.java`

- [ ] **Step 1: Create `ChefEntity.java`**

```java
package en.sd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

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
```

- [ ] **Step 2: Create `OrderEntity.java`**

```java
package en.sd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

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

---

### Task D2: Persistence adapter — repositories

**Files:**
- Create: `chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/repository/ChefRepository.java`
- Create: `chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/repository/OrderRepository.java`

- [ ] **Step 1: Create `ChefRepository.java`**

```java
package en.sd.repository;

import en.sd.entity.ChefEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChefRepository extends JpaRepository<ChefEntity, UUID> {
}
```

- [ ] **Step 2: Create `OrderRepository.java`**

```java
package en.sd.repository;

import en.sd.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
}
```

---

### Task D3: Persistence adapter — MapStruct mappers

**Files:**
- Create: `chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/mapper/ChefMapper.java`
- Create: `chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/mapper/OrderMapper.java`

- [ ] **Step 1: Create `ChefMapper.java`**

```java
package en.sd.mapper;

import en.sd.entity.ChefEntity;
import en.sd.model.domain.Chef;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChefMapper {

    Chef toDomain(ChefEntity entity);
}
```

- [ ] **Step 2: Create `OrderMapper.java`**

```java
package en.sd.mapper;

import en.sd.entity.OrderEntity;
import en.sd.model.domain.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "chef.id", target = "chefId")
    Order toDomain(OrderEntity entity);
}
```

---

### Task D4: Persistence adapter — port implementations

**Files:**
- Create: `chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/service/ChefServiceBean.java`
- Create: `chef-mgmt-mail/persistence-adapter/src/main/java/en/sd/service/OrderServiceBean.java`

**Note on transactions:** `OrderEntity.chef` is `FetchType.LAZY`, and `OrderMapper.toDomain` reads `entity.chef.id` (via `@Mapping(source = "chef.id", target = "chefId")`). The mapper runs *after* the repository call returns; without an active transaction the lazy proxy would throw `LazyInitializationException` (especially with `open-in-view: false`, which we set in `application.yaml`). The fix is to wrap each `getById` in a read-only transaction so the mapper executes inside the same JPA session as `findById`. We add `@Transactional(readOnly = true)` to both beans for consistency, even though `ChefEntity` itself has no lazy relations.

- [ ] **Step 1: Create `ChefServiceBean.java`**

```java
package en.sd.service;

import en.sd.entity.ChefEntity;
import en.sd.mapper.ChefMapper;
import en.sd.model.domain.Chef;
import en.sd.model.exception.DataNotFoundException;
import en.sd.model.exception.ExceptionCode;
import en.sd.repository.ChefRepository;
import en.sd.service.persistence.ChefService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChefServiceBean implements ChefService {

    private final ChefRepository chefRepository;
    private final ChefMapper chefMapper;

    @Override
    @Transactional(readOnly = true)
    public Chef getById(UUID id) {
        ChefEntity entity = chefRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, id));
        return chefMapper.toDomain(entity);
    }
}
```

- [ ] **Step 2: Create `OrderServiceBean.java`**

```java
package en.sd.service;

import en.sd.entity.OrderEntity;
import en.sd.mapper.OrderMapper;
import en.sd.model.domain.Order;
import en.sd.model.exception.DataNotFoundException;
import en.sd.model.exception.ExceptionCode;
import en.sd.repository.OrderRepository;
import en.sd.service.persistence.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceBean implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public Order getById(UUID id) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.ORDER_NOT_FOUND, id));
        return orderMapper.toDomain(entity);
    }
}
```

- [ ] **Step 3: Compile up to persistence-adapter**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail
./mvnw -q -pl persistence-adapter -am -DskipTests clean compile
```

Expected: BUILD SUCCESS.

- [ ] **Step 4: Commit**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
git add chef-mgmt-mail/persistence-adapter/
git commit -m "feat(mail/persistence): add read-only Chef/Order entities + repos + service beans"
```

---

### Task D5: Mail adapter — Thymeleaf dependency

**Files:**
- Modify: `chef-mgmt-mail/mail-adapter/pom.xml`

- [ ] **Step 1: Add Thymeleaf to mail-adapter dependencies**

After the existing `spring-boot-starter-mail` dependency block, add:

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
```

The `commons-io` dependency on lines 33–36 of the original `mail-adapter/pom.xml` was used by the deleted `MailUtils` class — remove it:

Delete lines 33–36 (the `commons-io` `<dependency>` block).

---

### Task D6: Mail adapter — `MailCreationServiceBean` (Thymeleaf)

**Files:**
- Create: `chef-mgmt-mail/mail-adapter/src/main/java/en/sd/service/MailCreationServiceBean.java`

- [ ] **Step 1: Create the file**

```java
package en.sd.service;

import en.sd.model.domain.Chef;
import en.sd.model.domain.Order;
import en.sd.service.mail.MailCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class MailCreationServiceBean implements MailCreationService {

    private static final String ORDER_PLACED_TEMPLATE = "order-placed";
    private static final String CHEF_VAR = "chef";
    private static final String ORDER_VAR = "order";

    private final SpringTemplateEngine templateEngine;

    @Override
    public String renderOrderPlaced(Chef chef, Order order) {
        Context context = new Context();
        context.setVariable(CHEF_VAR, chef);
        context.setVariable(ORDER_VAR, order);
        return templateEngine.process(ORDER_PLACED_TEMPLATE, context);
    }
}
```

**Note:** the `org.thymeleaf.spring6.SpringTemplateEngine` package name is unchanged in Spring Boot 4 / Spring 7 (Thymeleaf still ships its `spring6` integration module against Spring 7 — they kept the artifact name for compat).

---

### Task D7: Mail adapter — `MailSenderServiceBean` (HTML, configurable from-address)

**Files:**
- Create: `chef-mgmt-mail/mail-adapter/src/main/java/en/sd/service/MailSenderServiceBean.java`

- [ ] **Step 1: Create the file**

```java
package en.sd.service;

import en.sd.model.mail.SendingStatus;
import en.sd.service.mail.MailSenderService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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

---

### Task D8: Mail adapter — Thymeleaf template

**Files:**
- Create: `chef-mgmt-mail/mail-adapter/src/main/resources/templates/order-placed.html`

- [ ] **Step 1: Create the template**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="en">
<head>
    <meta charset="UTF-8">
    <title>New order</title>
</head>
<body style="font-family: Arial, sans-serif; background:#f4f4f4; padding:24px; margin:0;">
<div style="max-width:600px;margin:0 auto;background:#ffffff;padding:24px;border-radius:8px;">
    <h2 style="color:#2c3e50;margin-top:0;">
        Hey <span th:text="${chef.name()}">Chef</span>, you have a new order!
    </h2>

    <h3 style="margin-top:24px;color:#34495e;">Chef details</h3>
    <ul style="padding-left:20px;">
        <li><strong>Name:</strong> <span th:text="${chef.name()}"></span></li>
        <li><strong>Email:</strong> <span th:text="${chef.email()}"></span></li>
    </ul>

    <h3 style="margin-top:24px;color:#34495e;">Order details</h3>
    <ul style="padding-left:20px;">
        <li><strong>Order ID:</strong> <span th:text="${order.id()}"></span></li>
        <li><strong>Item:</strong> <span th:text="${order.itemName()}"></span></li>
        <li><strong>Price:</strong> <span th:text="${order.totalPrice()}"></span></li>
        <li>
            <strong>Placed at:</strong>
            <span th:text="${#temporals.format(order.orderedAt(), 'yyyy-MM-dd HH:mm')}"></span>
        </li>
    </ul>

    <p style="margin-top:24px;color:#7f8c8d;font-size:12px;">
        This is an automated message from chef-mgmt. Please do not reply.
    </p>
</div>
</body>
</html>
```

- [ ] **Step 2: Compile mail-adapter**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail
./mvnw -q -pl mail-adapter -am -DskipTests clean compile
```

Expected: BUILD SUCCESS.

- [ ] **Step 3: Commit**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
git add chef-mgmt-mail/mail-adapter/
git commit -m "feat(mail/mail-adapter): Thymeleaf order-placed template + HTML sender"
```

---

### Task D9: Core-impl — `OrderMailServiceBean` facade

**Files:**
- Create: `chef-mgmt-mail/core-impl/src/main/java/en/sd/service/OrderMailServiceBean.java`

- [ ] **Step 1: Create the file**

```java
package en.sd.service;

import en.sd.model.domain.Chef;
import en.sd.model.domain.Order;
import en.sd.model.mail.OrderMailResult;
import en.sd.model.mail.SendingStatus;
import en.sd.service.mail.MailCreationService;
import en.sd.service.mail.MailSenderService;
import en.sd.service.mail.OrderMailService;
import en.sd.service.persistence.ChefService;
import en.sd.service.persistence.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

- [ ] **Step 2: Compile core-impl**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail
./mvnw -q -pl core-impl -am -DskipTests clean compile
```

Expected: BUILD SUCCESS.

- [ ] **Step 3: Commit**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
git add chef-mgmt-mail/core-impl/
git commit -m "feat(mail/core-impl): OrderMailServiceBean facade over chef/order/render/send ports"
```

---

### Task D10: Rest adapter — request/response DTOs

**Files:**
- Create: `chef-mgmt-mail/rest-adapter/src/main/java/en/sd/model/mail/SendOrderMailRequestDTO.java`
- Create: `chef-mgmt-mail/rest-adapter/src/main/java/en/sd/model/mail/SendOrderMailResponseDTO.java`

- [ ] **Step 1: Create `SendOrderMailRequestDTO.java`**

```java
package en.sd.model.mail;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SendOrderMailRequestDTO(
        @NotNull UUID chefId,
        @NotNull UUID orderId
) {
}
```

- [ ] **Step 2: Create `SendOrderMailResponseDTO.java`**

```java
package en.sd.model.mail;

import java.util.UUID;

public record SendOrderMailResponseDTO(UUID id, String to, String status) {
}
```

---

### Task D11: Rest adapter — result mapper

**Files:**
- Create: `chef-mgmt-mail/rest-adapter/src/main/java/en/sd/model/mapper/OrderMailResultMapper.java`

- [ ] **Step 1: Create the mapper**

```java
package en.sd.model.mapper;

import en.sd.model.mail.OrderMailResult;
import en.sd.model.mail.SendOrderMailResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMailResultMapper {

    SendOrderMailResponseDTO toResponse(OrderMailResult result);
}
```

(MapStruct converts the `SendingStatus` enum to a String automatically via `.name()` because the target field type is `String` and the source is an enum.)

---

### Task D12: Rest adapter — controller interface and bean

**Files:**
- Create: `chef-mgmt-mail/rest-adapter/src/main/java/en/sd/controller/mail/OrderMailController.java`
- Create: `chef-mgmt-mail/rest-adapter/src/main/java/en/sd/controller/mail/OrderMailControllerBean.java`

- [ ] **Step 1: Create `OrderMailController.java`**

```java
package en.sd.controller.mail;

import en.sd.model.exception.ExceptionBody;
import en.sd.model.mail.SendOrderMailRequestDTO;
import en.sd.model.mail.SendOrderMailResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/v1/mails/orders")
@Tag(name = "Order Mail", description = "Send the order-placed email to the chef who received an order")
public interface OrderMailController {

    @PostMapping
    @Operation(summary = "Send order-placed mail", description = "Looks up the chef and order, renders a Thymeleaf email, and dispatches it via SMTP.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mail successfully dispatched",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SendOrderMailResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionBody.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionBody.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    SendOrderMailResponseDTO send(@Valid @RequestBody SendOrderMailRequestDTO request);
}
```

- [ ] **Step 2: Create `OrderMailControllerBean.java`**

```java
package en.sd.controller.mail;

import en.sd.model.mail.OrderMailResult;
import en.sd.model.mail.SendOrderMailRequestDTO;
import en.sd.model.mail.SendOrderMailResponseDTO;
import en.sd.model.mapper.OrderMailResultMapper;
import en.sd.service.mail.OrderMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

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

- [ ] **Step 3: Compile rest-adapter**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail
./mvnw -q -pl rest-adapter -am -DskipTests clean compile
```

Expected: BUILD SUCCESS.

- [ ] **Step 4: Whole-project compile sanity**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail
./mvnw -q -DskipTests clean compile
```

Expected: BUILD SUCCESS for **all six modules**. The mail service is structurally complete.

- [ ] **Step 5: Commit**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
git add chef-mgmt-mail/rest-adapter/
git commit -m "feat(mail/rest-adapter): order-placed controller, DTOs, response mapper"
```

---

## Phase E — Configuration

### Task E1: Update mail's `application.yaml`

**Files:**
- Modify: `chef-mgmt-mail/application/src/main/resources/application.yaml`

- [ ] **Step 1: Replace the file content**

Replace the entire content of `chef-mgmt-mail/application/src/main/resources/application.yaml` with:

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

---

### Task E2: Add `application-local.yaml.example` (committed)

**Files:**
- Create: `chef-mgmt-mail/application/src/main/resources/application-local.yaml.example`

- [ ] **Step 1: Create the file**

```yaml
# Copy this file to application-local.yaml (gitignored) and fill in real
# Mailtrap credentials from https://mailtrap.io/inboxes -> SMTP Settings.
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

---

### Task E3: Create real `application-local.yaml` (gitignored, for the developer's machine)

**Files:**
- Create: `chef-mgmt-mail/application/src/main/resources/application-local.yaml`

This file is gitignored (`.gitignore` from Task A1), so it will not be committed. Create a placeholder copy locally so the application can boot without exploding on missing config.

- [ ] **Step 1: Copy the example file**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail/application/src/main/resources
cp application-local.yaml.example application-local.yaml
```

- [ ] **Step 2: Tell the user (the human) to fill in real Mailtrap creds**

Print to the console:

> ⚠ ACTION REQUIRED: open `chef-mgmt-mail/application/src/main/resources/application-local.yaml` and replace `<your-mailtrap-username>` / `<your-mailtrap-password>` with the real credentials shown in the Mailtrap UI for your sandbox inbox. Do not commit this file.

- [ ] **Step 3: Verify gitignore is working**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
git status chef-mgmt-mail/application/src/main/resources/
```

Expected: `application-local.yaml.example` is shown as a new file; `application-local.yaml` is NOT shown (because `.gitignore` excludes it).

- [ ] **Step 4: Commit (only the example, the active config, and the .gitignore is already committed in A1)**

```bash
git add chef-mgmt-mail/application/src/main/resources/application.yaml
git add chef-mgmt-mail/application/src/main/resources/application-local.yaml.example
git commit -m "chore(mail): local-profile config + Mailtrap example, ddl-auto: validate"
```

---

### Task E4: Verify mail service boots end-to-end

- [ ] **Step 1: Make sure Postgres is up and `chef_mgmt_db` exists with the `CHEFS` and `ORDERS` tables (created by chef-mgmt-backend on its first run)**

Quick check:
```bash
psql -h localhost -U postgres -d chef_mgmt_db -c '\dt'
```

Expected output includes `CHEFS` and `ORDERS` (case may vary depending on how Hibernate quoted them).

If the tables aren't there: start `chef-mgmt-backend` once first to let it create them via `ddl-auto: update`, then stop it.

- [ ] **Step 2: Boot the mail service**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail
./mvnw -q -pl application -am spring-boot:run
```

Expected: log line `Started ChefMgmtMailApplication in N seconds`. No `SchemaManagementException` (would mean `ddl-auto: validate` couldn't find a table).

- [ ] **Step 3: Hit the new endpoint with a placeholder UUID to confirm 404/500 routing works (no actual chef yet)**

In another terminal:
```bash
curl -i -X POST http://localhost:8888/api/v1/mails/orders \
    -H 'Content-Type: application/json' \
    -d '{"chefId":"00000000-0000-0000-0000-000000000000","orderId":"00000000-0000-0000-0000-000000000000"}'
```

Expected: HTTP 500 with a JSON `ExceptionBody` whose `code` is `ERR_3001` (CHEF_NOT_FOUND). This proves: routing works, validation works, the facade reaches the persistence layer, and the global exception handler translates the domain exception. Mail will be tested end-to-end at the very end.

- [ ] **Step 4: Stop the mail service** (Ctrl-C).

---

## Phase F — Backend client

### Task F1: Add the HTTP client interface and DTOs in chef-mgmt-backend

**Files:**
- Create: `chef-mgmt-backend/src/main/java/en/sd/chefmgmt/service/mailing/client/SendOrderMailRequestDTO.java`
- Create: `chef-mgmt-backend/src/main/java/en/sd/chefmgmt/service/mailing/client/SendOrderMailResponseDTO.java`
- Create: `chef-mgmt-backend/src/main/java/en/sd/chefmgmt/service/mailing/client/MailClient.java`

- [ ] **Step 1: Create `SendOrderMailRequestDTO.java`**

```java
package en.sd.chefmgmt.service.mailing.client;

import java.util.UUID;

public record SendOrderMailRequestDTO(UUID chefId, UUID orderId) {
}
```

- [ ] **Step 2: Create `SendOrderMailResponseDTO.java`**

```java
package en.sd.chefmgmt.service.mailing.client;

import java.util.UUID;

public record SendOrderMailResponseDTO(UUID id, String to, String status) {
}
```

- [ ] **Step 3: Create `MailClient.java`**

```java
package en.sd.chefmgmt.service.mailing.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/v1/mails")
public interface MailClient {

    @PostExchange("/orders")
    SendOrderMailResponseDTO sendOrderMail(@RequestBody SendOrderMailRequestDTO request);
}
```

---

### Task F2: Add the `MailClientConfig` bean

**Files:**
- Create: `chef-mgmt-backend/src/main/java/en/sd/chefmgmt/config/MailClientConfig.java`

- [ ] **Step 1: Create the file**

```java
package en.sd.chefmgmt.config;

import en.sd.chefmgmt.service.mailing.client.MailClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

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

---

### Task F3: Add the request mapper

**Files:**
- Create: `chef-mgmt-backend/src/main/java/en/sd/chefmgmt/mapper/SendOrderMailRequestMapper.java`

- [ ] **Step 1: Create the file**

```java
package en.sd.chefmgmt.mapper;

import en.sd.chefmgmt.service.mailing.client.SendOrderMailRequestDTO;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface SendOrderMailRequestMapper {

    SendOrderMailRequestDTO toRequest(UUID chefId, UUID orderId);
}
```

(MapStruct auto-maps multi-source primitives to record components by parameter name when the names match — here `chefId` → `chefId`, `orderId` → `orderId`. No `@Mapping` needed.)

---

### Task F4: Add the base-URL property to backend's `application.yaml`

**Files:**
- Modify: `chef-mgmt-backend/src/main/resources/application.yaml`

- [ ] **Step 1: Append a `chef-mgmt-mail:` block at the end of the file**

After the existing `app:` block (which ends at line 26 with `allowed-urls: ...`), add:

```yaml
chef-mgmt-mail:
  base-url: http://localhost:8888/api
```

- [ ] **Step 2: Compile the backend**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-backend
./mvnw -q -DskipTests clean compile
```

Expected: BUILD SUCCESS.

- [ ] **Step 3: Commit**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
git add chef-mgmt-backend/src/main/java/en/sd/chefmgmt/service/mailing/
git add chef-mgmt-backend/src/main/java/en/sd/chefmgmt/config/MailClientConfig.java
git add chef-mgmt-backend/src/main/java/en/sd/chefmgmt/mapper/SendOrderMailRequestMapper.java
git add chef-mgmt-backend/src/main/resources/application.yaml
git commit -m "feat(backend): RestClient + @HttpExchange MailClient for chef-mgmt-mail"
```

---

### Task F5: Hook into `OrderServiceBean.save()`

**Files:**
- Modify: `chef-mgmt-backend/src/main/java/en/sd/chefmgmt/service/order/OrderServiceBean.java`

- [ ] **Step 1: Add the new imports**

In `chef-mgmt-backend/src/main/java/en/sd/chefmgmt/service/order/OrderServiceBean.java`, after the existing imports (around line 21), add:

```java
import en.sd.chefmgmt.mapper.SendOrderMailRequestMapper;
import en.sd.chefmgmt.service.mailing.client.MailClient;
import en.sd.chefmgmt.service.mailing.client.SendOrderMailRequestDTO;
import en.sd.chefmgmt.service.mailing.client.SendOrderMailResponseDTO;
import lombok.extern.slf4j.Slf4j;
```

- [ ] **Step 2: Add `@Slf4j` to the class**

Above the existing `@Service` annotation on the `OrderServiceBean` class, add `@Slf4j` on its own line:

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceBean implements OrderService {
```

- [ ] **Step 3: Add two new injected dependencies**

After the existing `private final OrderMapper orderMapper;` field, add:

```java
    private final MailClient mailClient;
    private final SendOrderMailRequestMapper sendOrderMailRequestMapper;
```

- [ ] **Step 4: Replace the `save` method body**

Find the existing `save` method (lines 77–87 in the original file) and replace its body with the new one. The full replacement method:

```java
    @Override
    @Transactional
    public OrderResponseDTO save(UUID chefId, OrderRequestDTO orderRequestDTO) {
        ChefEntity chefEntity = chefRepository.findById(chefId)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, chefId));

        OrderEntity toAdd = orderMapper.convertRequestDtoToEntity(orderRequestDTO, chefEntity);
        OrderEntity added = orderRepository.save(toAdd);
        OrderResponseDTO response = orderMapper.convertEntityToResponseDto(added);

        SendOrderMailRequestDTO mailRequest = sendOrderMailRequestMapper.toRequest(chefEntity.getId(), added.getId());
        try {
            SendOrderMailResponseDTO mailResponse = mailClient.sendOrderMail(mailRequest);
            log.info("Order mail sent: order={} to={} status={}", added.getId(), mailResponse.to(), mailResponse.status());
        } catch (Exception e) {
            log.error("Order mail failed: order={} chef={}: {}", added.getId(), chefEntity.getId(), e.getMessage());
        }

        return response;
    }
```

- [ ] **Step 5: Compile**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-backend
./mvnw -q -DskipTests clean compile
```

Expected: BUILD SUCCESS.

- [ ] **Step 6: Commit**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
git add chef-mgmt-backend/src/main/java/en/sd/chefmgmt/service/order/OrderServiceBean.java
git commit -m "feat(backend): trigger order-placed mail on OrderServiceBean.save"
```

---

## Phase G — End-to-end verification

### Task G1: Run both services and exercise the full path

**Pre-flight:** Make sure `application-local.yaml` (gitignored) has been filled in with real Mailtrap credentials (Task E3 step 2).

- [ ] **Step 1: Start `chef-mgmt-backend` on port 8777**

In terminal 1:
```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-backend
./mvnw -q spring-boot:run
```

Expected: `Started ChefMgmtApplication in N seconds`.

- [ ] **Step 2: Start `chef-mgmt-mail` on port 8888**

In terminal 2:
```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail
./mvnw -q -pl application -am spring-boot:run
```

Expected: `Started ChefMgmtMailApplication`. No schema-validation error (would mean `CHEFS`/`ORDERS` shape drifted from `ChefEntity`/`OrderEntity`).

- [ ] **Step 3: Authenticate with the backend (it has Spring Security)**

Login using the existing auth endpoint (find a known user, e.g. via the `users` table):
```bash
curl -s -X POST http://localhost:8777/api/auth/v1/login \
    -H 'Content-Type: application/json' \
    -d '{"email":"<your-test-user-email>","password":"<password>"}'
```

Capture the JWT from the response. Set it as a shell variable:
```bash
TOKEN="<the-jwt-from-above>"
```

- [ ] **Step 4: Find a chef with a real test email address**

```bash
curl -s -H "Authorization: Bearer $TOKEN" \
    'http://localhost:8777/api/chefs/v1?pageNumber=0&pageSize=10'
```

Pick a chef whose email is one you can monitor in Mailtrap (or update one row in the DB to point to your sandbox-monitored address). Capture its `id`:
```bash
CHEF_ID="<chef-uuid>"
```

- [ ] **Step 5: POST a new order for that chef**

```bash
curl -i -X POST "http://localhost:8777/api/chefs/v1/$CHEF_ID/orders" \
    -H "Authorization: Bearer $TOKEN" \
    -H 'Content-Type: application/json' \
    -d '{"itemName":"Test pizza","totalPrice":42.0}'
```

(Adjust the body to whatever `OrderRequestDTO` requires — see `chef-mgmt-backend/src/main/java/en/sd/chefmgmt/dto/order/OrderRequestDTO.java` if the fields differ.)

Expected: 200/201 with a JSON body containing the new order's id. In terminal 1 (backend) you should see:
```
Order mail sent: order=<order-uuid> to=<chef-email> status=SUCCESS
```

In terminal 2 (mail) you should see:
```
Order mail dispatched: id=<correlation-uuid> order=<order-uuid> to=<chef-email> status=SUCCESS
```

- [ ] **Step 6: Verify the email landed in Mailtrap**

Open the Mailtrap UI for your sandbox inbox. The new email should be present:
- From: `noreply@chefmgmt.com`
- Subject: `You have a new order`
- Body: Thymeleaf-rendered HTML with the chef's name + email and the order's id, item name, price, and formatted timestamp.

- [ ] **Step 7: Verify the failure-tolerant path**

Stop `chef-mgmt-mail` (Ctrl-C in terminal 2). Repeat step 5. Expected:
- Order is still saved (200/201 with a body).
- Backend logs `Order mail failed: order=<uuid> chef=<uuid>: ...`.
- No exception propagates to the HTTP response.

This proves the Section §2 non-goal "mail-service down ⇒ order still saves" holds.

---

### Task G2: Final tidy

- [ ] **Step 1: Run a full clean compile of both projects to make sure nothing is left in a broken state**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-mail
./mvnw -q -DskipTests clean compile
```

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj/chef-mgmt-backend
./mvnw -q -DskipTests clean compile
```

Expected: BUILD SUCCESS for both.

- [ ] **Step 2: Confirm git status is clean**

```bash
cd /Users/sergiublaj/Documents/SD2026/sergiublaj
git status
```

Expected: clean tree (or only the pre-existing `chef-mgmt-backend/.idea/workspace.xml` and `.DS_Store` which are unrelated). `application-local.yaml` should NOT appear.

- [ ] **Step 3: Confirm the lab09 branch's commits read as a coherent story**

```bash
git log --oneline lab09 -20
```

Expected (top → bottom): the 7 new commits from this plan plus the spec commit (`657b8de`) and the prior lab09 commits. Each new commit message should clearly describe what it did.

---

## Acceptance Checklist

Tied to spec §7.2:

- [ ] `chef-mgmt-mail` builds and starts on Spring Boot 4.0.3 / Java 24.
- [ ] `Role`, `UserEntity`, `UserRepository`, `UserService(Bean)` are gone from `persistence-adapter` and `core-api`.
- [ ] Old generic mail flow (`MailService`, `MailServiceBean`, `MailRequest`, `MailResponse`, `MailController(Bean)`, all old DTOs and DTO mappers, `MailUtils`, `email.html`) is gone.
- [ ] `POST /api/v1/mails/orders` with body `{ "chefId": "...", "orderId": "..." }` returns `201 Created` and dispatches a Mailtrap delivery.
- [ ] Saving an order in `chef-mgmt-backend` triggers exactly one mail send; mail-service down ⇒ order still saves and backend logs the failure.
- [ ] Email body is Thymeleaf-rendered (no placeholder substitution leftover) and contains chef name, chef email, order id/item/price/timestamp, and a "Hey <chef>, you have a new order!" greeting.
- [ ] `application-local.yaml` is gitignored; `application-local.yaml.example` is committed.
- [ ] `git log lab09` tells a clean story: track → bump platform → delete dead code → add domain → add adapters → add facade → add controller → config → backend client → backend hook.
