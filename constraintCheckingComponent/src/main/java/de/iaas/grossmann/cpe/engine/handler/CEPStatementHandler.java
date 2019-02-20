package de.iaas.grossmann.cpe.engine.handler;

import de.iaas.grossmann.cpe.engine.subscriber.PatternStatementSubscriber;

public interface CEPStatementHandler {

	/**
	 * Add a new Statement Subscriber that monitors the event stream. 
	 * @param subscriber
	 */
	public void addStatementSubscriber(PatternStatementSubscriber subscriber);
	/**
	 * Add a single statement. Can be used to define interim events.
	 * @param statement
	 */
	public void addEplStatement(String statement);
    /**
     * Deletes all subscribers
     */
    public void deleteAllSubscribers();
}
