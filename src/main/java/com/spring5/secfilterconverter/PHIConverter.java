/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.secfilterconverter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.spring5.secfilterconverter.PHIPseudonymizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
6. Best Practices for PHI Logging
    Rotate the per-environment HMAC key regularly.
    Use structured logging (e.g., JSON) with masking logic at the log appender level.
    Avoid logging PHI at all unless strictly necessary.
    Consider using Spring Boot Logback filters for centralized masking if many fields need protection.
 */
public class PHIConverter extends ClassicConverter {

	private static final PHIPseudonymizer pseudonymizer = new PHIPseudonymizer(
			System.getenv().getOrDefault("PHI_KEY", "default-dev-key"));

	private static final Pattern PATIENT_ID_PATTERN = Pattern.compile("patientId=([A-Za-z0-9_-]+)");

	// No raw PHI ever goes into logs.
	@Override
	public String convert(ILoggingEvent event) {
		String msg = event.getFormattedMessage();
		// Simple rule: pseudonymize anything after "patientId="
		// Example: patientId=12345 → patientId=<hashed>
		Matcher matcher = PATIENT_ID_PATTERN.matcher(msg);

		// Java 9+ supports lambda here
		return matcher.replaceAll(matchResult -> "patientId=" + pseudonymizer.pseudonymize(matchResult.group(1)));
	}

}
