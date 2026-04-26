package en.sd.chefmgmt.config;

import en.sd.chefmgmt.service.mail.OrderMailService;
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

    @Override
    public void setEmbeddedValueResolver(@NonNull StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Bean
    public OrderMailService mailService(@Value("${chef-mgmt-mail.base-url}") String baseUrl) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(RestClient.builder().baseUrl(baseUrl).build()))
                .embeddedValueResolver(embeddedValueResolver)
                .build()
                .createClient(OrderMailService.class);
    }
}
