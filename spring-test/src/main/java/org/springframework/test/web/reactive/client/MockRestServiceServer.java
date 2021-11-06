package org.springframework.test.web.reactive.client;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.mock.http.client.reactive.MockClientHttpRequest;
import org.springframework.test.web.client.ExpectedCount;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

public final class MockRestServiceServer {

	private ReactiveRequestExpectationManager expectationManager = new ReactiveUnorderedRequestExpectationManager();

	public ClientHttpConnector clientHttpConnector() {
		return new MockClientHttpConnector();
	}

	public ExpectationBuilder expectRequest(ReactiveRequestMatcher matcher) {
		return expectationManager.expectRequest(matcher);
	}

	public ExpectationBuilder expectRequest(ExpectedCount count, ReactiveRequestMatcher matcher) {
		return expectationManager.expectRequest(count, matcher);
	}

	private class MockClientHttpConnector implements ClientHttpConnector {

		@NotNull
		@Override
		public Mono<ClientHttpResponse> connect(@NotNull HttpMethod method,
												@NotNull URI uri,
												@NotNull Function<? super ClientHttpRequest, Mono<Void>>
														requestCallback) {

			final MockClientHttpRequest request = new MockClientHttpRequest(method, uri);


			return requestCallback.apply(request)
					.then(expectationManager.validateRequest(request));
		}
	}
}
