package org.springframework.test.web.reactive.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.reactive.client.match.ReactiveRequestMatchers.content;
import static org.springframework.test.web.reactive.client.match.ReactiveRequestMatchers.method;
import static org.springframework.test.web.reactive.client.response.ReactiveResponseCreators.withSuccess;

public class MockRestServiceServerTest {
	@Test
	void shouldWork() {
		final MockRestServiceServer restServiceServer = new MockRestServiceServer();

		final WebClient client = WebClient.builder()
				.clientConnector(restServiceServer.clientHttpConnector())
				.build();

		restServiceServer.expectRequest(ExpectedCount.times(2), method(POST))
				.andExpect(content().string("foo"))
				.andRespond(withSuccess("bar", MediaType.TEXT_PLAIN));

		makeRequestAndVerify(client);
		makeRequestAndVerify(client);
	}

	private void makeRequestAndVerify(WebClient client) {
		TestPublisher<String> publisher = TestPublisher.create();

		Mono<ResponseEntity<String>> entityMono = client.post()
				.uri("https://test-uri.org/web-client")
				.accept(MediaType.TEXT_PLAIN)
				.contentType(MediaType.TEXT_PLAIN)
				.body(publisher, String.class)
				.retrieve()
				.toEntity(String.class);

		StepVerifier.create(entityMono)
				.then(() -> publisher.next("foo").complete())
				.assertNext(responseEntity -> {
					assertThat(responseEntity.getStatusCode())
							.isEqualTo(HttpStatus.OK);

					assertThat(responseEntity.getBody())
							.isEqualTo("bar");
				})
				.verifyComplete();
	}
}
