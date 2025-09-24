/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.service;

import com.spring5.entity.AlgoTrade;
import com.spring5.repository.AlgoTradeR2dbcRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author javaugi
 */
@Service
public class AlgoTradeR2dbcService {
    
    private final AlgoTradeR2dbcRepository algoTradeRepository;

    public AlgoTradeR2dbcService(AlgoTradeR2dbcRepository algoAlgoTradeRepository) {
        this.algoTradeRepository = algoAlgoTradeRepository;
    }
    
    public Mono<AlgoTrade> findById(Long id) {
        return algoTradeRepository.findById(id).defaultIfEmpty(AlgoTrade.builder().build());
    }

    public Flux<AlgoTrade> findAll() {
        return algoTradeRepository.findAll();
    }

    public Flux<AlgoTrade> findAll(Sort sort) {
        return algoTradeRepository.findAll(sort);
    }

    @Transactional
    public Mono<AlgoTrade> save(AlgoTrade trade) {
        return algoTradeRepository.save(trade);
    }    

    @Transactional
    public Flux<AlgoTrade> saveAll(List<AlgoTrade> trades) {
        return algoTradeRepository.saveAll(trades);
    }

    @Transactional
    public Mono<AlgoTrade> update(AlgoTrade trade) {
        return algoTradeRepository.save(trade);
    }

    public Flux<AlgoTrade> getByUserEmail(String userEmail) {
        try {
            return algoTradeRepository.findByUserEmail(userEmail);
        } catch (Exception ex) {
            return Flux.just(AlgoTrade.builder().build());
        }
    }
    
    public Mono<Boolean> addMoney(Long userAccountId, BigDecimal amount) {
        try {
            algoTradeRepository.addMoney(userAccountId, amount);
        } catch (Exception ex) {
            return Mono.just(Boolean.FALSE);
        }

        return Mono.just(Boolean.TRUE);
    }

    public Mono<Boolean> deleteById(Long id) {
        try {
            algoTradeRepository.deleteById(id);
        } catch (Exception ex) {
            return Mono.just(Boolean.FALSE);
        }

        return Mono.just(Boolean.TRUE);
    }
}
