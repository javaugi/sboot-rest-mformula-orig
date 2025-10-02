/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.oracleheavyingest;

import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class OracleBatchConfig {

	// Use Direct Path Inserts: /*+ APPEND */ for minimal redo generation
	/*
	 * Key Strategies Summary 1. Use Direct Path Inserts: for minimal redo generation 2.
	 * Leverage Partitioning: Process data in manageable chunks 3. Implement Parallel
	 * Processing: Use Oracle parallel hints and Spring Batch 4. Avoid Row-Level Locks:
	 * Use ROWID-based processing and batch updates 5. Use External Tables: For initial
	 * loads without impacting production tables 6. Implement Async Processing: Use Oracle
	 * AQ for non-blocking operations 7. Monitor Continuously: Track lock contention and
	 * performance metrics 8. Use Materialized Views: Offload reporting from transactional
	 * systems
	 */
	@Bean
	public TaskExecutor batchTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(50);
		executor.setQueueCapacity(1000);
		executor.setThreadNamePrefix("oracle-batch-");
		return executor;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		template.setFetchSize(10000); // Large fetch size for batch processing
		return template;
	}

	// *
	@Bean
	public DataSource dataSource() throws SQLException {
		OracleDataSource dataSource = new OracleDataSource();
		dataSource.setURL("jdbc:oracle:thin:@//host:port/service");
		dataSource.setUser("username");
		dataSource.setPassword("password");
		dataSource.setConnectionProperties(new Properties() {
			{
				put("oracle.jdbc.defaultRowPrefetch", "1000");
				put("oracle.jdbc.useThreadLocalBufferCache", "true");
			}
		});
		return dataSource;
	}
	// */

}
