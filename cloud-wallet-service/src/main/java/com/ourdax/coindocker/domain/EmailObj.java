package com.ourdax.coindocker.domain;

import java.io.File;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by zhangjinyang on 2018/8/21.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmailObj {

    /**
     * 发送人
     */
    private String fromUser;
    /**
     * 收件人
     */
    private List<String> toUser;
    /**
     * 抄送人
     */
    private String[] ccUser;
    /**
     * 主题
     */
    private String subject;
    /**
     * 内容
     */
    private String text;

    /**
     * 附件
     */
    private List<File> fileList;

}
