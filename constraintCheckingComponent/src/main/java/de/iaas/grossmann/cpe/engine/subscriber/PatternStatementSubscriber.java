package de.iaas.grossmann.cpe.engine.subscriber;

import java.util.Map;

public interface PatternStatementSubscriber {

    /**
     * Get the EPL Stamement the Subscriber will listen to.
     * @return EPL Statement
     */
    public String getStatement();
    public String getPatternName();
    public void setActive(boolean active);
    
    default public void update(Map<String, Object> eventMap) {
    	StringBuilder sb = new StringBuilder();
		sb.append("***************************************");
		sb.append("\n* [" + getPatternName() + "] \n*");
		
		for (String key : eventMap.keySet()) {
			sb.append(key + ": " + eventMap.get(key) + ", ");
		}
		sb.append("\n***************************************");
		System.out.println(sb.toString());
    }

}
