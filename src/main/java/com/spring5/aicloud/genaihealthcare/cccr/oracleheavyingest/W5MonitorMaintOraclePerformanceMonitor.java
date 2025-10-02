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
public class W5MonitorMaintOraclePerformanceMonitor {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Scheduled(fixedDelay = 300000) // Every 5 minutes
	public void monitorPerformance() {
		checkForLockContention();
		checkForLongRunningQueries();
		checkTablespaceUsage();
	}

	private void checkForLockContention() {
		String sql = """
				SELECT sid, serial#, username,
				       seconds_in_wait, blocking_session
				FROM v$session
				WHERE blocking_session IS NOT NULL
				AND seconds_in_wait > 300
				""";

		jdbcTemplate.query(sql, rs -> {
			// Alert on long-running locks
			alertLockContention(rs.getString("username"), rs.getInt("seconds_in_wait"));
		});
	}

	private void checkTablespaceUsage() {
		String sql = """
				SELECT tablespace_name,
				       used_percent,
				       autoextensible
				FROM dba_tablespace_usage_metrics
				WHERE used_percent > 90
				""";

		jdbcTemplate.query(sql, rs -> {
			// Alert on space issues
			alertTablespaceFull(rs.getString("tablespace_name"), rs.getDouble("used_percent"));
		});
	}

	private void checkForLongRunningQueries() {
	}

	private void alertLockContention(String username, int waittime) {
	}

	private void alertTablespaceFull(String tablespaceName, double percent) {
	}

}
