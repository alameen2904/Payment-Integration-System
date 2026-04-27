package com.project.payments.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.payments.constant.Constant;
import com.project.payments.service.HmacSha256Service;
import com.project.payments.util.JsonUtil; 
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HmacSha256Filter extends OncePerRequestFilter {

	private final HmacSha256Service hmacSha256Service;
	private final JsonUtil jsonUtil; 



	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		log.info("HmacSha256Filter: Processing request for URI: {}", request.getRequestURI());

		String hmacSignature = request.getHeader("Hmac-Signature");
		WrappedRequest wrappedRequest = new WrappedRequest(request);
		String jsonBody = wrappedRequest.getBody();

		String formattedJson = null;
		try {

			formattedJson = jsonUtil.prepareFormattedJson(jsonBody);
		} catch (Exception e) {
			log.error("Error while formatting JSON body: {}", e.getMessage());
			throw new AccessDeniedException("Invalid JSON body");
		}


		hmacSha256Service.isHmacSignatureValid(formattedJson, hmacSignature);

		log.info("HmacSha256Filter: HMAC validation successful");

		SecurityContext context = SecurityContextHolder.createEmptyContext();

		Authentication authentication = new HmacAuthenticationToken(Constant.MERCHANT_ID, hmacSignature, Constant.ROLE_MERCHANT);
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);

		filterChain.doFilter(wrappedRequest, response);
		log.info("HmacSha256Filter: Finished processing request for URI: {}", request.getRequestURI());
	}
}