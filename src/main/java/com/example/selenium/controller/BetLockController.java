package com.example.selenium.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.example.selenium.mapper.BetLockMapper;

import lombok.RequiredArgsConstructor;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-20 15:34:30
 * @description: 描述
 */
@RequestMapping("/test")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BetLockController {

    private final BetLockMapper betLockMapper;

    @GetMapping("/getLock")
    public String getTest() {
        BetYaBoUtil bet = new BetYaBoUtil();
        bet.bet();
        return JSON.toJSONString(betLockMapper.selectById(1));
    }

}
