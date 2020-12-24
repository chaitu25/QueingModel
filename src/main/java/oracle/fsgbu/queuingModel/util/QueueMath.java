package oracle.fsgbu.queuingModel.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueueMath {
	private static Map<Integer, Integer> factmap = new HashMap<>();

	public static Integer factorial(Integer n) {
		if(n < 0) {
			return 0;
		}
		else if (n == 0) {
			return 1;
		} else if (factmap.containsKey(n)) {
			return factmap.get(n);
		}
		else {
			Integer res = n * factorial(n - 1);
			factmap.put(n, res);
			return res;
		}
	}
	
	public static Double power_sum(Double rho, Integer n) {
		Double sum = 0.0;
		for (int i = 0; i <= n; i++) {
			sum += Math.pow(rho, i) / factorial(i);
		}
		return sum;
	}
	
	public static Double numerical_diff(Map<Integer, Double> queueDict, Integer key) {
		queueDict = queueDict.entrySet().stream().sorted(Map.Entry.<Integer, Double>comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		List<Integer> keyarr = new ArrayList<>(queueDict.keySet());
		return queueDict.get(keyarr.get(keyarr.indexOf(key) - 1)) - queueDict.get(key);
	}
}
