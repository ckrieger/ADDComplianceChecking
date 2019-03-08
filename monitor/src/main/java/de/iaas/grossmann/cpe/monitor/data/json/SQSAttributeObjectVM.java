package de.iaas.grossmann.cpe.monitor.data.json;

public class SQSAttributeObjectVM {

	private MessageAttributeValue vmid;
	private MessageAttributeValue vmcpu;
	private MessageAttributeValue vmmemory;
	private MessageAttributeValue vmdisk;
	
	public MessageAttributeValue getVmid() {
		return vmid;
	}
	public void setVmid(MessageAttributeValue vmid) {
		this.vmid = vmid;
	}
	public MessageAttributeValue getVmcpu() {
		return vmcpu;
	}
	public void setVmcpu(MessageAttributeValue vmcpu) {
		this.vmcpu = vmcpu;
	}
	public MessageAttributeValue getVmmemory() {
		return vmmemory;
	}
	public void setVmmemory(MessageAttributeValue vmmemory) {
		this.vmmemory = vmmemory;
	}
	public MessageAttributeValue getVmdisk() {
		return vmdisk;
	}
	public void setVmdisk(MessageAttributeValue vmdisk) {
		this.vmdisk = vmdisk;
	}
}
