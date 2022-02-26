package com.example.selenium.controller;

import com.example.selenium.handle.BetGameInfoHandle;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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

    @GetMapping("/getGameInfoResult")
    public void testGameInfo() throws IOException {
        betGameInfoHandle.getGameInfoResult();
    }

}
