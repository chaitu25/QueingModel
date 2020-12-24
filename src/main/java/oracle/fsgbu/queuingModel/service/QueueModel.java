package oracle.fsgbu.queuingModel.service;

public abstract class QueueModel {
	protected Double p0;
	protected Double pn;
	protected Double lq;
	protected Double ls;
	protected Double wq;
	protected Double ws;
	protected Double L;
	protected Double rho;
	protected Double lambdaEffective;
	
	public abstract Double getProbZero();
	protected abstract Double getProbN();
	protected abstract Double getExpectedQueueLength();
	protected abstract Double getExpectedSystemLength();
	protected abstract Double getExpectedQueueWaitingTime();
	protected abstract Double getExpectedSystemWaitingTime();
	protected abstract Double getAvgCustomerInSystem();
	
	
}
