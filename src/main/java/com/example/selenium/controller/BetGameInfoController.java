package com.example.selenium.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.selenium.handle.BetGameInfoHandle;

import lombok.RequiredArgsConstructor;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-26 23:09:38
 * @description: 描述
 */
@RequestMapping("/gameInfo")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BetGameInfoController {

    private final BetGameInfoHandle betGameInfoHandle;

    @GetMapping("/comparisonResults")
    public void getTest() throws IOException {
        betGameInfoHandle.comparisonResults();
    }
}
