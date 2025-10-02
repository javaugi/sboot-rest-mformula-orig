/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.idempotency;

import lombok.Data;

@Data
public class OrderRequest {

	private Long productId;

	private int quantity;

}
