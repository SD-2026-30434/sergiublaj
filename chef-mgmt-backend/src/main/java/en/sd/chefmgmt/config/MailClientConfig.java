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
