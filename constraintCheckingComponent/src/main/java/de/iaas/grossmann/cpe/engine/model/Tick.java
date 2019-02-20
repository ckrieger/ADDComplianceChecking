package de.iaas.grossmann.cpe.engine.model;

import java.util.Date;

public class Tick {

	String symbol;
    Double price;
    private Date timeStamp;

    public Tick(String s, double p, long t) {
        symbol = s;
        price = p;
        timeStamp = new Date(t);
    }
    public double getPrice() {return price;}
    public String getSymbol() {return symbol;}
    public Date getTimeStamp() {return timeStamp;}

    @Override
    public String toString() {
        return "Price: " + price.toString() + " time: " + getTimeStamp().toString();
    }

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
}
