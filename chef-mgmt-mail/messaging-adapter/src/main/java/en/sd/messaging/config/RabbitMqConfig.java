package en.sd.messaging.config;

import org.springframework.amqp.support.converter.DefaultJacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper.TypePrecedence;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    // JSON converter for incoming order events. Bigger than the publisher's because
    // deserialization has to choose a target class:
    //   - The publisher stamps __TypeId__ with its FQN (en.sd.chefmgmt.event.OrderCreatedEvent),
    //     which doesn't exist here (our class lives in en.sd.messaging.event).
    //   - setTypePrecedence(INFERRED) ignores that header and uses the type declared in
    //     the @RabbitListener method signature instead.
    //   - setTrustedPackages("*") permits any package (default would block unknown ones).
    @Bean
    public MessageConverter jsonMessageConverter() {
        DefaultJacksonJavaTypeMapper typeMapper = new DefaultJacksonJavaTypeMapper();
        typeMapper.setTrustedPackages("*");
        typeMapper.setTypePrecedence(TypePrecedence.INFERRED);

        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
