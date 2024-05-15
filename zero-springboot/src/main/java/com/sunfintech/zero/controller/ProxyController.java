package com.sunfintech.zero.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Controller
public class ProxyController {

	@RequestMapping(value = "/proxy/{register}/**", method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT})
	public void proxy(@PathVariable("register") String register, HttpServletRequest request, HttpServletResponse response) throws URISyntaxException, IOException, InterruptedException {
		System.out.println("deep dark fantasy register " + register);
		System.out.println("what method are you use " + request.getMethod());

		String requestURI = request.getRequestURI();

		HttpClient client = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(60))
				.build();

		HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(new URI("https://www.baidu.com/"))
				.method(request.getMethod(), HttpRequest.BodyPublishers.ofInputStream(() -> {
					try {
						return request.getInputStream();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}))
				.build();
		HttpResponse<InputStream> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
		IOUtils.copyLarge(httpResponse.body(), response.getOutputStream());
	}

}
