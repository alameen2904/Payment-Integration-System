package com.project.payments.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import lombok.Data;

@Data
public class HttpRequest {

	private String url;
	private HttpMethod httpMethod;
	private HttpHeaders httpHeaders;
	private Object requestData;
}