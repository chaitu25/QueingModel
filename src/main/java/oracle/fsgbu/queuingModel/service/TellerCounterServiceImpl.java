package oracle.fsgbu.queuingModel.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oracle.fsgbu.queuingModel.dto.OptimalTellerCounterResponse;
import oracle.fsgbu.queuingModel.util.QueueMath;
import oracle.fsgbu.queuingModel.util.SystemStability;

public class TellerCounterServiceImpl implements TellerCounterService {
	private static final String THIS_COMPONENT_NAME = TellerCounterServiceImpl.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(TellerCounterServiceImpl.class);

	private Integer optimalTellerCount;
	private Integer arrivalRate;
	private Integer serviceRate;
	private Integer queueCapacity;
	private Integer teller_capacity;
	private Double balkingProb;
	private Double avgNoOfCustomers;
	private Map<Integer, Double> queueDict;
	private static Properties properties;
	private static DecimalFormat df2 = new DecimalFormat("#.####");

	public TellerCounterServiceImpl(String arrivalRate, String serviceRate, String queueCapacity,
			String teller_capacity) throws Exception {
		super();
		if (properties == null) {
			properties = new Properties();
			properties.load(TellerCounterServiceImpl.class.getClassLoader().getResourceAsStream("queue.properties"));
		}
		this.arrivalRate = new Integer(arrivalRate);
		this.serviceRate = 60 / (new Integer(serviceRate));
		this.queueCapacity = new Integer(queueCapacity);
		this.teller_capacity = new Integer(teller_capacity);
		if (this.teller_capacity <= 0) {
			throw new Exception("teller_capacity must be positive integer");
		}
		this.optimalTellerCount = 0;
		queueDict = new HashMap<>();
		LOGGER.info("Queue Object Modeled Successfully");
	}

	@Override
	public OptimalTellerCounterResponse fit(Integer targetWaitTime) throws Exception {
		LOGGER.info("into %s method of class %","fit",THIS_COMPONENT_NAME);
		OptimalTellerCounterResponse response = new OptimalTellerCounterResponse();
		Double waitingTime = Double.POSITIVE_INFINITY;
		Double delta = Double.parseDouble(properties.get("delta").toString());
		while (waitingTime > targetWaitTime && optimalTellerCount < teller_capacity) {
			optimalTellerCount += 1;
			try {
				QueueModel queue = new MarkovErlangFiniteQueueModel(this.arrivalRate, this.serviceRate,
						this.optimalTellerCount, this.queueCapacity);
				waitingTime = 60 * queue.getExpectedQueueWaitingTime();
				queueDict.put(optimalTellerCount, waitingTime);
				if (queueDict.size() > 1 && (QueueMath.numerical_diff(queueDict, optimalTellerCount) < delta)) {
					optimalTellerCount -= 1;
					balkingProb = queue.pn;
					avgNoOfCustomers = queue.L;
					break;
				}
				balkingProb = queue.pn;
				avgNoOfCustomers = queue.L;
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				throw e;
			}
		}
		response.setOptimizedTellerCounters(new BigDecimal(optimalTellerCount));
		response.setBalkingProbability(new Double(df2.format(balkingProb)));
		response.setCategory("Success");
		response.setAverageNumberOfCustomersInSystem(new BigDecimal(avgNoOfCustomers).setScale(4,RoundingMode.DOWN));
		if (!queueDict.containsKey(optimalTellerCount))
			response.setOptimizer(SystemStability.Unstable.getStability());
		else if((queueDict.get(optimalTellerCount) - targetWaitTime) > delta)
			response.setOptimizer(SystemStability.SemiStable.getStability());
		else
			response.setOptimizer(SystemStability.Stable.getStability());
		return response;
	}
}
