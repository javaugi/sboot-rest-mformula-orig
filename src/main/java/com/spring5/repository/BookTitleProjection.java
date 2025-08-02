/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.repository;

// 3. Using Spring Data JPA Projections
public interface BookTitleProjection {
    String getTitle();
    String getAuthor();
}