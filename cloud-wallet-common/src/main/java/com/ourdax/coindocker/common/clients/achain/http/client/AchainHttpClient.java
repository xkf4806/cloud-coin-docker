package com.ourdax.coindocker.common.clients.achain.http.client;

import com.ourdax.coindocker.common.clients.achain.AchainClientException;

public interface AchainHttpClient {
	
	String execute(String reqMethod, String reqPayload) throws AchainClientException;
	
	void close();
}