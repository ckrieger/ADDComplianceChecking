package de.iaas.grossmann.cpe.engine.model;

public class HttpEvent {
	 private long id;
	    private String source;
	    private String destination;
	    private int failure;
	    private int success;
	    private long timestamp;

	    public HttpEvent(long id, String source, String destination, int failure, int success, long timestamp) {
	        this.id = id;
	        this.source = source;
	        this.destination = destination;
	        this.failure = failure;
	        this.success = success;
	        this.timestamp = timestamp;
	    }

	    public HttpEvent() {
			// TODO Auto-generated constructor stub
		}

		public long getId() {
	        return id;
	    }

	    public void setId(long id) {
	        this.id = id;
	    }

	    public String getSource() {
	        return source;
	    }

	    public void setSource(String source) {
	        this.source = source;
	    }

	    public String getDestination() {
	        return destination;
	    }

	    public void setDestination(String destination) {
	        this.destination = destination;
	    }

	    public int getFailure() {
	        return failure;
	    }

	    public void setFailure(int failure) {
	        this.failure = failure;
	    }

	    public int getSuccess() {
	        return success;
	    }

	    public void setSuccess(int success) {
	        this.success = success;
	    }

	    public long getTimestamp() {
	        return timestamp;
	    }

	    public void setTimestamp(long timestamp) {
	        this.timestamp = timestamp;
	    }

	    @Override
	    public String toString() {
	        return "HttpEvent{" +
	                "id=" + id +
	                ", source='" + source + '\'' +
	                ", destination='" + destination + '\'' +
	                ", failure=" + failure +
	                ", success=" + success +
	                ", timestamp=" + timestamp +
	                '}';
	    }

}
