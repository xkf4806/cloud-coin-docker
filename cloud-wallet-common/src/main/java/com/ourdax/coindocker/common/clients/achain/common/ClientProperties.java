package com.ourdax.coindocker.common.clients.achain.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum ClientProperties {
    RPC_PROTOCOL("achain.rpc.protocol", "http"),
    RPC_HOST("achain.rpc.host", "127.0.0.1"),
    RPC_PORT("achain.rpc.port", "8332"),
    RPC_USER("achain.rpc.user", "user"),
    RPC_PASSWORD("achain.rpc.password", "password"),
    HTTP_AUTH_SCHEME("achain.http.auth_scheme", "Basic");
	
    private final String key;
    private final String defaultValue;
}