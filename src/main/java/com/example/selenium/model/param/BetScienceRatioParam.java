package com.example.selenium.model.param;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 10中4（前期亏损+预计盈利-前期盈利）/剩余中奖次数/单倍盈利金额
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-10-27 11:50
 */
@Data
public class BetScienceRatioParam {

    /**
     * 前期亏损
     */
    private BigDecimal earlierStageLoss;

    /**
     * 预计盈利
     */
    private BigDecimal expectProfit;

    /**
     * 前期盈利
     */
    private BigDecimal earlierProfit;

    /**
     * 盈亏总额 = 前期盈利 - 前期亏损
     */
    private BigDecimal profitAndLossTotalAmount;

    /**
     * 剩余中奖次数
     */
    private BigDecimal residueWinningNumber;

    /**
     * 单倍盈利金额
     */
    private BigDecimal haploidProfitAmount;

    /**
     * 上期下注金额
     */
    private BigDecimal priorPeriodAmount;

    /**
     * 下注总额
     */
    private BigDecimal betTotalAmount;

    /**
     * 上期下注倍率
     */
    private BigDecimal scienceRatio;

    /**
     * 下注期数
     */
    private String qs;
}
