package com.example.selenium.controller.ssc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.selenium.handle.BetGameAccountInfoHandle;

import lombok.RequiredArgsConstructor;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-26 23:09:38
 * @description: 描述
 */
@RequestMapping("/ssc")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BetSscGameInfoController {

    private final BetGameAccountInfoHandle betGameAccountInfoHandle;

    @GetMapping("/getSscToken")
    public String getSscToken() {
        return betGameAccountInfoHandle.getSscToken();
    }

    @GetMapping("/updateToken")
    public String updateToken(@RequestParam("token") String token) {
        if (null == token) {
            throw new RuntimeException("不能为空");
        }
        // 查询 - 地址是否存在
        Integer ssc_token = betGameAccountInfoHandle.updateKey("SSC_TOKEN", token);
        if (ssc_token == 0) {
            throw new RuntimeException("获取不到地址，请检查！");
        }
        return "UPDATE TOKEN SUCCESS";
    }

    @GetMapping("/getGms")
    public String getGms() {
        return betGameAccountInfoHandle.getKey("SSC_GMS");
    }

    @GetMapping("/getSscUrl")
    public String getSscUrl() {
        return betGameAccountInfoHandle.getSscUrl();
    }

    @GetMapping("/updateGms")
    public String updateGms(@RequestParam("gms") String gms) {
        if (null == gms) {
            throw new RuntimeException("不能为空");
        }
        // 查询 - 地址是否存在
        Integer ssc_gms = betGameAccountInfoHandle.updateKey("SSC_GMS", gms);
        if (ssc_gms == 0) {
            throw new RuntimeException("获取不到地址，请检查！");
        }
        return "UPDATE GMS SUCCESS";
    }
}
