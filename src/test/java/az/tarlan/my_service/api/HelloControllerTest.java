package az.tarlan.my_service.api;

import az.tarlan.my_service.config.AppProperties;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HelloControllerTest {

    private final MockMvc mvc = MockMvcBuilders
            .standaloneSetup(new HelloController(new AppProperties("Hi", "test")))
            .build();

    @Test
    void returnsGreetingAndEnvironment() throws Exception {
        mvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello"))
                .andExpect(jsonPath("$.environment").value("test"));
    }

}