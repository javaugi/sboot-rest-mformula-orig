/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.proactivecachepopu;

import java.util.Collection;
import java.util.Collections;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.stereotype.Component;

@Component
public class DynamicCacheResolver implements CacheResolver {

	private final CacheManager cacheManager;

	public DynamicCacheResolver(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
		String regionName = CacheRegionContextHolder.getRegion();
		if (regionName != null) {
			Cache cache = cacheManager.getCache(regionName);
			if (cache != null) {
				return Collections.singletonList(cache);
			}
		}
		// Fallback to default or other logic if no region is set
		return Collections.singletonList(cacheManager.getCache("defaultCache"));
	}

}
