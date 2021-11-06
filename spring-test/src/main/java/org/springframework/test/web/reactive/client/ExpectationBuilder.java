package org.springframework.test.web.reactive.client;

import org.springframework.test.web.client.ExpectedCount;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ExpectationBuilder {

	private final List<ReactiveRequestMatcher> matchers = new ArrayList<>(1);

	private final ExpectedCount expectedCount;

	private final Consumer<ReactiveRequestExpectation> expectationConsumer;

	public ExpectationBuilder(ExpectedCount expectedCount,
							  Consumer<ReactiveRequestExpectation> expectationConsumer,
							  ReactiveRequestMatcher matcher) {
		this.expectedCount = expectedCount;
		this.expectationConsumer = expectationConsumer;
		andExpect(matcher);
	}

	public ExpectationBuilder andExpect(ReactiveRequestMatcher matcher) {
		matchers.add(matcher);
		return this;
	}

	public void andRespond(ReactiveResponseCreator responseCreator) {
		expectationConsumer.accept(new ReactiveRequestExpectation(expectedCount, matchers, responseCreator));
	}
}
