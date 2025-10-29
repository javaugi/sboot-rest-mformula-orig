/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dto;

public class CursorPageRequest {

    private String cursor;
    private int limit = 10;
    private Direction direction = Direction.NEXT;

    public enum Direction {
        NEXT, PREVIOUS
    }

    // Constructors, getters, setters
    public CursorPageRequest() {
    }

    public CursorPageRequest(String cursor, int limit, Direction direction) {
        this.cursor = cursor;
        this.limit = limit;
        this.direction = direction;
    }

    // Getters and setters...
    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
