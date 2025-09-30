/*
 * Copyright (C) 2019 Center for Information Management, Inc.
 *
 * This program is proprietary.
 * Redistribution without permission is strictly prohibited.
 * For more information, contact <http://www.ciminc.com>
 */
package com.patterns.structural.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GOF: Provide a surrogate or placeholder for another object to control access
 * to it.
 *
 * <p>
 * Provide a surrogate or placeholder for another object to control access to
 * it.
 *
 * <p>
 * Allows for object level access control by acting as a pass through entity or
 * a placeholder object.
 *
 * <p>
 * The definition itself is very clear and proxy design pattern is used when we
 * want to provide controlled access of a functionality.
 *
 * <p>
 * Proxy design pattern common uses are to control access or to provide a
 * wrapper implementation for better performance.
 *
 * <p>
 * In proxy pattern, a class represents functionality of another class. This
 * type of design pattern comes under structural pattern.
 *
 * <p>
 * In proxy pattern, we create object having original object to interface its
 * functionality to outer world.
 *
 * <p>
 * Proxy pattern is used when we need to create a wrapper to cover the main
 * object’s complexity from the client.
 *
 * <p>
 * Types of proxies
 *
 * <p>
 * Remote proxy:
 *
 * <p>
 * They are responsible for representing the object located remotely. Talking to
 * the real object might involve marshalling and unmarshalling of data and
 * talking to the remote object. All that logic is encapsulated in these proxies
 * and the client application need not worry about them.
 *
 * <p>
 * Virtual proxy:
 *
 * <p>
 * These proxies will provide some default and instant results if the real
 * object is supposed to take some time to produce results. These proxies
 * initiate the operation on real objects and provide a default result to the
 * application. Once the real object is done, these proxies push the actual data
 * to the client where it has provided dummy data earlier.
 *
 * <p>
 * Protection proxy:
 *
 * <p>
 * If an application does not have access to some resource then such proxies
 * will talk to the objects in applications that have access to that resource
 * and then get the result back.
 *
 * <p>
 * Smart Proxy:
 *
 * <p>
 * A smart proxy provides additional layer of security by interposing specific
 * actions when the object is accessed. An example can be to check if the real
 * object is locked before it is accessed to ensure that no other object can
 * change it.
 *
 * @author david
 * @version $LastChangedRevision $LastChangedDate Last Modified Author:
 * $LastChangedBy
 */
public class ProxyPattern {

    private static final Logger log = LoggerFactory.getLogger(ProxyPattern.class);

    public static void main(String[] args) {
        System.out.println("testing image proxy ...");
        Image image = new ProxyImage("test_10mb.jpg");
        // image will be loaded from disk
        image.display();
        // image will not be loaded from disk
        image.display();

        System.out.println("\n\n testing internet proxy ...");
        //
        Internet internet = new ProxyInternet();
        try {
            // will be connected
            internet.connectTo("geeksforgeeks.org");
            // will not be connected
            internet.connectTo("abc.com");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("\n\n testing command proxy ...");

        CommandExecutor executor = new CommandExecutorProxy("Pankaj", "wrong_pwd");
        try {
            executor.runCommand("ls -ltr");
            executor.runCommand(" rm -rf abc.pdf");
        } catch (Exception e) {
            System.out.println("Exception Message::" + e.getMessage());
        }
    }
}
