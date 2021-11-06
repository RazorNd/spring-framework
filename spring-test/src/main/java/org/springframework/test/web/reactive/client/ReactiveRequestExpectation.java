package org.springframework.test.web.reactive.client;

import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.test.web.client.ExpectedCount;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

final class ReactiveRequestExpectation {

	private final List<ReactiveRequestMatcher> requestMatchers;
	private final ReactiveResponseCreator responseCreator;
	private final RequestCount requestCount;

	ReactiveRequestExpectation(ExpectedCount expectedCount,
							   List<ReactiveRequestMatcher> requestMatchers,
							   ReactiveResponseCreator responseCreator) {
		this.requestCount = new RequestCount(expectedCount);
		this.requestMatchers = requestMatchers;
		this.responseCreator = responseCreator;
	}


	Mono<ClientHttpResponse> tryResponse(ClientHttpRequest request) {
		return Flux.fromIterable(requestMatchers)
				.flatMap(matcher -> matcher.matchRequest(request))
				.all(Boolean::booleanValue)
				.filter(Boolean::booleanValue)
				.filter(t -> requestCount.tryIncrement())
				.flatMap(t -> responseCreator.createResponse(request));
	}


	static class RequestCount {
		private final ExpectedCount expectedCount;

		private int matchedRequestCount = 0;

		RequestCount(ExpectedCount expectedCount) {
			this.expectedCount = expectedCount;
		}

		boolean tryIncrement() {
			int newValue = matchedRequestCount + 1;
			if (newValue > expectedCount.getMaxCount()) {
				return false;
			}
			matchedRequestCount = newValue;
			return true;
		}
	}

}
