/*
 * Copyright (C) 2018 Center for Information Management, Inc.
 *
 * This program is proprietary.
 * Redistribution without permission is strictly prohibited.
 * For more information, contact <http://www.ciminc.com>
 */
package com.interview.hrank;

import com.interview.shoppingcart.api.Receipt;
import com.interview.shoppingcart.api.impl.ItemImpl;
import com.interview.shoppingcart.api.impl.ReceiptImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author javaugi
 * @version $LastChangedRevision $LastChangedDate Last Modified Author: $LastChangedBy
 */
public class ReceiptTest {

	Receipt rec = null;

	@Before
	public void setup() {
		rec = new ReceiptImpl();
	}

	@Test
	public void whenAdd3ThenPriceTotal3Times() {
		rec.addItem(new ItemImpl("ID001", 1.5));
		rec.addItem(new ItemImpl("ID001", 1.5));
		rec.addItem(new ItemImpl("ID001", 1.5));
		Assert.assertTrue(4.5 == rec.getTotal());
	}

}
