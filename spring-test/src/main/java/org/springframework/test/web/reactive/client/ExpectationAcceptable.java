package org.springframework.test.web.reactive.client;

interface ExpectationAcceptable {
	void accept(Expectation expectation);
}
