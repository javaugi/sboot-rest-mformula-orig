/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.trans;

/**
 *
 * @author javau
 */
public class TransactionMapper {
    public TransactionDto toDto(Transaction tran) {
        return TransactionDto.builder().build();
    }
}
