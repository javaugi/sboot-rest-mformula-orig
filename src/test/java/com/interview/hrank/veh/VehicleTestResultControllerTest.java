/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.veh;

// Unit Test
import com.spring5.dbisolation.gmcodility.VehicleTestResult;
import com.spring5.dbisolation.gmcodility.VehicleTestResultRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @ExtendWith(MockitoExtension.class)
// @DataJpaTest
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class VehicleTestResultControllerTest {

	// @MockBean
	// @Mock
	// @InjectMocks
	@Autowired
	private VehicleTestResultRepository testResultRepository;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	// @Test
	public void getTestResults_withFilters_shouldReturnFilteredResults() throws Exception {
		VehicleTestResult result = new VehicleTestResult(1L, "BATTERY", "shsddfdfldfdf", LocalDate.now(), true);

		when(testResultRepository.findAll(any(Specification.class))).thenReturn(List.of(result));

		mockMvc.perform(get("/api/vehicle-tests").param("testType", "BATTERY").param("passed", "true"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].testType").value("BATTERY"))
			.andExpect(jsonPath("$[0].passed").value(true));
	}

}
