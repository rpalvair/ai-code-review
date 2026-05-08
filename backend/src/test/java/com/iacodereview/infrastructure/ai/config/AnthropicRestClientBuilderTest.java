package com.iacodereview.infrastructure.ai.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AnthropicRestClientBuilderTest {

    @Test
    void restClient_returnConfiguredRestClient() {
        var restClientBuilder = RestClient.builder();
        var mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        var properties = new AnthropicProperties("sk-ant-test", "http://localhost/v1/messages", "claude-test", "2023-06-01", 1024);
        var anthropicRestClientBuilder = new AnthropicRestClientBuilder(properties, restClientBuilder);
        final RestClient restClient = anthropicRestClientBuilder.restClient();

        mockServer.expect(requestTo("http://localhost/v1/messages"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("x-api-key", "sk-ant-test"))
                .andExpect(header("anthropic-version", "2023-06-01"))
                .andExpect(header("content-type", "application/json"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        restClient.post().body(Map.of()).retrieve().toBodilessEntity();

        mockServer.verify();
    }

}
