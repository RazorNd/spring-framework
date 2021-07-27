package org.springframework.test.web.reactive.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

public class MockRestServiceServerTest {
	@Test
	void shouldWork() {
		final MockRestServiceServer restServiceServer = new MockRestServiceServer();

		final WebClient client = WebClient.builder()
				.clientConnector(restServiceServer.clientHttpConnector())
				.build();


		final ResponseEntity<String> responseEntity = client.post()
				.uri("https://test-uri.org/web-client")
				.accept(MediaType.TEXT_PLAIN)
				.contentType(MediaType.TEXT_PLAIN)
				.bodyValue("Hello")
				.retrieve()
				.toEntity(String.class)
				.block();

		assertThat(responseEntity.getStatusCode())
				.isEqualTo(HttpStatus.OK);

		assertThat(responseEntity.getBody())
				.isEqualTo("world");
	}
}
