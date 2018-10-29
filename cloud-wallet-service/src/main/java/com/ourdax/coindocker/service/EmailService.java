package com.ourdax.coindocker.service;

import com.ourdax.coindocker.domain.EmailObj;

/**
 * Created by zhangjinyang on 2018/8/21.
 */
public interface EmailService {

  void sendEmail(EmailObj emailObj);

}
