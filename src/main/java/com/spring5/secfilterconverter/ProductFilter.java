package com.spring5.secfilterconverter;

import com.spring5.entity.Product;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductFilter {

	private final Set<String> recalledProducts;

	public ProductFilter(Set<String> recalledProducts) {
		this.recalledProducts = recalledProducts;
	}

	public List<Product> removeRecalledFrom(Collection<Product> allProduct) {
		// return
		// allProduct.stream().filter(ProductFilter::filterByName).collect(Collectors.toList());
		return allProduct.stream().filter(this::filterByNameNew).collect(Collectors.toList());
	}

	public List<Product> doRemoveRecalledFrom(Collection<Product> allProduct) {
		return allProduct.stream().filter(prod -> filterByNameNew(prod)).collect(Collectors.toList());
	}

	public static boolean filterByName(Product product) {
		return true;
	}

	public boolean filterByNameNew(Product product) {
		return (recalledProducts != null) && !recalledProducts.contains(product.getName());
	}

}
