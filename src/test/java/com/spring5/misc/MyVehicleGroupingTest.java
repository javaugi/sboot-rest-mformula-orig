/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.misc;

import com.interview.hrank.sort.MyVehicleGrouping;
import com.interview.hrank.sort.MyVehicleGrouping.Vehicle;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MyVehicleGroupingTest {


    private final MyVehicleGrouping myTest = new MyVehicleGrouping();
    List<Vehicle> list = myTest.getVehicles();

    //@Test
    public void testVehcileMakeModel() {
        Integer[] rtn = myTest.countMakeModels(list);
        assertTrue(rtn[0] == 2 && rtn[1] == 7);
    }
}
