package com.example.selenium.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.selenium.handle.BetGameAccountInfoHandle;

import lombok.RequiredArgsConstructor;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-02-28 14:20
 */
@RequestMapping("/gameAccountInfo")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BetGameAccountInfoController {

    private final BetGameAccountInfoHandle betGameAccountInfoHandle;

    @GetMapping("/getToken")
    public String getToken() {
        return betGameAccountInfoHandle.getToken();
    }

    @GetMapping("/getUrl")
    public String getUrl() {
        return betGameAccountInfoHandle.getUrl("url");
    }

}
