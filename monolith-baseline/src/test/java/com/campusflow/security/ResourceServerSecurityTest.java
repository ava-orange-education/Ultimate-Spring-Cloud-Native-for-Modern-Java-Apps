package com.campusflow.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "oauth2"})
class ResourceServerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void getApiWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getApiWithoutReadScopeReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/students")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_other"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getApiWithReadScopeIsAuthorized() throws Exception {
        mockMvc.perform(get("/api/students")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_campusflow.read"))))
                .andExpect(status().isOk());
    }

    @Test
    void postApiWithOnlyReadScopeReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/students")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_campusflow.read")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void healthEndpointRemainsAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }
}
