/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
// @AllArgsConstructor
public class TopUsers implements Comparable<TopUsers> {

	// For priority queue

	String userId;

	Long count;

	public TopUsers(String userId, Long count) {
		this.userId = userId;
		this.count = count;
	}

	@Override
	public int compareTo(TopUsers other) {
		return Long.compare(this.count, other.count);
	}
	// Min-heap by count
	// equals and hashCode needed for proper set behavior if needed

}
