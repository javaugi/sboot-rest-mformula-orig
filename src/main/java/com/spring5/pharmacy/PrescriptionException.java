/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.pharmacy;

import org.springframework.http.HttpStatus;

/**
 * @author javaugi
 */
public class PrescriptionException extends RuntimeException {

	private HttpStatus status;

	public PrescriptionException() {
		super();
	}

	public PrescriptionException(String message) {
		super(message);
	}

	public PrescriptionException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

}
