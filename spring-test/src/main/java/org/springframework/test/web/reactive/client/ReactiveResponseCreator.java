package org.springframework.test.web.reactive.client;

import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ReactiveResponseCreator {
	Mono<ClientHttpResponse> createResponse(ClientHttpRequest request);
}
