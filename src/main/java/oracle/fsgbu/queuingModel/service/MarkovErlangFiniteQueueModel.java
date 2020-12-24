package oracle.fsgbu.queuingModel.service;

import oracle.fsgbu.queuingModel.util.QueueMath;

public class MarkovErlangFiniteQueueModel extends QueueModel {

	private Integer lambda;
	private Integer mu;
	private Integer C;
	private Integer queueLength;

	public MarkovErlangFiniteQueueModel(Integer arrivalRate, Integer serviceRate, Integer noOfServers,
			Integer queueLength) throws Exception {
		super();
		if (arrivalRate < 0 || serviceRate < 0 || noOfServers < 0 || queueLength < 0) {
			throw new Exception("arrival_rate, service_rate, num_of_server, queue_capacity must be positive number");
		} else if (arrivalRate == (serviceRate * noOfServers)) {
			throw new Exception(
					"Queue is unstable. Arrival rate can't be equal to multiplication of service rate and number\"\r\n"
							+ "                \" of server for a finite length queue");
		} else if (noOfServers > queueLength) {
			throw new Exception("Queue is unstable. Number of server can not be more than queue capacity");

		} else {
			this.lambda = arrivalRate;
			this.mu = serviceRate;
			this.C = noOfServers;
			this.queueLength = queueLength;
			rho = ((double) this.lambda / (double) this.mu);
			p0 = this.getProbZero();
			pn = this.getProbN();
			lambdaEffective = this.lambda * (1 - pn);
			lq = this.getExpectedQueueLength();
			ls = this.getExpectedSystemLength();
			wq = this.getExpectedQueueWaitingTime();
			ws = this.getExpectedSystemWaitingTime();
			L = this.getAvgCustomerInSystem();
		}
	}

	@Override
	public Double getProbZero() {
		if (this.C == 1)
			return (1 - rho) / (1 - Math.pow(rho, (this.queueLength + 1)));
		else {
			Double firtsCom = QueueMath.power_sum(rho, C - 1);
			Double secondCom = (Math.pow(rho, C) / QueueMath.factorial(C))
					* ((1 - Math.pow((rho/C), (queueLength - C + 1))) / (1 - rho / C));
			return (1 / (firtsCom + secondCom));
		}

	}

	@Override
	protected Double getProbN() {
		if (C == 1) {
			return Math.pow(rho, queueLength) * p0;
		} else {
			return (Math.pow(rho, queueLength) * p0) / (QueueMath.factorial(C) * Math.pow(C, queueLength - C));
		}

	}

	@Override
	protected Double getExpectedQueueLength() {
		if (C == 1) {
			return ((p0 * rho) / Math.pow((1 - rho), 2)) * (1 - ((queueLength + 1) * Math.pow(rho, queueLength))
					+ (queueLength * Math.pow(rho, (queueLength + 1))));
		} else {
			Double firstTerm = (Math.pow(rho, C + 1) * p0) / (QueueMath.factorial(C - 1));
			Double secondTerm = 1 - (Math.pow((rho / C), (queueLength - C + 1)))
					- ((queueLength - C + 1) * (1 - (rho / C)) * (Math.pow((rho / C), (queueLength - C))));
			Double thirdTerm = Math.pow((C - rho), 2);
			return firstTerm * (secondTerm / thirdTerm);
		}

	}

	@Override
	public Double getExpectedSystemLength() {
		return lq + (lambdaEffective / mu);
	}

	@Override
	protected Double getExpectedQueueWaitingTime() {
		return lq / lambdaEffective;
	}

	@Override
	protected Double getExpectedSystemWaitingTime() {
		return ls / lambdaEffective;
	}

	@Override
	protected Double getAvgCustomerInSystem() {
		return lambdaEffective * ws;
	}

}
