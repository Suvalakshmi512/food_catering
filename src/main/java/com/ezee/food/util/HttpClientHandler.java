package com.ezee.food.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.ezee.food.Exception.ErrorCode;
import com.ezee.food.Exception.ServiceException;
import com.ezee.food.dto.AuthDTO;
import com.ezee.food.dto.AuthResponseDTO;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpClientHandler {

	private final Gson gson = new Gson();
	public static final Logger LOGGER = LogManager.getLogger("com.ezee.food");

	public AuthResponseDTO authenticateViaThirdPartyPost(AuthDTO authDTO, String authUrl) {
		AuthResponseDTO authResponse = null;
		try {
			String jsonRequest = gson.toJson(authDTO);
			HttpPost post = new HttpPost(authUrl);
			post.setHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(jsonRequest, ContentType.APPLICATION_JSON));

			RequestConfig config = RequestConfig.custom().setConnectTimeout(15000).setSocketTimeout(15000).build();
			post.setConfig(config);

			try (CloseableHttpClient client = HttpClients.createDefault();
					CloseableHttpResponse response = client.execute(post)) {

				int code = response.getStatusLine().getStatusCode();
				String jsonResponse = EntityUtils.toString(response.getEntity());

				if (code != 200) {
					LOGGER.warn("Auth API returned HTTP {}: {}", code, jsonResponse);
					throw new ServiceException(ErrorCode.STATUS_NOT_ACCEPTABLE, "Auth API HTTP error " + code);
				}

				authResponse = gson.fromJson(jsonResponse, AuthResponseDTO.class);

				if (authResponse == null || authResponse.getAccessToken() == null) {
					LOGGER.warn("Auth API response missing accessToken: {}", jsonResponse);
					throw new ServiceException(ErrorCode.SERVICE_UNAVAILABLE, "Invalid auth API response");
				}
			}

		} catch (Exception e) {
			LOGGER.error("Error calling third-party auth API: {}", e.getMessage(), e);
			throw new ServiceException(ErrorCode.SERVICE_UNAVAILABLE, "Auth API error");
		}
		return authResponse;
	}
}
