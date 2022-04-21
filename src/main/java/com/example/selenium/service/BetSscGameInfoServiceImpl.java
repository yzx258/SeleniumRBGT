package com.example.selenium.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.selenium.entity.ssc.BetSscGameInfo;
import com.example.selenium.mapper.BetSscGameInfoMapper;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-04-20 15:35
 */
@Service
public class BetSscGameInfoServiceImpl extends ServiceImpl<BetSscGameInfoMapper, BetSscGameInfo>
    implements BetSscGameInfoService {

    @Override
    public void updateBetSscGameInfo(String period, Integer bsResult) {
        boolean updateResult = update(Wrappers.lambdaUpdate(BetSscGameInfo.class)
            .set(BetSscGameInfo::getResult, bsResult).eq(BetSscGameInfo::getPeriod, period));
        if (!updateResult) {
            throw new RuntimeException("更新数据失败");
        }
    }
}
