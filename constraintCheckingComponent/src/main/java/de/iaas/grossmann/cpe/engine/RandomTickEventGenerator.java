package de.iaas.grossmann.cpe.engine;

import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import de.iaas.grossmann.cpe.engine.handler.EsperCEPEngine;
import de.iaas.grossmann.cpe.engine.model.Tick;
import de.iaas.grossmann.cpe.engine.subscriber.PatternStatementSubscriber;

/**
 * Just a simple class to create a number of Random TemperatureEvents and pass them off to the
 * TemperatureEventHandler.
 */
public class RandomTickEventGenerator {

    /** The TemperatureEventHandler - wraps the Esper engine and processes the Events  */
    @Autowired
    private EsperCEPEngine eventHandler;

    public void startDemo(int numberOfEvents) {
    	eventHandler.addStatementSubscriber(new PatternStatementSubscriber() {
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
		});
    	for (int i = 0; i < numberOfEvents; i++) generateRandomTick();
    }

    public void generateRandomTick() {
    	Random generator = new Random();
		double price = (double) generator.nextInt(10);
		long timeStamp = System.currentTimeMillis();
		String symbol = "AAPL";
		Tick tick = new Tick(symbol, price, timeStamp);
		System.out.println("Sending tick:" + tick);
//		eventHandler.handle(tick);
	}
    
}
