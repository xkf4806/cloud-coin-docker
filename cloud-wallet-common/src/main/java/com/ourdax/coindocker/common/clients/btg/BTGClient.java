package com.ourdax.coindocker.common.clients.btg;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.domain.SinceBlock;
import com.neemre.btcdcli4j.core.domain.Transaction;
import com.ourdax.coindocker.common.clients.btg.pojo.BTGBlock;

import java.math.BigDecimal;

/**
 * @author xinj.x
 */
public interface BTGClient {

    String getBestBlockHash() throws BitcoindException, CommunicationException;

    BTGBlock getBlock(String blockHash) throws BitcoindException, CommunicationException;

    /**
     * 获取区块链最大高度
     * @return
     * @throws BitcoindException
     * @throws CommunicationException
     */
    Long getBlockCount()throws BitcoindException, CommunicationException;

    BigDecimal getBalance() throws BitcoindException, CommunicationException;

    Transaction getTransaction(String txId, Boolean withWatchOnly) throws BitcoindException, CommunicationException;

    /**
     * 给指定地址充币
     * @param address
     * @param amount
     * @return
     * @throws BitcoindException
     * @throws CommunicationException
     */
    String sendToAddress(String address, BigDecimal amount) throws BitcoindException, CommunicationException;

    SinceBlock listSinceBlock() throws BitcoindException, CommunicationException;

    SinceBlock listSinceBlock(String headerHash) throws BitcoindException, CommunicationException;

    SinceBlock listSinceBlock(String headerHash, Integer confirmations) throws BitcoindException,
            CommunicationException;

    SinceBlock listSinceBlock(String headerHash, Integer confirmations, Boolean withWatchOnly)
            throws BitcoindException, CommunicationException;
}
