package com.example.selenium.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-19 23:34:08
 * @description: 描述
 */
@Data
public class YaBoInfoBO {

    /**
     * 联赛名称
     */
    private String competitionName;

    /**
     * 主队名称
     */
    private String homeTeamName;

    /**
     * 客队名称
     */
    private String awayTeamName;

    /**
     * 第几节
     */
    private String whichSection;

    /**
     * 场次
     */
    private Integer screenings;

    /**
     * 原始金额
     */
    private BigDecimal originalAmount;

    /**
     * 剩余金额
     */
    private BigDecimal balanceAmount;

    /**
     * 投注金额
     */
    private BigDecimal betAmount;
}
