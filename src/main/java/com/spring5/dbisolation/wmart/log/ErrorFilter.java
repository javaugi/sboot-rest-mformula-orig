/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.log;

// import org.neo4j.kernel.impl.transaction.log.entry.LogEntry;
// import org.springframework.boot.logging.LogLevel;
/**
 * @author javau
 */
public interface ErrorFilter {

	boolean includes(LogEntry log);

	ErrorFilter ERRORS_ONLY = LogEntry::isError;

	ErrorFilter WARNINGS_AND_ERRORS = log -> log.getLevel() == LogLevel.WARN || log.getLevel() == LogLevel.ERROR;

	ErrorFilter ALL_LEVELS = log -> true;

}
