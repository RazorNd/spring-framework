package org.springframework.test.web.reactive.client.match;

import org.springframework.mock.http.client.reactive.MockClientHttpRequest;
import org.springframework.test.web.reactive.client.ReactiveRequestMatcher;

public class ContentRequestMatchers {

	public ReactiveRequestMatcher string(String expectedContent) {
		return request -> {
			MockClientHttpRequest mockClientHttpRequest = (MockClientHttpRequest) request;
			return mockClientHttpRequest.getBodyAsString()
					.map(expectedContent::equals);
		};
	}
}
