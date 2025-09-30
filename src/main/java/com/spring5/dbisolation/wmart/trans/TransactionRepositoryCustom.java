/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.trans;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author javau
 */
public interface TransactionRepositoryCustom {

    Page<Transaction> findByCriteria(TransactionQueryCriteria criteria, Pageable pageable);
}
