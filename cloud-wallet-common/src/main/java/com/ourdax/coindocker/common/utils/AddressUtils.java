package com.ourdax.coindocker.common.utils;

import java.util.regex.Pattern;

/**
 * Created by hongzong.li on 6/28/16.
 */
public class AddressUtils {


    /**校验btc bch usdt的地址*/
    public static boolean validateBTCAddr(String address){
        String s = "[1][a-zA-Z0-9]{25,34}";
        return Pattern.matches(s, address);
    }

    /**校验以太坊和erc20的地址*/
    public static boolean validateETHAddr(String address){
        String s = "0x[a-z0-9]{40}";
        return Pattern.matches(s, address);
    }

    /**校验莱特币的地址*/
    public static boolean validateLTCAddr(String address){
        String s = "L[a-zA-Z0-9]{24,33}";
        return Pattern.matches(s, address);
    }

    /**校验量子以及qrc20的地址*/
    public static boolean validateQTUMAddr(String address){
        String s = "Q[a-zA-Z0-9]{24,33}";
        return Pattern.matches(s, address);
    }

    /**校验狗狗币的地址*/
    public static boolean validateDOGEAddr(String address){
        String s = "D[a-zA-Z0-9]{24,33}";
        return Pattern.matches(s, address);
    }

    /**校验act 和 act合约的地址*/
    public static boolean validateAchainAddr(String address){
        //TODO 有没有更好的写法
        String s = "ACT[a-zA-Z0-9]{32,33}|ACT[a-zA-Z0-9]{64,65}";
        return Pattern.matches(s, address);
    }




}


