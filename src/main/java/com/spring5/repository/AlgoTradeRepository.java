/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.repository;

import com.spring5.entity.AlgoTrade;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlgoTradeRepository extends JpaRepository<AlgoTrade, Long> {

    @Query("SELECT t FROM AlgoTrade t JOIN FETCH t.userAccount ua JOIN FETCH ua.user u WHERE u.email=(:email)")
    List<AlgoTrade> findByUserEmail(@Param("email") String email) throws Exception;

    @Query("SELECT t FROM AlgoTrade t WHERE u.id in (:ids)")
    List<AlgoTrade> findByIds(@Param("ids") List<Long> ids) throws Exception;

    @Query("UPDATE UserAccount SET cashBalance = cashBalance + (:amount) WHERE id =(:userAccountId)")
    void addMoney(@Param("userAccountId") Long userAccountId, @Param("amount") BigDecimal amount) throws Exception;
}
