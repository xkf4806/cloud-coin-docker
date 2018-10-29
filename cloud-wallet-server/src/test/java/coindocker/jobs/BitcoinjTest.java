package coindocker.jobs;

import java.io.File;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.junit.Test;

/**
 * Created by zhangjinyang on 2018/9/20.
 */
public class BitcoinjTest {

  @Test
  public void testAddress()
      throws InsufficientMoneyException, InterruptedException, BlockStoreException, UnknownHostException {
    // TODO: Assumes main network not testnet. Make it selectable.
    NetworkParameters params = MainNetParams.get();
    // Decode the private key from Satoshis Base58 variant. If 51 characters long then it's from Bitcoins
    // dumpprivkey command and includes a version byte and checksum, or if 52 characters long then it has
    // compressed pub key. Otherwise assume it's a raw key.
    ECKey key;
    String keyStr = "KxcGKRiRubenjLzBW3crD5WGfm9m2aifP1dE9aNiB91kNRRJCzw7";
    if (keyStr.length() == 51 || keyStr.length() == 52) {
      DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, keyStr);
      key = dumpedPrivateKey.getKey();
    } else {
      BigInteger privKey = Base58.decodeToBigInteger(keyStr);
      key = ECKey.fromPrivate(privKey);
    }
    System.out.println(key.toAddress(params).toBase58());

  }

  @Test
  public void testCreateNewAddress() throws BlockStoreException {

    NetworkParameters params = MainNetParams.get();
    Wallet wallet = new Wallet(params);

    Address address = wallet.currentReceiveAddress();
    System.out.println(address.toBase58());

  }

  @Test
  public void testGetPubKeyAndPrivKey(){

    NetworkParameters params = MainNetParams.get();
//    Address address = Address.fromBase58(params, "1CWsug7JGCtPyZFQHJFGAS1WCaMwPRc8Mn");
    ECKey key = new ECKey();
    System.out.println("privatekey: " + key.getPrivateKeyAsWiF(params));
    System.out.println("address: " + key.toAddress(params));

  }

  @Test
  public void testForRandomUse(){
    System.out.println("L5NXNGe6QwH7g5A6VQ3RW9v5v7VsKkEUUmL7nTEKoNWZ89x9fyzG".length());
  }

}
