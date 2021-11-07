package org.springframework.test.web.reactive.client.match;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.client.reactive.MockClientHttpRequest;
import org.springframework.test.web.reactive.client.ReactiveRequestMatcher;
import reactor.test.StepVerifier;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.HttpMethod.*;

class ReactiveRequestMatchersTests {

	private MockClientHttpRequest request = new MockClientHttpRequest(GET, "http://www.foo.example/bar/");

	@Test
	void anything() {
		verifyMatcher(ReactiveRequestMatchers.anything(), true);
	}

	@Test
	void method() {
		verifyMatcher(ReactiveRequestMatchers.method(GET), true);
		verifyMatcher(ReactiveRequestMatchers.method(POST), false);
		verifyMatcher(ReactiveRequestMatchers.method(PUT), false);
		verifyMatcher(ReactiveRequestMatchers.method(PATCH), false);
	}

	@Test
	void requestToHamcrestMatcher() {
		verifyMatcher(ReactiveRequestMatchers.requestTo(startsWith("http://www.foo.example")), true);
		verifyMatcher(ReactiveRequestMatchers.requestTo(startsWith("http://foo.example")), false);
		verifyMatcher(ReactiveRequestMatchers.requestTo(containsString("foo.example")), true);
		verifyMatcher(ReactiveRequestMatchers.requestTo(containsString("baz.example")), false);
	}

	@Test
	void requestToUriExpectation() throws URISyntaxException {
		verifyMatcher(ReactiveRequestMatchers.requestTo(new URI("http://www.foo.example/bar/")), true);
		verifyMatcher(ReactiveRequestMatchers.requestTo(new URI("http://www.foo.example/baz/")), false);
	}

	@Test
	void requestToStringExpectation() {
		verifyMatcher(ReactiveRequestMatchers.requestTo("http://www.foo.example/bar/"), true);
		verifyMatcher(ReactiveRequestMatchers.requestTo("http://www.foo.example/baz/"), false);
	}

	@Test
	void requestToUriTemplate() {
		verifyMatcher(ReactiveRequestMatchers.requestToUriTemplate("http://www.foo.example/{path}/", "bar"), true);
		verifyMatcher(ReactiveRequestMatchers.requestToUriTemplate("http://www.foo.example/{path}/", "baz"), false);
	}

	@Test
	void queryParam() {
		request = new MockClientHttpRequest(GET, "http://www.example/?foo=bar&foo=baz");
		verifyMatcher(ReactiveRequestMatchers.queryParam("foo", "bar", "baz"), true);
		verifyMatcher(ReactiveRequestMatchers.queryParam("foo", "bar"), false);
		verifyMatcher(ReactiveRequestMatchers.queryParam("foo", "baz", "bar"), false);
		verifyMatcher(ReactiveRequestMatchers.queryParam("example", "baz", "bar"), false);
	}

	@Test
	void header() {
		request.getHeaders().addAll("foo", Arrays.asList("bar", "baz"));
		verifyMatcher(ReactiveRequestMatchers.header("foo", "bar", "baz"), true);
		verifyMatcher(ReactiveRequestMatchers.header("foo", "bar"), false);
		verifyMatcher(ReactiveRequestMatchers.header("foo", "baz", "bar"), false);
		verifyMatcher(ReactiveRequestMatchers.header("example", "baz", "bar"), false);
	}

	@Test
	void headerDoesNotExists() {
		request.getHeaders().add("foo", "bar");
		verifyMatcher(ReactiveRequestMatchers.headerDoesNotExists("baz"), true);
		verifyMatcher(ReactiveRequestMatchers.headerDoesNotExists("foo"), false);
	}

	private void verifyMatcher(ReactiveRequestMatcher requestMatcher, boolean expectResult) {
		StepVerifier.create(requestMatcher.matchRequest(request))
				.expectNext(expectResult)
				.verifyComplete();
	}
}