/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql.yhsports;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import static com.spring5.RedisConfig.REDIS_TPL_GAME;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GameQueryResolver implements GraphQLQueryResolver {

    private final SportsGameRepository repo;
    private final @Qualifier(REDIS_TPL_GAME)
    RedisTemplate<String, SportsGame> redis;

    @QueryMapping
    public SportsGame getSportsGame(Long id) {
        SportsGame game = this.getResponse(id);
        if (game != null) {
            return game;
        }

        game = repo.findById(id).orElseThrow();
        saveResponse(id, game, Duration.ofHours(1));
        return game;
	}

    public Boolean saveResponse(Long key, SportsGame responseJson, Duration ttl) {
        Boolean ok = redis.opsForValue().setIfAbsent(key.toString(), responseJson, ttl);
        return Boolean.TRUE.equals(ok);
        //redis.opsForValue().set(key + ":game", responseJson);
    }

    public SportsGame getResponse(Long key) {
        return redis.opsForValue().get(key + ":game");
    }

    @QueryMapping
    public List<SportsGame> liveGames() {
        return repo.findAll();
    }

    // This method resolves the 'author' field within the 'Book' type.
    // The 'book' argument is the parent object (the Book that was just fetched).
    @SchemaMapping
    public SportsScore sportsScore(Long gameId) {
        System.out.println("Resolving score for game ID: " + gameId + ")");
        SportsGame game = this.getSportsGame(gameId);
        return game.getSportsScore();
    }
}
