package oracle.fsgbu.queuingModel.service;

import oracle.fsgbu.queuingModel.dto.OptimalTellerCounterResponse;

public interface TellerCounterService {
public OptimalTellerCounterResponse fit(Integer targetWaitTime) throws Exception;
}
