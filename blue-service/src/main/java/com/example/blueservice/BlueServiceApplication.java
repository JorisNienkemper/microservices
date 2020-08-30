package com.example.blueservice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class BlueServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlueServiceApplication.class, args);
    }

}

@RestController
class BlueServiceController {
    private final DiscoveryClient discoveryClient;
    @Value("${random.value}")
    private String district;

    private final WebClient.Builder builder;

    public BlueServiceController(DiscoveryClient discoveryClient, WebClient.Builder builder) {
        this.discoveryClient = discoveryClient;

        this.builder = builder;
    }

    @GetMapping(path = "/instances")
    List<?> getInstances() {
        return discoveryClient.getServices().stream()
                .map(discoveryClient::getInstances)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/votes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CircuitBreaker(fallbackMethod = "emptyVote", name = "default")
    public Flux<Vote> getVotes() {
        return builder.build().get()
                .uri("http://voter/random-numbers")
                .retrieve()
                .bodyToFlux(Integer.class)
                .map(count -> new Vote.VoteBuilder()
                        .count(count)
                        .district(district)
                        .unavailableMessage("")
                        .build()
                )
                ;
    }

    @GetMapping(value = "/empty-vote", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Vote> emptyVote (Exception e) {
        return Flux.just(Vote.builder()
                .count(0)
                .district(district)
                .unavailableMessage("no data available")
                .build());
    }
}

@Data
@Builder
class Vote {
    private Integer count;
    private String district;
    private String unavailableMessage;
}

@Configuration
class Config {
    @Bean
    @LoadBalanced
    WebClient.Builder builder() {
        return WebClient.builder();
    }
}