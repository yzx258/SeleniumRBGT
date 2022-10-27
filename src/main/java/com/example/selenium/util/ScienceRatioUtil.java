package com.example.selenium.util;

import java.math.BigDecimal;
import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.example.selenium.model.param.BetScienceRatioParam;

import lombok.extern.slf4j.Slf4j;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-10-27 11:49
 */
@Slf4j
public class ScienceRatioUtil {

    public static void main(String[] args) {
        BetScienceRatioParam param = new BetScienceRatioParam();
        param.setEarlierStageLoss(new BigDecimal("0"));
        param.setExpectProfit(new BigDecimal("18"));
        param.setEarlierProfit(BigDecimal.ZERO);
        param.setResidueWinningNumber(new BigDecimal("4"));
        param.setHaploidProfitAmount(new BigDecimal("4.7"));

        BigDecimal betScienceRatio = getBetScienceRatio(param);
        System.out.println("betScienceRatio:" + betScienceRatio);

    }

    /***
     * 倍数计算公式(beishu)：（前期亏损+预计盈利-前期盈利）/剩余中奖次数/单倍盈利金额;
     * 
     * @param param
     *            入参
     * @return java.math.BigDecimal
     * @author yucw
     * @date 2022-10-27 11:52
     */
    public static BigDecimal getBetScienceRatio(BetScienceRatioParam param) {
        // 前期亏损
        BigDecimal earlierStageLoss = param.getEarlierStageLoss();
        // 预计盈利
        BigDecimal expectProfit = param.getExpectProfit();
        // 前期盈利
        BigDecimal earlierProfit = param.getEarlierProfit();
        // 剩余中奖次数
        BigDecimal residueWinningNumber = param.getResidueWinningNumber();
        // 单倍盈利金额
        BigDecimal haploidProfitAmount = param.getHaploidProfitAmount();
        log.info("前期亏损:{},预计盈利:{},前期盈利:{},剩余中奖次数:{},单倍盈利金额:{}", earlierStageLoss, expectProfit, earlierProfit,
            residueWinningNumber, haploidProfitAmount);

        // 计算 - 合计数据
        BigDecimal totalAmount = earlierStageLoss.add(expectProfit).subtract(earlierProfit);
        // 计算 - 倍率
        BigDecimal scienceRatio = totalAmount.divide(residueWinningNumber, 1, BigDecimal.ROUND_HALF_UP)
            .divide(haploidProfitAmount, 1, BigDecimal.ROUND_HALF_UP);
        log.info("投注赔率:{}", scienceRatio);
        return scienceRatio;
    }

    /***
     * 当期投入公式：原始投注 * beishu
     * 
     * @param betAmount
     *            原始投注金额
     * @param scienceRatio
     *            倍率
     * @return java.math.BigDecimal
     * @author yucw
     * @date 2022-10-27 12:34
     */
    public static BigDecimal getBetAmount(BigDecimal betAmount, BigDecimal scienceRatio) {
        return betAmount.multiply(scienceRatio);
    }

    /***
     * 模拟下注
     * 
     * @param param
     * @return void
     * @author yucw
     * @date 2022-10-27 13:43
     */
    public static BetScienceRatioParam testBet(BetScienceRatioParam param) {
        // 获取 - 开奖结果
        Boolean result = testResult();
        if (result) {
            // 第一期开奖或是，则跳出
            log.info("第一期中，执行下一次10期");
            param.setEarlierStageLoss(BigDecimal.ZERO);
            param.setEarlierProfit(param.getHaploidProfitAmount());
            param.setProfitAndLossTotalAmount(param.getHaploidProfitAmount());
            param.setQs("第1期下注");
            return param;
        } else {
            for (int i = 2; i < 10000; i++) {
                log.info("第" + i + "期下注");
                param.setQs("第" + i + "期下注");
                if (i == 2) {
                    // 获取 - 下注倍率
                    BigDecimal scienceRatio = getBetScienceRatio(param);
                    // 获取 - 下注金额
                    BigDecimal betAmount = getBetAmount(new BigDecimal(5), scienceRatio);
                    log.info("投注金额:{}", betAmount);
                    // 设置 - 上期金额
                    param.setPriorPeriodAmount(betAmount);
                    // 设置 - 下注总额
                    param.setBetTotalAmount(param.getBetTotalAmount().add(betAmount));
                    // 设置 - 最新倍率
                    param.setScienceRatio(scienceRatio);
                    log.info("param -> {}", JSON.toJSONString(param));
                    log.info("--------------------------------");
                    continue;
                }

                if (testResult()) {
                    log.info("中奖");
                    // 中奖
                    // 设置 - 剩余中奖次数
                    param.setResidueWinningNumber(param.getResidueWinningNumber().subtract(new BigDecimal(1)));
                    // 设置 - 前期盈利
                    param.setEarlierProfit(
                        param.getEarlierProfit().add(param.getScienceRatio().multiply(param.getHaploidProfitAmount())));
                    // 设置 - 盈亏总额 = 前期盈利 - 前期亏损
                    param.setProfitAndLossTotalAmount(param.getEarlierProfit().subtract(param.getEarlierStageLoss()));
                    // 校验 - 是否完结计划
                    if (param.getResidueWinningNumber().compareTo(BigDecimal.ZERO) == 0) {
                        log.info("param -> {}", JSON.toJSONString(param));
                        log.info("--------------------------------");
                        log.info("10中4任务结束,执行下次操作");
                        break;
                    }

                    // 获取 - 下注倍率
                    BigDecimal scienceRatio = getBetScienceRatio(param);
                    // 获取 - 下注金额
                    BigDecimal betAmount = getBetAmount(new BigDecimal(5), scienceRatio);
                    log.info("投注金额:{}", betAmount);
                    // 设置 - 上期金额
                    param.setPriorPeriodAmount(betAmount);
                    // 设置 - 下注总额
                    param.setBetTotalAmount(param.getBetTotalAmount().add(betAmount));
                    // 设置 - 最新倍率
                    param.setScienceRatio(scienceRatio);
                    log.info("param -> {}", JSON.toJSONString(param));
                    log.info("--------------------------------");
                } else {
                    // 未中将
                    log.info("未中奖");
                    // 设置 - 前期亏损
                    param.setEarlierStageLoss(param.getEarlierStageLoss().add(param.getPriorPeriodAmount()));
                    // 设置 - 盈亏总额 = 前期盈利 - 前期亏损
                    param.setProfitAndLossTotalAmount(param.getEarlierProfit().subtract(param.getEarlierStageLoss()));

                    // 获取 - 下注倍率
                    BigDecimal scienceRatio = getBetScienceRatio(param);
                    // 获取 - 下注金额
                    BigDecimal betAmount = getBetAmount(new BigDecimal(5), scienceRatio);
                    log.info("投注金额:{}", betAmount);
                    // 设置 - 上期金额
                    param.setPriorPeriodAmount(betAmount);
                    // 设置 - 下注总额
                    param.setBetTotalAmount(param.getBetTotalAmount().add(betAmount));
                    // 设置 - 最新倍率
                    param.setScienceRatio(scienceRatio);
                    log.info("param -> {}", JSON.toJSONString(param));
                    log.info("--------------------------------");
                }
            }
            return param;
        }
    }

    /***
     * 模拟开奖结果
     * 
     * @return java.lang.Boolean
     * @author yucw
     * @date 2022-10-27 13:43
     */
    public static Boolean testResult() {
        int kj = new Random().nextInt(10);
        return kj % 2 == 1;
    }
}
