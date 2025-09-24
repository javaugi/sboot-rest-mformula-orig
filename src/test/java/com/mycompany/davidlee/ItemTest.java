/*
 * Copyright (C) 2018 Center for Information Management, Inc.
 *
 * This program is proprietary.
 * Redistribution without permission is strictly prohibited.
 * For more information, contact <http://www.ciminc.com>
 */
package com.mycompany.davidlee;

import org.junit.Assert;
import org.junit.Test;

import com.interview.shoppingcart.api.Item;
import com.interview.shoppingcart.api.Item.Category;
import com.interview.shoppingcart.api.impl.ItemImpl;
import com.interview.shoppingcart.api.impl.WeightItem;
import com.spring5.ProjectTest;


/**
 *
 *
 * @author javaugi
 * @version $LastChangedRevision $LastChangedDate Last Modified Author:
 * $LastChangedBy
 */
public class ItemTest extends ProjectTest {

    @Test
    public void whenScannedItemThenCategoryScanned() {
        Item item = new ItemImpl("ID001", 1.25);
        Assert.assertTrue(item.getCategory().equals(Category.Bagged));
    }

    @Test
    public void whenWeightItemThenCategoryWeight() {
        Item item = new WeightItem("ID001", 0.5, 1.25);
        Assert.assertTrue(item.getCategory().equals(Category.Weight));
    }
}
