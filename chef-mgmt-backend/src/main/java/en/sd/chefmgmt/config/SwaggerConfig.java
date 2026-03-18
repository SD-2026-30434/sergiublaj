package en.sd.chefmgmt.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "chef-mgmt API",
        version = "1.0",
        description = "chef-mgmt app endpoints"
))
public class SwaggerConfig { }