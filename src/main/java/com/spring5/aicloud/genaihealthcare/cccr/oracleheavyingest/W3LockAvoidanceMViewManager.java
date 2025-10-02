/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.oracleheavyingest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author javau
 */
public class W3LockAvoidanceMViewManager {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
	public void refreshMaterializedViews() {
		refreshMv("claims_daily_mv", "COMPLETE");
		refreshMv("claims_monthly_mv", "FAST");
	}

	private void refreshMv(String mvName, String refreshType) {
		String sql = String.format("BEGIN DBMS_MVIEW.REFRESH('%s', '%s'); END;", mvName, refreshType);

		jdbcTemplate.execute(sql);
	}

	public void createIncrementalMView(String mvName, String query) {
		String sql = String.format("""
				CREATE MATERIALIZED VIEW %s
				BUILD IMMEDIATE
				REFRESH FAST ON COMMIT
				AS %s
				""", mvName, query);

		jdbcTemplate.execute(sql);
	}

}
