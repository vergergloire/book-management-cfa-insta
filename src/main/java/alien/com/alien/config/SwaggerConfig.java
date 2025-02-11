package alien.com.alien.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Alien API")
                        .version("1.0")
                        .description("Moteur de recherche avancée pour les livre")
                        .contact(new Contact()
                                .name("Verger MOUSSAVOU")
                                .email("demain.moussavou@gmail.com")
                                .url("https://github.com/repo")));
    }
}

