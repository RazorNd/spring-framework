package org.springframework.test.web.reactive.client;


import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.test.web.client.ExpectedCount;
import reactor.core.publisher.Mono;

import static org.springframework.test.web.client.ExpectedCount.once;

public interface ReactiveRequestExpectationManager {

	default ExpectationBuilder expectRequest(ReactiveRequestMatcher matcher) {
		return expectRequest(once(), matcher);
	}

	ExpectationBuilder expectRequest(ExpectedCount count, ReactiveRequestMatcher matcher);

	Mono<ClientHttpResponse> validateRequest(ClientHttpRequest request);
}
