package com.example.votegenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;
import java.util.function.Function;

@SpringBootApplication
public class VoteGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoteGeneratorApplication.class, args);
	}
}


@RestController
class VoteController {
	Random random = new Random();


	@GetMapping(path ="/random-numbers", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Integer> getRandomNumbers() {
		return Flux.interval(Duration.ofSeconds(3)).map(i -> random.nextInt(100));
	}
}