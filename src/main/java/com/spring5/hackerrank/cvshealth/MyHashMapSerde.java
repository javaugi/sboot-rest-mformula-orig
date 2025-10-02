/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import java.util.Map;
import org.apache.kafka.common.serialization.Serdes;

/**
 * @author javau
 */
public class MyHashMapSerde extends Serdes.WrapperSerde<Map<String, Long>> {

	public MyHashMapSerde() {
		super(new HashMapSerializer(), new HashMapDeserializer());
	}

}
