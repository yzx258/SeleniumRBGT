package com.example.selenium.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.selenium.entity.ssc.BetSscGameInfo;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-04-20 15:35
 */
public interface BetSscGameInfoService extends IService<BetSscGameInfo> {

    /***
     * 操作 - 更新比赛结果
     * 
     * @param period
     * @param bsResult
     * @author yucw
     * @date 2022-04-21 10:15
     */
    void updateBetSscGameInfo(String period, Integer bsResult);

}