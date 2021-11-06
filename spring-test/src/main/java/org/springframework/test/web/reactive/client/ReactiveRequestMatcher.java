package org.springframework.test.web.reactive.client;

import org.springframework.http.client.reactive.ClientHttpRequest;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ReactiveRequestMatcher {
	Mono<Boolean> matchRequest(ClientHttpRequest request);
}
