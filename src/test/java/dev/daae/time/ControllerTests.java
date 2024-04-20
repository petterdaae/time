package dev.daae.time;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void statusEndpointReturns200WithValidCredentials() throws Exception {
        this.mockMvc.perform(get("/").header("Authorization", ""))
                .andExpect(status().isOk());
    }

    @Test
    void statusEndpointReturns401WithoutCredentials() throws Exception {
        this.mockMvc.perform(get("/").header("Authorization", ""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void statusEndpointReturns403WithInvalidCredentials() throws Exception {
        this.mockMvc.perform(get("/").header("Authorization", ""))
                .andExpect(status().isForbidden());
    }
}
