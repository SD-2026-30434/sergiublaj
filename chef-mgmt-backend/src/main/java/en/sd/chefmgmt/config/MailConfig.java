package en.sd.chefmgmt.config;

import en.sd.chefmgmt.service.mail.ChefMailService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class MailConfig implements EmbeddedValueResolverAware {

    private StringValueResolver embeddedValueResolver;

    /**
     * Captures Spring's {@link StringValueResolver} so it can later be passed to the
     * {@link HttpServiceProxyFactory}, enabling resolution of {@code ${...}} property
     * placeholders in {@link ChefMailService} annotations.
     */
    @Override
    public void setEmbeddedValueResolver(@NonNull StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    /**
     * Creates a {@link ChefMailService} HTTP client proxy that targets the mail service at the
     * configured {@code chef-mgmt-mail.base-url}. Uses Spring's {@link HttpServiceProxyFactory}
     * with a {@link RestClient}-backed adapter, and wires in the embedded value resolver so that
     * property placeholders in the {@code ChefMailService} interface annotations are resolved.
     */
    @Bean
    public ChefMailService chefMailService(@Value("${chef-mgmt-mail.base-url}") String baseUrl) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(RestClient.builder().baseUrl(baseUrl).build()))
                .embeddedValueResolver(embeddedValueResolver)
                .build()
                .createClient(ChefMailService.class);
    }
}
