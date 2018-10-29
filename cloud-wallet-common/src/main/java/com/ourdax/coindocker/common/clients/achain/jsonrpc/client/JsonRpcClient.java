package com.ourdax.coindocker.common.clients.achain.jsonrpc.client;

import com.ourdax.coindocker.common.clients.achain.AchainClientException;
import com.ourdax.coindocker.common.clients.achain.jsonrpc.JsonMapper;
import com.ourdax.coindocker.common.clients.achain.jsonrpc.JsonPrimitiveParser;
import java.util.List;

public interface JsonRpcClient {

	String execute(String method) throws AchainClientException;
	
	<T> String execute(String method, T param) throws AchainClientException;
	
	<T> String execute(String method, List<T> params) throws AchainClientException;
	
	JsonPrimitiveParser getParser();

	JsonMapper getMapper();
	
	void close();
}