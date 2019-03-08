package de.iaas.grossmann.cpe.monitor;

/**
 * Inteface for implementing monitors for a specific node type.
 * 
 * @author Steven
 *
 */
public interface ComponentMonitor {

	/**
	 * Starts the monitoring process and processes all incomming data to be integrated into the CEP engine. 
	 */
	public void start();
	/**
	 * Stops the monitoring process. No data of the nodes will be processed anymore.
	 */
	public void stop();
	/**
	 * Retrieves the name of the Node Monitor. Can be used as identification.
	 * @return name of this Node Monitor
	 */
	public String getName();
	/**
	 * Retrieves the status of the Node Monitor.
	 * @return monitorStatus ACTIVE if monitor is running, otherwise INACTIVE
	 */
	public MonitorStatus getStatus();
}
