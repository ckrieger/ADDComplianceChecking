package de.iaas.grossmann.cpe.engine.subscriber;

import org.springframework.stereotype.Component;

@Component(value="subscriberFactory")
public abstract class SubscriberFactory {

	public abstract PatternViolationStatementSubscriber createViolationSubscriber(String statement, String patternName);
	
}
