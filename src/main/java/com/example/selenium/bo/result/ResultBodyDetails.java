package com.example.selenium.bo.result;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-02-25 16:17
 */
@Data
public class ResultBodyDetails {

    // {
    // "wid":"2202250253014926",
    // "wcdt":"2022-02-25T02:53:01.6601019-04:00",
    // "sa":5.0,
    // "wla":4.7,
    // "wt":1,
    // "ot":2,
    // "bcs":2,
    // "bss":1,
    // "bts":0,
    // "btbba":0.0,
    // "noc":0,
    // "cs":0,
    // "pp":0.0,
    // "cse":false,
    // "edt":"2022-02-25T02:05:00-04:00",
    // "oc":1,
    // "wil":[{"wics":2,"wicr":0,"wict":1,"m":3,"eid":45370869,"et":1,"edt":"2022-02-25T02:05:00-04:00","s":2,"cid":31049,"cn":"FIBA世界杯预选赛(第四节)","egt":7,"htid":29150,"htn":"澳大利亚","atid":21607,"atn":"中华台北","bt":5,"p":1,"bts":10,"otid":0,"o":0.94,"ot":0,"h":0.0,"fths":28,"ftas":9,"hths":0,"htas":0,"gt":0,"se":0,"md":0,"btn":"第四节
    // 单/双"}]}

    /***
     * 赛事ID
     */
    private String wid;

    /***
     * 金额
     */
    private BigDecimal sa;

    /***
     * 盈亏
     */
    private BigDecimal wla;

    /***
     * 是否过关
     */
    private Integer oc;

    /***
     * Details
     */
    private List<ResultBodyCompetitionDetails> wil;

}