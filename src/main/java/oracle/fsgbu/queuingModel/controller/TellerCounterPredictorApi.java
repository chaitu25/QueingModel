package oracle.fsgbu.queuingModel.controller;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;
import oracle.fsgbu.queuingModel.api.TellerCountApi;
import oracle.fsgbu.queuingModel.dto.OptimalTellerCounterResponse;
import oracle.fsgbu.queuingModel.service.TellerCounterService;
import oracle.fsgbu.queuingModel.service.TellerCounterServiceImpl;

@RestController
public class TellerCounterPredictorApi implements TellerCountApi {
	private static final Logger LOGGER = LoggerFactory.getLogger(TellerCounterServiceImpl.class);

	@Override
	@RequestMapping(value = "/tellerCount/optimizer", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.POST)
	public ResponseEntity predictTellerCount(
			@NotNull @ApiParam(value = "Expected volume of customer (per hour) . This should be positive number", required = true) @Valid @RequestParam(value = "expectedArrivalRate", required = true) String expectedArrivalRate,
			@NotNull @ApiParam(value = "Average time to serve a customer (in Minutes). This should be positive number", required = true) @Valid @RequestParam(value = "expectedServiceTime", required = true) String expectedServiceTime,
			@NotNull @ApiParam(value = "Maximum number of customers can be in a branch at given time. This should be positive number", required = true) @Valid @RequestParam(value = "branchCapacity", required = true) String branchCapacity,
			@NotNull @ApiParam(value = "Physical Teller counters available. This should be positive number", required = true) @Valid @RequestParam(value = "maxTellerCounters", required = true) String maxTellerCounters,
			@NotNull @ApiParam(value = "Target waiting time for a customer (in Minutes). This should be positive number", required = true) @Valid @RequestParam(value = "targetWaitTime", required = true) String targetWaitTime) {
		OptimalTellerCounterResponse response = null;
		try {
			TellerCounterService service = new TellerCounterServiceImpl(expectedArrivalRate, expectedServiceTime,
					branchCapacity, maxTellerCounters);
			response = service.fit(new Integer(targetWaitTime));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}
