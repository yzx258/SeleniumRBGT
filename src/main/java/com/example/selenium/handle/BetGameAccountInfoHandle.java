package com.example.selenium.handle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.selenium.entity.BetGameAccountInfo;
import com.example.selenium.mapper.BetGameAccountInfoMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-02-28 11:23
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BetGameAccountInfoHandle {

    private final BetGameAccountInfoMapper betGameAccountInfoMapper;

    /***
     *
     * GET - TOKEN
     *
     * @return java.lang.String
     * @author yucw
     * @date 2022-02-28 11:31
     */
    public String getToken() {
        BetGameAccountInfo info = betGameAccountInfoMapper
            .selectOne(Wrappers.lambdaQuery(BetGameAccountInfo.class).eq(BetGameAccountInfo::getAccKey, "token"));
        if (null != info && StringUtils.isNotEmpty(info.getAccVal())) {
            return info.getAccVal();
        }
        return null;
    }

    /***
     *
     * GET - URL
     * 
     * @return java.lang.String
     * @author yucw
     * @date 2022-02-28 12:46
     */
    public String getUrl(String key) {
        BetGameAccountInfo info = betGameAccountInfoMapper
            .selectOne(Wrappers.lambdaQuery(BetGameAccountInfo.class).eq(BetGameAccountInfo::getAccKey, key));
        if (null != info && StringUtils.isNotEmpty(info.getAccVal())) {
            return info.getAccVal();
        }
        return null;
    }

}
