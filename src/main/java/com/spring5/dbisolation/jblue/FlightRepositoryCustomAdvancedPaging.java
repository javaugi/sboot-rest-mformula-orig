/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import java.util.List;

/**
 *
 * @author javau
 */
public interface FlightRepositoryCustomAdvancedPaging {

    List<FlightEvent> findLargeDatasetWithPagination(String query, int maxItemCount, String continuationToken);

    void processLargeDatasetInParallel(String query, int batchSize, int parallelism);
}
