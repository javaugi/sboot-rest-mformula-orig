/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import org.apache.kafka.common.serialization.Serdes;

/**
 * @author javau
 */
public class ClickEventSerde extends Serdes.WrapperSerde<ClickEvent> {

    public ClickEventSerde() {
        super(new ClickEventSerializer(), new ClickEventDeserializer());
    }
}
