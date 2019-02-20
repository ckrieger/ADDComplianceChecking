package de.iaas.grossmann.cpe.engine.test.subscriber;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import de.iaas.grossmann.cpe.engine.subscriber.PatternStatementSubscriber;

@Component
public class TestSubscriber implements PatternStatementSubscriber {

	public String getStatement() {
		String testStatement = "select * from " +
				"StockTick(symbol='AAPL').win:length(1) " +
				"having avg(price) > 6.0";
		return testStatement;
	}
	
	public String getPatternName() {
		return "test pattern";
	}

	/**
	 * Listener method called when Esper has detected a pattern match.
	 */
	public void update(Map<String, Object> eventMap) {
		StringBuilder sb = new StringBuilder();
		sb.append("***************************************");
		sb.append("\n* [VIOLATION] : " + getPatternName() + "! \n*");
		
		for (String key : eventMap.keySet()) {
			sb.append(key + ": " + eventMap.get(key) + ", ");
		}
		sb.append("\n***************************************");

		System.out.println(sb.toString());
	}

	@Override
	public void setActive(boolean active) {
		// TODO Auto-generated method stub
	}
}
