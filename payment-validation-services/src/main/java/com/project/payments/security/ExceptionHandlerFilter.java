package com.project.payments.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.payments.constant.ErrorCodeEnum;
import com.project.payments.exception.PaymentValidationException;
import com.project.payments.pojo.ErrorResponse;
import com.project.payments.util.JsonUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

	private final JsonUtil jsonUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, FilterChain filterChain)
					throws ServletException, IOException {
		try {
			log.info(" ExceptionHandlerFilter Before doFilter");

			filterChain.doFilter(request, response);

			log.info(" ExceptionHandlerFilter After doFilter");
		} catch (PaymentValidationException ex) {
			log.error(" ValidationException message is -> " + ex.getMessage());

			writeErrorResponse(response,
					ex.getErrorCode(),
					ex.getErrorMessage(),
					ex.getHttpStatus());

		} catch (Exception ex) {
			log.error(" generic exception message is -> " + ex.getMessage());

			writeErrorResponse(response,
					ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),
					ErrorCodeEnum.GENERIC_ERROR.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void writeErrorResponse(
			HttpServletResponse response, 
			String errorCode,
			String errorMessage,
			HttpStatus status) throws IOException {
		ErrorResponse errorResponse = new ErrorResponse(
				errorCode,
				errorMessage);

		log.error(" errorResponse is -> " + errorResponse);

		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(
				jsonUtil.convertObjectToJson(errorResponse));
		response.getWriter().flush();
	}
}
