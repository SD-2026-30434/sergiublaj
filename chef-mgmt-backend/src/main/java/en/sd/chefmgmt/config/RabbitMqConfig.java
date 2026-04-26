package en.sd.chefmgmt.config;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    // Replaces Spring Boot's default SimpleMessageConverter (Java serialization,
    // requires Serializable + matching FQN on both sides) with a JSON converter
    // so we can publish OrderCreatedEvent as JSON over RabbitMQ.
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
