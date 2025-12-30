/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql.yhsports;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.ToString;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@Entity
@Table(name = "sports_games")
public class SportsGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;
    @Column(name = "home_team")
    private String homeTeam;
    @Column(name = "away_team")
    private String awayTeam;
    private String status;

    @jakarta.persistence.OneToOne(cascade = CascadeType.ALL)
    @jakarta.persistence.JoinColumn(name = "sportsScore_id") // This defines the foreign key column in the 'Person' table
    private SportsScore sportsScore;

}
