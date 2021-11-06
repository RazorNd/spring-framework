package org.springframework.test.web.reactive.client.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

public abstract class ReactiveResponseCreators {

	public static DefaultReactiveResponseCreator withSuccess() {
		return new DefaultReactiveResponseCreator(HttpStatus.OK);
	}

	public static DefaultReactiveResponseCreator withSuccess(String body, @Nullable MediaType contentType) {
		DefaultReactiveResponseCreator creator = new DefaultReactiveResponseCreator(HttpStatus.OK).body(body);

		return contentType != null ? creator.contentType(contentType) : creator;
	}
}
