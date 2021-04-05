package com.customermanagement.circuitbreaker.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

@Slf4j
@Component
@Profile("local")
public class MockCustomerService {
    @Value("${customer-feign.base.url}")
    private String baseUrl;

    @Value("${customer-feign.getAllCustomers.url}")
    private String basePath;

    private WireMockServer wireMockServer;

    public void init() throws MalformedURLException{
        configureMockServer();
        simulate();
    }



    private void simulate() {
        simulateScenario(HttpStatus.SC_OK, expectedResponse());
    }

    private void simulateScenario(int httpStatus, String response){
        WireMock.stubFor(WireMock
                .get(WireMock.urlPathEqualTo(basePath))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
                //.withHeader(RequestInterceptor.HEADER_X_CORRELATION_ID, WireMock.matching(".*"))

                .willReturn(WireMock.aResponse()
                .withStatus(httpStatus)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(response)));
    }

    private void configureMockServer() throws MalformedURLException {
        URL url = new URL(baseUrl);
        wireMockServer = new WireMockServer(url.getPort());
        wireMockServer.start();
        WireMock.configureFor(url.getHost(), wireMockServer.port());
    }

    @PreDestroy
    public void destroy(){
        if(Objects.nonNull(wireMockServer)){
            wireMockServer.shutdown();
        }
    }

    private String expectedResponse() {
        return "";
    }
}
