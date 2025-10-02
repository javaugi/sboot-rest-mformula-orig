/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Version;

@Data
@Builder(toBuilder = true)
// @Entity
public class Account {

	@Id
	private Long id;

	private BigDecimal balance;

	@Version
	private Long version;

	// getters/setters

}
