package com.example.selenium.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.selenium.model.param.BetScienceRatioParam;
import com.example.selenium.util.ScienceRatioUtil;

import lombok.RequiredArgsConstructor;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-10-27 12:44
 */
@RequestMapping("/science")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScienceRatioController {

    @PostMapping("/getBetScienceRatio")
    public BigDecimal getBetScienceRatio(@RequestBody BetScienceRatioParam param) {
        return ScienceRatioUtil.getBetScienceRatio(param);
    }

    @PostMapping("/testBet")
    public BetScienceRatioParam testBet(@RequestBody BetScienceRatioParam param) {
        return ScienceRatioUtil.testBet(param);
    }
}
