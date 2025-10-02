/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.veh;

import com.spring5.utils.MapToJsonConverter;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MapToJsonConverterTest {

	// @Autowired
	// private MapToJsonConverter converter;
	@Test
	public void testConverter() {
		Map<String, Object> testMap = Map.of("key", "value");
		MapToJsonConverter converter = new MapToJsonConverter();
		String json = converter.convertToDatabaseColumn(testMap);
		Map<String, Object> result = converter.convertToEntityAttribute(json);
		assertEquals(testMap, result);
	}

}
