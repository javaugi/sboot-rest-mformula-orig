/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import java.util.Objects;

public class MedicationItem {

	private String drugCode; // e.g., NDC code

	private int quantity;

	private String dosageInstructions;

	// Constructor, getters, setters
	public MedicationItem() {
	}

	public String getDrugCode() {
		return drugCode;
	}

	public void setDrugCode(String drugCode) {
		this.drugCode = drugCode;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getDosageInstructions() {
		return dosageInstructions;
	}

	public void setDosageInstructions(String dosageInstructions) {
		this.dosageInstructions = dosageInstructions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MedicationItem that = (MedicationItem) o;
		return quantity == that.quantity && Objects.equals(drugCode, that.drugCode)
				&& Objects.equals(dosageInstructions, that.dosageInstructions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(drugCode, quantity, dosageInstructions);
	}

	@Override
	public String toString() {
		return "MedicationItem{" + "drugCode='" + drugCode + '\'' + ", quantity=" + quantity + ", dosageInstructions='"
				+ dosageInstructions + '\'' + '}';
	}

}
