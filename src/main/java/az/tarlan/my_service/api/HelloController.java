package az.tarlan.my_service.api;

import az.tarlan.my_service.config.AppProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    private final AppProperties properties;

    public HelloController(AppProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/hello")
    public HelloResponse hello() {
        return new HelloResponse(
                properties.greeting(),
                properties.environment(),
                System.getenv().getOrDefault("HOSTNAME", "unknown")
        );
    }
    public record HelloResponse(String message, String environment, String host){}

}

