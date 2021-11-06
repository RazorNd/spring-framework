package org.springframework.test.web.reactive.client.response;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.mock.http.client.reactive.MockClientHttpResponse;
import org.springframework.test.web.reactive.client.ReactiveResponseCreator;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DefaultReactiveResponseCreator implements ReactiveResponseCreator {

	private final HttpStatus httpStatus;

	private final HttpHeaders headers = new HttpHeaders();

	private String bodyString;
	private Charset bodyCharset = StandardCharsets.UTF_8;

	private Publisher<DataBuffer> body;

	protected DefaultReactiveResponseCreator(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public DefaultReactiveResponseCreator body(String bodyString) {
		this.bodyString = bodyString;
		return this;
	}

	@Override
	public Mono<ClientHttpResponse> createResponse(ClientHttpRequest request) {
		MockClientHttpResponse response = new MockClientHttpResponse(httpStatus);

		response.getHeaders().addAll(headers);

		if (bodyString != null) {
			response.setBody(bodyString, bodyCharset);
		} else if(body != null) {
			response.setBody(body);
		}

		return Mono.just(response);
	}

	public DefaultReactiveResponseCreator contentType(MediaType contentType) {
		headers.setContentType(contentType);
		return this;
	}
}
