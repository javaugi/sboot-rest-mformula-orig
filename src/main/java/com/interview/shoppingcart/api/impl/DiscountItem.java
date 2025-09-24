/*
 * Copyright (C) 2018 Center for Information Management, Inc.
 *
 * This program is proprietary.
 * Redistribution without permission is strictly prohibited.
 * For more information, contact <http://www.ciminc.com>
 */
package com.interview.shoppingcart.api.impl;

import com.interview.shoppingcart.api.Item;

/**
 *
 *
 * @author javaugi
 * @version $LastChangedRevision $LastChangedDate Last Modified Author:
 * $LastChangedBy
 */
public class DiscountItem extends ItemImpl {

    private double discount;

    public DiscountItem(String name, double price, double discount) {
        super(name, price);
        super.setCategory(Item.Category.Discount);
        this.discount = discount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

}
