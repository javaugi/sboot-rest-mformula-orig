/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity;

/**
 * @author javau
 */
public class SoftwarePackages {

}

/*
 * CREATE TABLE software_packages ( package_id UUID PRIMARY KEY, package_name VARCHAR(100)
 * NOT NULL, package_version VARCHAR(20) NOT NULL, release_date DATE NOT NULL,
 * package_size_mb INTEGER NOT NULL, is_critical BOOLEAN DEFAULT FALSE,
 * min_hardware_version VARCHAR(20), dependencies VARCHAR(20)[] -- Array of required
 * package versions );
 */
