/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.oracleheavyingest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author javau
 */
public class W1BulkImportExternalTabParallelProcLoader {

	/*
	 * External Tables with Parallel Processing
	 */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void loadFromExternalTable(String externalTableName, String targetTable) {
		String sql = String.format("""
				INSERT /*+ APPEND PARALLEL(8) */ INTO %s
				SELECT * FROM %s
				LOG ERRORS REJECT LIMIT UNLIMITED
				""", targetTable, externalTableName);

		jdbcTemplate.execute(sql);
	}

	public void createExternalTable(String tableName, String directory, String filePattern) {
		String sql = String.format("""
				CREATE TABLE %s (
				    claim_id VARCHAR2(50),
				    patient_id VARCHAR2(50),
				    -- other columns
				)
				ORGANIZATION EXTERNAL (
				    TYPE ORACLE_LOADER
				    DEFAULT DIRECTORY data_dir
				    ACCESS PARAMETERS (
				        RECORDS DELIMITED BY NEWLINE
				        BADFILE 'bad_%s.bad'
				        LOGFILE 'log_%s.log'
				        FIELDS TERMINATED BY '|'
				        MISSING FIELD VALUES ARE NULL
				    )
				    LOCATION ('%s')
				)
				REJECT LIMIT UNLIMITED
				""", tableName, tableName, tableName, filePattern);

		jdbcTemplate.execute(sql);
	}

}
