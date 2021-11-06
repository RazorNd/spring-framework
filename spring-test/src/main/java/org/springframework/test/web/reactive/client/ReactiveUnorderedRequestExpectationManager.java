package org.springframework.test.web.reactive.client;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.test.web.client.ExpectedCount;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.client.ExpectedCount.once;

public class ReactiveUnorderedRequestExpectationManager implements ReactiveRequestExpectationManager {

	private final List<ClientHttpRequest> requests = new ArrayList<>();
	private final List<Expectation> expectations = new ArrayList<>();


	@Override
	public ExpectationBuilder expectRequest(ExpectedCount count, ReactiveRequestMatcher matcher) {
		return new ExpectationBuilder(count, expectations::add, matcher);
	}

	@Override
	public Mono<ClientHttpResponse> validateRequest(ClientHttpRequest request) {
		return Flux.fromIterable(expectations)
				.flatMap(expectation -> expectation.tryResponse(request))
				.next()
				.doOnNext(res -> requests.add(request))
				.switchIfEmpty(Mono.error(() -> createUnexpectedRequestError(request)));
	}

	protected AssertionError createUnexpectedRequestError(ClientHttpRequest request) {
		HttpMethod method = request.getMethod();
		URI uri = request.getURI();
		String message = "No further requests expected: HTTP " + method + " " + uri + "\n";
		return new AssertionError(message + getRequestDetails());
	}

	/**
	 * Return details of executed requests.
	 */
	protected String getRequestDetails() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.requests.size()).append(" request(s) executed");
		if (!this.requests.isEmpty()) {
			sb.append(":\n");
			for (ClientHttpRequest request : this.requests) {
				sb.append(request.toString()).append('\n');
			}
		}
		else {
			sb.append(".\n");
		}
		return sb.toString();
	}
}
