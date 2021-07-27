package org.springframework.test.web.reactive.client;

import com.sun.tools.javac.util.List;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.mock.http.client.reactive.MockClientHttpRequest;
import org.springframework.mock.http.client.reactive.MockClientHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

public final class MockRestServiceServer {

	public final ClientHttpConnector clientHttpConnector() {
		return new MockClientHttpConnector();
	}

	private class MockClientHttpConnector implements ClientHttpConnector {

		@NotNull
		@Override
		public Mono<ClientHttpResponse> connect(@NotNull HttpMethod method,
												@NotNull URI uri,
												@NotNull
														Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {

			final MockClientHttpRequest request = new MockClientHttpRequest(method, uri);

			final List<Expectation> expectations = List.of(new Expectation("Hello", "world"),
														   new Expectation("foo", "bar"));


			return requestCallback.apply(request)
					.then(Mono.defer(() -> Flux.fromIterable(expectations)
							.flatMap(expectation -> expectation.expectRequest(request))
							.next()));
		}
	}

	private static class Expectation {
		private final String requestBody;

		private final String responseBody;

		Expectation(String requestBody, String responseBody) {
			this.requestBody = requestBody;
			this.responseBody = responseBody;
		}

		Mono<ClientHttpResponse> expectRequest(MockClientHttpRequest request) {
			return Mono.just(request)
					.filterWhen(this::matchRequest)
					.flatMap(this::createResponse);
		}

		private Publisher<Boolean> matchRequest(MockClientHttpRequest r) {
			return r.getBodyAsString().map(requestBody::equals);
		}

		private Mono<ClientHttpResponse> createResponse(MockClientHttpRequest request) {
			final MockClientHttpResponse response = new MockClientHttpResponse(HttpStatus.OK);

			response.setBody(responseBody);

			return Mono.just(response);
		}
	}
}
