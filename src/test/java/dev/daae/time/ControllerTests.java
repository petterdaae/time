package dev.daae.time;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.daae.time.models.CreateLogRequest;
import dev.daae.time.models.CreateLogResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LogRepository logRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void statusEndpointReturns200WithValidCredentials() throws Exception {
        this.mockMvc.perform(get("/status").with(httpBasic("username", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void statusEndpointReturns401WithoutCredentials() throws Exception {
        this.mockMvc.perform(get("/status")).andExpect(status().isUnauthorized());
    }

    @Test
    void statusEndpointReturns401WithInvalidCredentials() throws Exception {
        this.mockMvc.perform(get("/status").with(httpBasic("invalid", "credentials")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logEndpointCreatesLogInDatabase() throws Exception {
        var request = new CreateLogRequest("description");
        var result = this.mockMvc.perform(
                post("/log")
                        .with(csrf())
                        .with(httpBasic("username", "password"))
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        var response = mapper.readValue(result.getResponse().getContentAsString(), CreateLogResponse.class);
        var savedLog = logRepository.findById(response.id()).orElseThrow();
        assertThat(savedLog.getDescription()).isEqualTo(request.description());
    }
}
