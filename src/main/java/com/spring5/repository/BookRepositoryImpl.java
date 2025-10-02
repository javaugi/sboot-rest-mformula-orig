/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.repository;

import com.spring5.entity.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import java.sql.Types;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BookRepositoryImpl {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// @Autowired
	// private MongoTemplate mongoTemplate;
	// Oracle Stored procedure
	public int updatePricesByCategory(String category, double percentage) {
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("update_book_prices")
			.registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
			.registerStoredProcedureParameter(2, Double.class, ParameterMode.IN)
			.registerStoredProcedureParameter(3, Integer.class, ParameterMode.OUT)
			.setParameter(1, category)
			.setParameter(2, percentage);

		query.execute();
		return (Integer) query.getOutputParameterValue(3);
	}

	// SQL Server Stored procedure
	public int archiveOldBooks(LocalDate cutoffDate, int archiveYear) {
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_archive_old_books")
			.declareParameters(new SqlParameter("cutoff_date", Types.DATE),
					new SqlParameter("archive_year", Types.INTEGER),
					new SqlOutParameter("rows_affected", Types.INTEGER));

		Map<String, Object> params = new HashMap<>();
		params.put("cutoff_date", cutoffDate);
		params.put("archive_year", archiveYear);

		Map<String, Object> result = call.execute(params);
		return (int) result.get("rows_affected");
	}

	// JS for MongoDB
	/*
	 * public String updateBookRatings() { return
	 * (String)mongoTemplate.scriptOps().call("updateBookRatings", 2); // minReviews = 2 }
	 * //
	 */
	// 4. Using Batch processing
	@Transactional
	public void batchInsert(List<Book> books) {
		for (int i = 0; i < books.size(); i++) {
			entityManager.persist(books.get(i));
			if (i % 50 == 0) { // Flush every 50 records
				entityManager.flush();
				entityManager.clear();
			}
		}
	}

}
