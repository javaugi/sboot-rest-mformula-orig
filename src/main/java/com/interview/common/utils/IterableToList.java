/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author javaugi
 */
public class IterableToList {

	public static <T> List<T> iterableToList(Iterable<T> iterable) {
		List<T> list = new ArrayList<>();
		iterable.forEach(list::add);
		return list;
	}

	public static <T> List<T> arrayToList(T[] arr) {
		List<T> list = new ArrayList<>();
		Arrays.stream(arr).forEach(list::add);
		Arrays.stream(arr).collect(Collectors.toList());
		return list;
	}

}
