package org.springframework.test.web.reactive.client.match;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.test.web.reactive.client.ReactiveRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class ReactiveRequestMatchers {

	private static ReactiveRequestMatcher match(Predicate<ClientHttpRequest> predicate) {
		return request -> Mono.just(predicate.test(request));
	}

	public static ReactiveRequestMatcher anything() {
		return request -> Mono.just(true);
	}

	public static ReactiveRequestMatcher method(HttpMethod method) {
		return match(request -> method.equals(request.getMethod()));
	}

	public static ReactiveRequestMatcher requestTo(Matcher<? extends String> matcher) {
		Assert.notNull(matcher, "'matcher' must not be null");
		return match(request -> matcher.matches(request.getURI().toString()));
	}

	public static ReactiveRequestMatcher requestTo(String expectedUri) {
		Assert.notNull(expectedUri, "'expectedUri' must not be null");
		return match(request -> expectedUri.equals(request.getURI().toString()));
	}

	public static ReactiveRequestMatcher requestTo(URI uri) {
		Assert.notNull(uri, "'uri' must not be null");
		return match(request -> uri.equals(request.getURI()));
	}

	public static ReactiveRequestMatcher requestToUriTemplate(String expectedUri, Object... uriVars) {
		Assert.notNull(expectedUri, "'expectedUri' must not be null");
		URI uri = UriComponentsBuilder.fromUriString(expectedUri).buildAndExpand(uriVars).encode().toUri();
		return requestTo(uri);
	}

	public static ReactiveRequestMatcher queryParam(String name, String... expectedValues) {
		return match(request -> {
			MultiValueMap<String, String> queryParams = getQueryParams(request);
			return Arrays.asList(expectedValues).equals(queryParams.get(name));
		});
	}

	public static ReactiveRequestMatcher header(String name, String... expectedValue) {
		return match(request -> {
			HttpHeaders headers = request.getHeaders();
			return Arrays.asList(expectedValue).equals(headers.get(name));
		});
	}

	public static ReactiveRequestMatcher headerDoesNotExists(String name) {
		return match(request -> {
			List<String> headerValues = request.getHeaders().get(name);
			return headerValues == null;
		});
	}

	public static ContentRequestMatchers content() {
		return new ContentRequestMatchers();
	}

	private static MultiValueMap<String, String> getQueryParams(ClientHttpRequest request) {
		return UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();
	}

}
