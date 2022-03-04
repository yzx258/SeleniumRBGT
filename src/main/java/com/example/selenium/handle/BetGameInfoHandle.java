package com.example.selenium.handle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.selenium.bo.BetGameInfoBO;
import com.example.selenium.bo.YaBoAccountInfoBO;
import com.example.selenium.bo.result.ResultBodyBO;
import com.example.selenium.entity.BetGameInfo;
import com.example.selenium.mapper.BetGameInfoMapper;
import com.example.selenium.util.CacheMapUtil;
import com.example.selenium.util.GetYaBoResultUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-26 23:11:50
 * @description: 描述
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BetGameInfoHandle {

    private final BetGameInfoMapper betGameInfoMapper;
    private final GetYaBoResultUtil getYaBoResultUtil;
    private final CacheMapUtil cacheMapUtil;

    /***
     * SAVE BETGAMEINFO
     * 
     * @param info
     * @return void
     * @author yucw
     * @date 2022-02-28 14:04
     */
    public void save(BetGameInfo info) {
        betGameInfoMapper.insert(info);
    }

    public BetGameInfo getBetGameInfoByLockId(String lockId) {
        return betGameInfoMapper
            .selectOne(Wrappers.lambdaQuery(BetGameInfo.class).eq(BetGameInfo::getLockId, lockId).last("limit 1"));
    }

    /***
     *
     * comparisonResults
     * 
     * @return void
     * @author yucw
     * @date 2022-02-28 11:40
     */
    public void comparisonResults() {
        Object bet_tag = cacheMapUtil.getMap("THREAD_EXECUTION_COMPARISON_RESULTS");
        if (null != bet_tag) {
            System.out.println("THREAD_EXECUTION_COMPARISON_RESULTS - 正在执行，不允许新线程操作");
            return;
        }

        // 线程开始执行
        cacheMapUtil.putMap("THREAD_EXECUTION_COMPARISON_RESULTS", "1");
        log.info("SEND THREAD_EXECUTION_COMPARISON_RESULTS");
        try {
            // 查询 - 待结算信息
            List<BetGameInfo> betGameInfos = betGameInfoMapper
                .selectList(Wrappers.lambdaQuery(BetGameInfo.class).eq(BetGameInfo::getIsSettlement, 0));
            if (null == betGameInfos || (null != betGameInfos && betGameInfos.size() == 0)) {
                log.info("NO SETTLED MATCHES");
                cacheMapUtil.delMap("THREAD_EXECUTION_COMPARISON_RESULTS");
                return;
            }

            // 1.获取待处理的结算数据
            ResultBodyBO resultBodyInfo = getYaBoResultUtil.completeSettlement();
            if (null == resultBodyInfo) {
                log.info("NO SETTLED MATCHES");
                cacheMapUtil.delMap("THREAD_EXECUTION_COMPARISON_RESULTS");
                return;
            }

            // 2.GET SCREENINGS
            List<BetGameInfoBO> betGameInfoList = resultBodyInfo.conversionInformation();
            if (null != betGameInfoList && betGameInfoList.size() > 0) {
                // 3.循环查找匹配数据
                BetGameInfoBO settledInfo;
                for (BetGameInfo item : betGameInfos) {
                    settledInfo = getBetGameInfo(betGameInfoList, item.getHomeTeamName(), item.getAwayTeamName(),
                        item.getWhichSection());
                    // 待结算比赛，是否已经存在结算
                    if (null != settledInfo) {
                        // 更新 - 待结算 -> 已结算数据
                        item.setIsSettlement(1);
                        item.setCompetitionType(0);
                        item.setCompetitionResult(settledInfo.getCompetitionResult());
                        item.setCompetitionMagnification(settledInfo.getCompetitionMagnification());
                        item.setCompetitionDividendAmount(settledInfo.getCompetitionDividendAmount());
                        item.setHomeTeamScore(settledInfo.getHomeTeamScore());
                        item.setAwayTeamScore(settledInfo.getAwayTeamScore());
                        betGameInfoMapper.updateById(item);
                    }
                }
            }
            cacheMapUtil.delMap("THREAD_EXECUTION_COMPARISON_RESULTS");
        } catch (Exception e) {
            log.info("处理同步结算信息失败 - message:{}", e.getMessage());
            cacheMapUtil.delMap("THREAD_EXECUTION_COMPARISON_RESULTS");
        } finally {
            cacheMapUtil.delMap("THREAD_EXECUTION_COMPARISON_RESULTS");
        }
    }

    /***
     * 获取 - 符合的数据
     * 
     * @param betGameInfoList
     * @param homeTeamName
     * @param awayTeamName
     * @param whichSection
     * @return com.example.selenium.bo.BetGameInfoBO
     * @author yucw
     * @date 2022-02-28 15:24
     */
    private static BetGameInfoBO getBetGameInfo(List<BetGameInfoBO> betGameInfoList, String homeTeamName,
        String awayTeamName, String whichSection) {
        for (BetGameInfoBO item : betGameInfoList) {
            // log.info("{} VS {} -> {}", homeTeamName.trim(), item.getHomeTeamName().trim(),
            // homeTeamName.trim().contains(item.getHomeTeamName().trim()));
            // log.info("{} VS {} -> {}", awayTeamName.trim(), item.getAwayTeamName().trim(),
            // awayTeamName.trim().contains(item.getAwayTeamName().trim()));
            // log.info("{} VS {} -> {}", whichSection.trim(), item.getWhichSection().trim(),
            // whichSection.trim().contains(item.getWhichSection().trim()));
            // log.info("--------------------");
            if (homeTeamName.trim().contains(item.getHomeTeamName().trim())
                && awayTeamName.trim().contains(item.getAwayTeamName().trim())
                && whichSection.trim().contains(item.getWhichSection().trim())) {
                BetGameInfoBO info = item;
                log.info("存在待结算数据 - BetGameInfoBO : {}", JSON.toJSONString(info));
                return info;
            }
        }
        return null;
    }

    /***
     * 校验比赛是否已存在
     * 
     * @param yaBoAccountInfo
     * @return java.lang.Boolean
     * @author yucw
     * @date 2022-03-01 15:18
     */
    public Boolean checkExists(YaBoAccountInfoBO yaBoAccountInfo) {
        BetGameInfo betGameInfo = betGameInfoMapper.selectOne(Wrappers.lambdaQuery(BetGameInfo.class)
            .eq(BetGameInfo::getHomeTeamName, yaBoAccountInfo.getHomeTeamName())
            .eq(BetGameInfo::getWhichSection, yaBoAccountInfo.getWhichSection()).eq(BetGameInfo::getIsSettlement, 0));
        if (null != betGameInfo) {
            log.info("其他线程已购买次场次，其他线程不允许在操作：{}", JSON.toJSONString(betGameInfo));
            return true;
        }
        return false;
    }
}
