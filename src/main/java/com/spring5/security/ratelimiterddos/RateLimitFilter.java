/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security.ratelimiterddos;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RateLimitFilter implements Filter {

	@Autowired
	private ProxyManager<String> proxyManager;

	@Autowired
	private Bandwidth bandwidth;

	@Autowired
	private RateLimiterRequestInterceptor ReqInterceptor;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String apiKey = httpRequest.getHeader("X-API-KEY");
		String ipAddress = httpRequest.getRemoteAddr();
		String clientId = apiKey != null ? apiKey : ipAddress;

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String userId = req.getHeader("X-User-Id");
		if (userId == null || !ReqInterceptor.isAllowed(userId)) {
			res.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate limit exceeded");
			return;
		}
		// chain.doFilter(req, res);

		BucketConfiguration config = BucketConfiguration.builder().addLimit(bandwidth).build();
		BucketProxy bucket = proxyManager.builder().build(clientId, config);
		if (bucket.tryConsume(1)) {
			chain.doFilter(request, response);
		}
		else {
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			httpResponse.setContentType("application/json");
			httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429
			httpResponse.getWriter().write("{\"error\": \"Too many requests\", \"message\": \"Rate limit exceeded\"}");
		}
	}

}
