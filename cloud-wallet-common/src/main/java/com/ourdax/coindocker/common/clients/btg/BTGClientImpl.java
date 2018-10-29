package com.ourdax.coindocker.common.clients.btg;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.Commands;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.ClientConfigurator;
import com.neemre.btcdcli4j.core.domain.SinceBlock;
import com.neemre.btcdcli4j.core.domain.Transaction;
import com.neemre.btcdcli4j.core.jsonrpc.client.JsonRpcClient;
import com.neemre.btcdcli4j.core.jsonrpc.client.JsonRpcClientImpl;
import com.neemre.btcdcli4j.core.util.CollectionUtils;
import com.ourdax.coindocker.common.clients.btg.pojo.BTGBlock;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

/**
 * @author xinj.x
 */
@Slf4j
public class BTGClientImpl implements BTGClient {

    private ClientConfigurator configurator;
    private final JsonRpcClient rpcClient;

    public BTGClientImpl(CloseableHttpClient httpProvider, Properties nodeConfig) throws BitcoindException, CommunicationException  {
        initialize();
        rpcClient = new JsonRpcClientImpl(configurator.checkHttpProvider(httpProvider),
                configurator.checkNodeConfig(nodeConfig));

    }

    public BTGClientImpl(Properties clientConfig) throws BitcoindException, CommunicationException  {
        this(null, clientConfig);
    }

    @Override
    public String getBestBlockHash() throws BitcoindException, CommunicationException {
        String resp = rpcClient.execute(Commands.GET_BEST_BLOCK_HASH.getName());
        return rpcClient.getParser().parseString(resp);
    }

    @Override
    public BTGBlock getBlock(String blockHash) throws BitcoindException, CommunicationException {
        String respJson = rpcClient.execute(Commands.GET_BLOCK.getName(), blockHash);
        return rpcClient.getMapper().mapToEntity(respJson, BTGBlock.class);
    }

    @Override
    public Long getBlockCount() throws BitcoindException, CommunicationException {
        String respJson = rpcClient.execute(Commands.GET_BLOCK_COUNT.getName());
        return rpcClient.getParser().parseLong(respJson);
    }

    @Override
    public BigDecimal getBalance() throws BitcoindException, CommunicationException {
        String balanceJson = rpcClient.execute(Commands.GET_BALANCE.getName());
        return rpcClient.getParser().parseBigDecimal(balanceJson);
    }

    @Override
    public Transaction getTransaction(String txId, Boolean withWatchOnly) throws BitcoindException, CommunicationException {
        List<Object> params = CollectionUtils.asList(txId, withWatchOnly);
        String transactionJson = rpcClient.execute(Commands.GET_TRANSACTION.getName(), params);
        return rpcClient.getMapper().mapToEntity(transactionJson, Transaction.class);
    }

    @Override
    public String sendToAddress(String address, BigDecimal amount) throws BitcoindException, CommunicationException {
        List<Object> params = CollectionUtils.asList(address, amount);
        String transactionIdJson = rpcClient.execute(Commands.SEND_TO_ADDRESS.getName(), params);
        return rpcClient.getParser().parseString(transactionIdJson);
    }

    @Override
    public SinceBlock listSinceBlock() throws BitcoindException, CommunicationException {
        String sinceBlockJson = rpcClient.execute(Commands.LIST_SINCE_BLOCK.getName());
        return rpcClient.getMapper().mapToEntity(sinceBlockJson, SinceBlock.class);
    }

    @Override
    public SinceBlock listSinceBlock(String headerHash) throws BitcoindException,
            CommunicationException {
        String sinceBlockJson = rpcClient.execute(Commands.LIST_SINCE_BLOCK.getName(), headerHash);
        return rpcClient.getMapper().mapToEntity(sinceBlockJson, SinceBlock.class);
    }

    @Override
    public SinceBlock listSinceBlock(String headerHash, Integer confirmations)
            throws BitcoindException, CommunicationException {
        List<Object> params = CollectionUtils.asList(headerHash, confirmations);
        String sinceBlockJson = rpcClient.execute(Commands.LIST_SINCE_BLOCK.getName(), params);
        return rpcClient.getMapper().mapToEntity(sinceBlockJson, SinceBlock.class);
    }

    @Override
    public SinceBlock listSinceBlock(String headerHash, Integer confirmations,
                                     Boolean withWatchOnly) throws BitcoindException, CommunicationException {
        List<Object> params = CollectionUtils.asList(headerHash, confirmations, withWatchOnly);
        String sinceBlockJson = rpcClient.execute(Commands.LIST_SINCE_BLOCK.getName(), params);
        return rpcClient.getMapper().mapToEntity(sinceBlockJson, SinceBlock.class);
    }

    private void initialize() {
        log.info(">> initialize(..): initiating the 'bitcoind' core wrapper");
        configurator = new ClientConfigurator();
    }

}
