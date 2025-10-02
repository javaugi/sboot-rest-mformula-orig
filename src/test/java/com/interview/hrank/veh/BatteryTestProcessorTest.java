/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.veh;

import com.spring5.dbisolation.gmcodility.BatteryMeasurement;
import com.spring5.dbisolation.gmcodility.BatteryTestData;
import com.spring5.dbisolation.gmcodility.BatteryTestProcessor;
import com.spring5.dbisolation.gmcodility.BatteryTestRepository;
import com.spring5.dbisolation.gmcodility.BatteryTestResult;
import com.spring5.utils.CommonUtils;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@DataJpaTest
public class BatteryTestProcessorTest {

	private BatteryTestRepository repository;

	private BatteryTestProcessor processor;

	@BeforeEach
	public void setUp() {
		repository = mock(BatteryTestRepository.class);
		processor = new BatteryTestProcessor(repository);
	}

	// @Test
	public void processTestDataWithValidDataShouldPass() {
		BatteryTestData testData = BatteryTestData.builder()
			.testId("test123")
			.ratedCapacity(5.0)
			.dischargeCurrent(1.0)
			.dischargeTimeSeconds(18000)
			.measurements(List.of(BatteryMeasurement.builder().voltage(3.7).temperature(25).build(),
					BatteryMeasurement.builder().voltage(3.6).temperature(26).build()))
			.build();

		BatteryTestResult result = processor.processTestData(testData);

		assertTrue(result.isPassed());
		assertEquals(0, CommonUtils.stringTokensize(result.getAnomalies()));
		assertEquals(5.0, CommonUtils.convertJsonToMap(result.getMetrics()).get("measuredCapacity"));
	}

	// @Test
	public void processTestDataWithHighTempShouldFail() {
		BatteryTestData testData = BatteryTestData.builder()
			.testId("test456")
			.ratedCapacity(5.0)
			.dischargeCurrent(1.0)
			.dischargeTimeSeconds(18000)
			.measurements(List.of(BatteryMeasurement.builder().voltage(3.7).temperature(50).build()))
			.build();

		BatteryTestResult result = processor.processTestData(testData);

		assertFalse(result.isPassed());
		assertEquals(1, CommonUtils.stringTokensize(result.getAnomalies()));
		assertTrue(CommonUtils.stringToList(result.getAnomalies()).get(0).contains("Temperature exceeded"));
	}

}
