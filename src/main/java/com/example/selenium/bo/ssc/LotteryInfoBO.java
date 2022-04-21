package com.example.selenium.bo.ssc;

import lombok.Data;

/**
 * @author 俞春旺
 * @program: selenium
 * @date 2022-04-19 23:00:05
 * @description: 描述
 */
@Data
public class LotteryInfoBO {

    /**
     * 期数
     */
    private String period;

    /**
     * 个
     */
    private String singleDigit;
    /**
     * 十
     */
    private String ten;
    /**
     * 百
     */
    private String hundreds;
    /**
     * 千
     */
    private String thousands;
    /**
     * 万
     */
    private String tenThousand;

    private Integer bl;

}
