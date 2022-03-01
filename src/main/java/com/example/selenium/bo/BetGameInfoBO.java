package com.example.selenium.bo;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-26 22:47:16
 * @description: 描述
 */
@Data
public class BetGameInfoBO {

    private String lockId;

    /**
     * 操作线程：0-A线程;1-B线程；2-C线程
     */
    private Integer operateThreadType;

    /**
     * 联赛名称
     */
    private String competitionName;

    /**
     * 主队名称
     */
    private String homeTeamName;

    /**
     * 主队名称比分
     */
    private String homeTeamScore;

    /**
     * 客队名称
     */
    private String awayTeamName;

    /**
     * 客队名称比分
     */
    private String awayTeamScore;

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
     * 是否结算：0-待结算；1-已结算
     */
    private Integer isSettlement;

    /**
     * 比赛结果：2-黑；1-红
     */
    private Integer competitionResult;

    /**
     * 比赛倍率
     */
    private BigDecimal competitionMagnification;

    /**
     * 比赛红利
     */
    private BigDecimal competitionDividendAmount;

    /**
     * 比赛类型：0-单双；1-待定
     */
    private Integer competitionType;
}
