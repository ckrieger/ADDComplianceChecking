package de.iaas.grossmann.cpe.monitor.data.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageAttributeValue {

	@JsonProperty
	private String DataType;
	@JsonProperty
	private String StringValue;
	
	public String getDataType() {
		return DataType;
	}
	public void setDataType(String dataType) {
		this.DataType = dataType;
	}
	public String getStringValue() {
		return StringValue;
	}
	public void setStringValue(String stringValue) {
		this.StringValue = stringValue;
	}
}
