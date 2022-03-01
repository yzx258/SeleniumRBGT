package com.example.selenium.bo.result;

import java.util.ArrayList;
import java.util.List;

import com.example.selenium.bo.BetGameInfoBO;
import com.example.selenium.util.GetYaBoResultUtil;

import lombok.Data;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-02-25 16:16
 */
@Data
public class ResultBodyBO {

    private String mc;

    List<ResultBodyDetails> wl;

    private Integer StatusCode;

    /***
     *
     * 转换
     * 
     * @return java.util.List<com.example.selenium.bo.BetGameInfoBO>
     * @author yucw
     * @date 2022-02-28 13:16
     */
    public List<BetGameInfoBO> conversionInformation() {
        List<BetGameInfoBO> list = new ArrayList<>();
        BetGameInfoBO info;
        for (ResultBodyDetails item : this.wl) {
            ResultBodyCompetitionDetails resultBodyCompetitionDetails = item.getWil().get(0);
            if (null != resultBodyCompetitionDetails) {
                info = new BetGameInfoBO();
                info.setCompetitionName(resultBodyCompetitionDetails.getCn());
                info.setHomeTeamName(resultBodyCompetitionDetails.getHtn());
                info.setHomeTeamScore(resultBodyCompetitionDetails.getFths());
                info.setAwayTeamName(resultBodyCompetitionDetails.getAtn());
                info.setAwayTeamScore(resultBodyCompetitionDetails.getFtas());
                info.setWhichSection(GetYaBoResultUtil.changeBetWhichSection(resultBodyCompetitionDetails.getBtn()));
                info.setCompetitionMagnification(resultBodyCompetitionDetails.getO());

                info.setBetAmount(item.getSa());
                info.setCompetitionResult(item.getOc());
                info.setCompetitionDividendAmount(item.getWla());
                info.setCompetitionType(0);
                info.setIsSettlement(1);
                list.add(info);
            }
        }
        return list;
    }
}
