package org.springframework.test.web.reactive.client.match;

import org.springframework.http.HttpMethod;
import org.springframework.test.web.reactive.client.ReactiveRequestMatcher;
import reactor.core.publisher.Mono;

public abstract class ReactiveRequestMatchers {

	public static ReactiveRequestMatcher anythig() {
		return request -> Mono.just(true);
	}

	public static ReactiveRequestMatcher method(HttpMethod method) {
		return request -> Mono.just(method.equals(request.getMethod()));
	}

	public static ContentRequestMatchers content() {
		return new ContentRequestMatchers();
	}

}
