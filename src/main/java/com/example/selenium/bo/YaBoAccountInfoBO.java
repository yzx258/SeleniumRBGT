package com.example.selenium.bo;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-20 14:04:17
 * @description: 描述
 */
@Data
public class YaBoAccountInfoBO {

    /**
     * 联赛名称
     */
    private String competitionName;

    /**
     * 单 - 0；双 - 1
     */
    private Integer singleOrDouble;

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

    /**
     * 操作类型
     */
    private String operateName;

    /**
     * 比赛倍率
     */
    private BigDecimal competitionMagnification;
}
