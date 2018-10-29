package com.ourdax.coindocker.asset.bitcoin.btg;

import com.google.common.util.concurrent.MoreExecutors;
import com.neemre.btcdcli4j.core.domain.PaymentOverview;
import com.neemre.btcdcli4j.core.domain.SinceBlock;
import com.neemre.btcdcli4j.core.domain.Transaction;
import com.neemre.btcdcli4j.core.domain.enums.PaymentCategories;
import com.ourdax.coindocker.block.*;
import com.ourdax.coindocker.common.base.AssetException;
import com.ourdax.coindocker.common.base.RpcCallTemplate;
import com.ourdax.coindocker.common.clients.btg.BTGClient;
import com.ourdax.coindocker.common.clients.btg.BTGClientImpl;
import com.ourdax.coindocker.common.clients.btg.pojo.BTGBlock;
import com.ourdax.coindocker.rpc.AbstractRpcProcessor;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author xinj.x
 */
public abstract class BTGBasedRpcProcessor extends AbstractRpcProcessor {

    protected abstract BTGClient getClient();

    protected abstract String getDepositAccount();

    protected abstract Integer getConfirmThreshold();

    @Override
    public BigDecimal queryChainMajorTokenBalance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal queryChainMajorTokenBalance(String inAddress) {
        throw new UnsupportedOperationException();
    }

    /**btc系列不支持使用指定account查询 */
    @Override
    public BigDecimal queryBalance(String account) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUniformAccount() {
        return getDepositAccount();
    }

    @Override
    public RpcTransResponse transferFrom(String from, String to, BigDecimal amount) {
        throw new UnsupportedOperationException();
    }

    protected BTGClient buildClient(String host, Integer port, String user, String password) {
        Properties clientProperties = new Properties();
        clientProperties.setProperty("node.bitcoind.rpc.protocol", "http");
        clientProperties.setProperty("node.bitcoind.rpc.host", host);
        clientProperties.setProperty("node.bitcoind.rpc.port", String.valueOf(port));
        clientProperties.setProperty("node.bitcoind.rpc.user", user);
        clientProperties.setProperty("node.bitcoind.rpc.password", password);
        clientProperties.setProperty("node.bitcoind.http.auth_scheme", "Basic");
        try {
            return new BTGClientImpl(clientProperties);
        } catch (Exception e) {
            throw new RuntimeException("Init BTGClient error", e);
        }
    }

    @Override
    public Block getLatestBlock() {
        String blockHash = new RpcCallTemplate<>(() -> getClient().getBestBlockHash()).execute();
        return queryBlockByHash(blockHash);
    }

    private Block queryBlockByHash(String blockHash) {
        BTGBlock block = new RpcCallTemplate<>(() -> getClient().getBlock(blockHash)).execute();
        return new SimpleBlock(String.valueOf(block.getHeight()), blockHash);
    }

    /**
     * this method has not used yet so far,
     * it doesn't allow to get transactions that don't belong to the node in loop
     * @param block
     * @return
     */
    @Override
    public BlockTrans queryTrans(Block block) {
        BTGBlock btgBlock = new RpcCallTemplate<>(() -> getClient().getBlock(block.getBlockHash())).execute();
        List<CompletableFuture<List<TransInfo>>> completableFutures = btgBlock.getTx().stream()
                .map(txId -> CompletableFuture
                        .supplyAsync(() -> queryTransInfo(txId), MoreExecutors.directExecutor()))
                .collect(Collectors.toList());
        CompletableFuture<Void> futureResults = CompletableFuture.allOf(
                completableFutures.toArray(new CompletableFuture[0]));
        CompletableFuture<List<TransInfo>> transInfosFuture = futureResults.thenApply(v ->
                completableFutures.stream()
                        .flatMap(future -> future.join().stream())
                        .collect(Collectors.toList())
        );
        List<TransInfo> transInfos = transInfosFuture.join();
        BlockTrans result = new BlockTrans();
        result.setBlock(new SimpleBlock(String.valueOf(btgBlock.getHeight()), block.getBlockHash()));
        result.setTrans(transInfos);
        return result;
    }

    @Override
    public List<TransInfo> queryTransInfo(String txId) {
        Transaction transaction = new RpcCallTemplate<>(() -> getClient().getTransaction(txId, true))
                .execute();
        return transaction.getDetails().stream()
                .map(payment -> adaptTransInfo(payment, transaction))
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal queryBalance() {
        return new RpcCallTemplate<>(() -> getClient().getBalance()).execute();
    }

    @Override
    public BlockTrans queryTransSince(Block block) {
        SinceBlock sinceBlock = new RpcCallTemplate<>(
                () -> getClient().listSinceBlock(block.getBlockHash(), 1, true)).execute();
        List<TransInfo> transInfos = sinceBlock.getPayments().stream()
                .filter(this::isTransferInType)
                .map(this::adaptTransInfo)
                .collect(Collectors.toList());

        BlockTrans result = new BlockTrans();
        result.setBlock(queryBlockByHash(sinceBlock.getLastBlock()));
        result.setTrans(transInfos);
        return result;
    }

    @Override
    public TransStatus getTransStatus(TransInfo transInfo) {
        if (transInfo.getConfirmNum() > getConfirmThreshold()) {
            return TransStatus.COMPLETED;
        } else {
            return TransStatus.CONFIRMING;
        }
    }

    @Override
    public void preTransfer(RpcTransRequest rpcTransRequest) {
        ensureFundSufficient(queryBalance(), rpcTransRequest.getAmount());
        unlockWallet();
    }

    private void ensureFundSufficient(BigDecimal balance, BigDecimal request) {
        if (balance.compareTo(request) < 0) {
            String msg = String.format("insufficient fund, %s remaining %s", getAssetCode(), balance);
            throw new AssetException(msg);
        }
    }

    private void unlockWallet() {}

    @Override
    public RpcTransResponse defaultTransfer(RpcTransRequest request) {
        String txId = new RpcCallTemplate<>(
                () -> getClient().sendToAddress(request.getTo(), request.getAmount())).execute();
        RpcTransResponse response = new RpcTransResponse();
        response.setTxId(txId);
        return response;
    }

    private boolean isTransferInType(PaymentOverview payment) {
        return payment.getCategory() == PaymentCategories.RECEIVE;
    }

}
