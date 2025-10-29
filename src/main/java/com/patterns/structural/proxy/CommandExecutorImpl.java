/*
 * Copyright (C) 2019 Center for Information Management, Inc.
 *
 * This program is proprietary.
 * Redistribution without permission is strictly prohibited.
 * For more information, contact <http://www.ciminc.com>
 */
package com.patterns.structural.proxy;

import java.io.IOException;

/**
 * @author david
 * @version $LastChangedRevision $LastChangedDate Last Modified Author: $LastChangedBy
 */
public class CommandExecutorImpl implements CommandExecutor {

	@Override
    public void runCommand(String cmd) throws Exception {
        // some heavy implementation
        String[] cmds = new String[1];
        cmds[0] = cmd;
        Runtime.getRuntime().exec(cmds);
		System.out.println("'" + cmd + "' command executed.");
	}

}
