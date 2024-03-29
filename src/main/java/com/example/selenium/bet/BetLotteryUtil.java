package com.example.selenium.bet;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.example.selenium.dto.InstructionDTO;
import com.example.selenium.util.DingUtil;
import com.example.selenium.util.SleepUtil;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author yucw
 * @date 2020/8/14 14:14
 * 描述：188下注平台
 */
@Slf4j
public class BetLotteryUtil {

    private static String URL_ERROR = "http://47.106.143.218:8081/instruction/update/error/";
    private static String URL_SUCCESS = "http://47.106.143.218:8081/instruction/update/success/";
    private static Cache<String, String> fifoCache = CacheUtil.newFIFOCache(1000);
    private static int S_W = 0;
    // 单个倍率,万位
    private static final String WW = "0,9";
    private static final int WW_CS = 0;

    // 单个倍率,千位位
    private static final String QW = "0,9";
    private static final int QW_CS = 0;

    // 单个倍率,百位
    private static final String BW = "0,9";
    private static final int BW_CS = 0;

    // 单个倍率,十位
    private static final String SW = "0,9";
    private static final int SW_CS = 0;

    // 单个倍率,个位
    private static final String GW = "0,9";
    private static final int GW_CS = 0;


    public static void main(String[] args) {
        BetLotteryUtil betLotteryUtil = new BetLotteryUtil();
        betLotteryUtil.sz();
    }

    /**
     * @throws InterruptedException
     */
    public void sz() {
        // 比赛进行中
        if (1 == S_W) {
            System.out.println("比赛进行中,跳过");
            return;
        }
        S_W = 1;
        // chromeDriver服务地址，需要手动下载
        // 测试环境：[D:\00002YX\chromedriver.exe]地址需要自己给
        // String chromeDriverUrl = "C:\\software\\chrome\\chromedriver.exe";
        // 正式环境：System.getProperty("user.dir")+"\\src\\main\\resources\\chromedriver.exe";
        String chromeDriverUrl = System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe";
        System.out.println(chromeDriverUrl);
        System.setProperty("webdriver.chrome.driver", chromeDriverUrl);
        WebDriver driver = new ChromeDriver();
        BetCopyUtil betCopyUtil = new BetCopyUtil();
        try {
            // 登录操作
            betCopyUtil.login(driver);
            SleepUtil.sleepUtil(2000);
            try {
                // //*[@id="indexann"]/h2/div
                driver.findElement(By.xpath("//*[@id=\"indexann\"]/h2/div")).click();
            } catch (Exception e) {
                System.out.println("点击不到数据");
            }
            SleepUtil.sleepUtil(2000);
            try {
                // //*[@id="indexann"]/h2/div
                driver.findElement(By.xpath("//*[@id=\"indexinfo_msg\"]/div/div[3]/button[1]")).click();
            } catch (Exception e) {
                System.out.println("点击不到数据");
            }
            SleepUtil.sleepUtil(2000);
            // 点击体育赛事
            driver.findElement(By.xpath("//*[@id=\"index\"]/div[2]/div/div/ul/li/a/div")).click();
            SleepUtil.sleepUtil(4000);
            try {
                driver.findElement(By.xpath("//*[@id=\"header-wrap\"]/div[1]/div[2]/ul/li[9]/a"));
                SleepUtil.sleepUtil(1000);
            } catch (Exception e) {
                System.out.println("获取不到，不报错");
            }
            SleepUtil.sleepUtil(1000);
            // 点击彩票界面
            driver.findElement(By.xpath("//*[@id=\"header-wrap\"]/div[1]/div[2]/ul/li[9]")).click();
            SleepUtil.sleepUtil(2000);
            //滚动到最底端
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,document.body.scrollHeight)");
            SleepUtil.sleepUtil(2000);
            // 点击世博彩票
            driver.findElement(By.xpath("//*[@id=\"lottery-wrap\"]/div[2]/div/div[20]")).click();
            SleepUtil.sleepUtil(2000);
            // 点击世博彩票
            driver.findElement(By.xpath("//*[@id=\"lottery-wrap\"]/div[2]/div/div[20]")).click();
            SleepUtil.sleepUtil(5000);
            // 获取新窗口句柄，才能操作新弹出的窗口
            System.out.println("1111111111111111111111111");
            Set<String> allWindowsId = driver.getWindowHandles();
            List<String> list = new ArrayList<>(allWindowsId);
            String JB = "";
            for (int i = 0; i < list.size(); i++) {
                if (i == 1) {
                    JB = list.get(i);
                }
            }
            SleepUtil.sleepUtil(5000);
            System.out.println("222222222222222222222222222");
            SleepUtil.sleepUtil(2000);
            // 全屏操作
            driver.switchTo().window(JB).manage().window().maximize();
            SleepUtil.sleepUtil(1000);
            // 倍率数据
            List<String> rl = new ArrayList<>();
            rl.add("1");
            rl.add("2");
            rl.add("4");
            rl.add("8");
            rl.add("17");
            rl.add("34");
            rl.add("69");
            rl.add("140");
            rl.add("282");
            rl.add("564");
            rl.add("1134");
            rl.add("2281");
            rl.add("4010");
            rl.add("8050");
            // 循环调用即可
            do {
                // 判断当前时间是否在这个事件段
                SleepUtil.sleepUtil(500);
                if (checkTime() && null == fifoCache.get("sendMassage")) {
                    String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
                    DingUtil dingUtil = new DingUtil();
                    dingUtil.sendMassage("我是航行者,前来汇报 : " + fifoCache.get("ZHJE"));
                    fifoCache.put("sendMassage", "OK", DateUnit.SECOND.getMillis() * 70);
                }
                SleepUtil.sleepUtil(2000);
                String str = driver.switchTo().window(JB).findElement(By.className("preparing")).getText();
                String ww = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"num0\"]")).getText();
                // 判断是否进行中
                if (str.equals("准备开奖") || StrUtil.isEmpty(ww)) {
                    // System.out.println("正在开奖，跳过");
                    continue;
                }
                String qs = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"kjdates_mc_txt\"]")).getText();
                // System.out.println("期数:" + qs);
                if (null == fifoCache.get("QS")) {
                    fifoCache.put("QS", qs);
                }
                if (qs.equals(fifoCache.get("QS"))) {
                    // System.out.println("期数相同，跳过买卖 -> " + qs);
                    continue;
                }
                fifoCache.put("QS", qs);
                log.info("*******************************");
                if(null == fifoCache.get("ZHJE")){
                    log.info("************* 开始下注期数[0] : " + qs + " *************");
                }else{
                    log.info("************* 开始下注期数["+Double.valueOf(Integer.parseInt(fifoCache.get("ZHJE"))/100)+"] : " + qs + " *************");
                }
                log.info("*******************************");
                SleepUtil.sleepUtil(200);
                driver.switchTo().window(JB).navigate().refresh();
                SleepUtil.sleepUtil(3500);
                // 下注万位
                String wws = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/ul/li[1]")).getText();
                log.info("万位数据 -> {}", wws);
                sendBet(wws, driver, JB, qs, rl, "sendBetWw", "sendBetAmountWw", "sendBetNumberWw", 1);
                //sendBetDob(wws, driver, JB, "WW_BD", "WW_CS", "WW", 1);
                driver.switchTo().window(JB).navigate().refresh();
                SleepUtil.sleepUtil(3500);
                // 下注千位
                String qws = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/ul/li[2]")).getText();
                log.info("千位数据 -> {}", qws);
                sendBet(qws, driver, JB, qs, rl, "sendBetQw", "sendBetAmountQw", "sendBetNumberQw", 2);
                //sendBetDob(qws, driver, JB, "QW_BD", "QW_CS", "QW", 2);
                driver.switchTo().window(JB).navigate().refresh();
                SleepUtil.sleepUtil(3500);
                // 下注百位
                String bws = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/ul/li[3]")).getText();
                log.info("百位数据 -> {}", bws);
                sendBet(bws, driver, JB, qs, rl, "sendBetBw", "sendBetAmountBw", "sendBetNumberBw", 3);
                //sendBetDob(bws, driver, JB, "BW_BD", "BW_CS", "BW", 3);
                driver.switchTo().window(JB).navigate().refresh();
                SleepUtil.sleepUtil(3500);
                // 下注十位
                String sws = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/ul/li[4]")).getText();
                log.info("十位数据 -> {}", sws);
                sendBet(sws, driver, JB, qs, rl, "sendBetSw", "sendBetAmountSw", "sendBetNumberSw", 4);
                //sendBetDob(sws, driver, JB, "SW_BD", "SW_CS", "SW", 4);
                driver.switchTo().window(JB).navigate().refresh();
                SleepUtil.sleepUtil(3500);
                // 下注个位
                String gws = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/ul/li[5]")).getText();
                log.info("个位数据 -> {}", gws);
                sendBet(gws, driver, JB, qs, rl, "sendBetGw", "sendBetAmountGw", "sendBetNumberGw", 5);
                //sendBetDob(gws, driver, JB, "GW_BD", "GW_CS", "GW", 5);
                driver.switchTo().window(JB).navigate().refresh();
            } while (true);
        } catch (Exception e) {
            System.out.println("====================== 报错了 ======================");
            System.out.println(e);
            driver.quit();
            S_W = 0;
            fifoCache.clear();
        }
    }

    /**
     * 判断时间，并发钉钉消息
     *
     * @return
     */
    public Boolean checkTime() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        if ("23:53".equals(df.format(new Date())) || "23:56".equals(df.format(new Date())) || "23:59".equals(df.format(new Date()))
                || "05:53".equals(df.format(new Date())) || "05:56".equals(df.format(new Date())) || "05:59".equals(df.format(new Date()))
                || "11:53".equals(df.format(new Date())) || "11:56".equals(df.format(new Date())) || "11:59".equals(df.format(new Date()))
                || "18:53".equals(df.format(new Date())) || "18:56".equals(df.format(new Date())) || "18:59".equals(df.format(new Date()))
        ) {
            log.info("=======================");
            log.info("发送钉钉通知当前盈利状况");
            log.info("=======================");
            return true;
        }
        return false;
    }

    /**
     * 下注指令
     *
     * @param ws               位数：万位，千位，百位，十位，个位
     * @param driver           浏览器
     * @param JB               句柄ID
     * @param qs               期数
     * @param rl               数组
     * @param sendBetKey       下注单双key
     * @param sendBetAmountKey 下注金额
     * @param sendBetNumberKey 下注次数
     * @throws InterruptedException
     */
    public void sendBet(String ws, WebDriver driver, String JB, String qs, List<String> rl, String sendBetKey, String sendBetAmountKey, String sendBetNumberKey, int div) {
        try {
            Boolean flag = true;
            if (null == fifoCache.get(sendBetKey)) {
                fifoCache.put(sendBetKey, "0,1,2,3,4,5,6,7,8,9");
            }
            if (fifoCache.get(sendBetNumberKey) == null) {
                fifoCache.put(sendBetNumberKey, "0");
                fifoCache.put(sendBetAmountKey, "1");
            }
            // 判断购买的金额是否停滞，等待几场在下注
            int code = Integer.parseInt(fifoCache.get(sendBetNumberKey));
            if ((code >= 11 && code <= 14) && null != fifoCache.get("TG_SEND")) {
                log.info("跳过下注");
                int tg_send = Integer.parseInt(fifoCache.get("TG_SEND"));
                if (code == 11 && tg_send == 5) {
                    fifoCache.put("TG_SEND", (tg_send + 1) + "");
                    return;
                }
                if (code == 12 && tg_send == 1) {
                    fifoCache.put("TG_SEND", (tg_send + 1) + "");
                    return;
                }
                if (code == 13 && tg_send == 1) {
                    fifoCache.put("TG_SEND", (tg_send + 1) + "");
                    return;
                }
                if (code == 14 && tg_send == 1) {
                    fifoCache.put("TG_SEND", (tg_send + 1) + "");
                    return;
                }
                // 清除缓存
                log.info("清除过滤下注缓存信息");
                fifoCache.remove("TG_SEND");
            }
            log.info("====================================================================");
            log.info("***********");
            log.info("下注场次[" + sendBetKey + "] -> {}", fifoCache.get(sendBetKey));
            log.info("***********");
            // 判断是否红[fifoCache.get("sendBet")]
            log.info("[" + fifoCache.get(sendBetKey) + "].contains(" + ws + ") -> {}", fifoCache.get(sendBetKey).contains(ws));
            // TODO null != fifoCache.get("TG_SEND")说明有下注需要过滤
            if (fifoCache.get(sendBetKey).contains(ws) && null == fifoCache.get("TG_SEND")) {
                // 上期比赛结果为单
                log.info("比赛单【fifoCache.get(sendBetKey).contains(" + ws + ")】 -> " + "单".equals(fifoCache.get(sendBetKey)));
                log.info("ws -> :" + ws + " -> " + fifoCache.get(sendBetKey));
                flag = true;
                fifoCache.remove("TG_SEND");
                if(null == fifoCache.get("ZHJE")){
                    fifoCache.put("ZHJE","0");
                }
                int amount = Integer.parseInt(fifoCache.get("ZHJE"));
                amount = amount + 97;
                fifoCache.put("ZHJE",amount + "");
            } else {
                // 黑了
                log.info("比赛黑了");
                log.info("ww -> :" + ws + " -> " + fifoCache.get(sendBetKey));
                flag = false;
            }
            if (null != fifoCache.get(sendBetNumberKey)) {
                String fl = "黑";
                if (flag) {
                    fl = "红";
                }
                if (code == 9) {
                    SleepUtil.sleepUtil(2000);
                    DingUtil d = new DingUtil();
                    String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
                    d.sendMassage("[ " + sendBetKey + " ]比赛第10场【" + fl + "】,航行者,前来汇报 : " + text);
                }
                if (code == 10) {
                    SleepUtil.sleepUtil(2000);
                    DingUtil d = new DingUtil();
                    String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
                    d.sendMassage("[ " + sendBetKey + " ]比赛第11场【" + fl + "】,航行者,前来汇报 : " + text);
                    fifoCache.put("TG_SEND", "0");
                }
                if (code == 11) {
                    SleepUtil.sleepUtil(2000);
                    DingUtil d = new DingUtil();
                    String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
                    d.sendMassage("[ " + sendBetKey + " ]比赛第12场【" + fl + "】,航行者,前来汇报 : " + text);
                    fifoCache.put("TG_SEND", "0");
                }
                if (code == 12) {
                    SleepUtil.sleepUtil(2000);
                    DingUtil d = new DingUtil();
                    String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
                    d.sendMassage("[ " + sendBetKey + " ]比赛第13场【" + fl + "】,航行者,前来汇报 : " + text);
                    fifoCache.put("TG_SEND", "0");
                }
                if (code == 13) {
                    SleepUtil.sleepUtil(2000);
                    DingUtil d = new DingUtil();
                    String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
                    d.sendMassage("[ " + sendBetKey + " ]比赛第14场【" + fl + "】,航行者,前来汇报 : " + text);
                    fifoCache.put("TG_SEND", "0");
                }
                if (code == 14) {
                    SleepUtil.sleepUtil(2000);
                    DingUtil d = new DingUtil();
                    String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
                    d.sendMassage("[ " + sendBetKey + " ]比赛第15场【" + fl + "】,航行者,前来汇报 : " + text);
                    fifoCache.put("TG_SEND", "0");
                }
                if (code == 15) {
                    SleepUtil.sleepUtil(2000);
                    DingUtil d = new DingUtil();
                    String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
                    d.sendMassage("[ " + sendBetKey + " ]比赛第16场【" + fl + "】,航行者,前来汇报 : " + text);
                }
                if (code == 16) {
                    SleepUtil.sleepUtil(2000);
                    DingUtil d = new DingUtil();
                    String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
                    d.sendMassage("[ " + sendBetKey + " ]比赛第17场【" + fl + "】,航行者,前来汇报 : " + text);
                    fifoCache.put("TG_SEND", "0");
                }
            }
            log.info("________点击选择分________");
            // 点击选择分
            SleepUtil.sleepUtil(500);
            driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[4]/div[2]/div/div[2]/div[2]/div[3]/select/option[3]")).click();
            // 点击单
            SleepUtil.sleepUtil(500);
            int number = Integer.parseInt(fifoCache.get(sendBetNumberKey));
            // 一直选择大
            driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[3]/dl/dd[2]")).click();
            fifoCache.put(sendBetKey, "5,6,7,8,9");
//            if (random() == 1) {
//                // 万位
//                // 大 /html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[1]/ul/li[3]/dl/dd[2]
//                // 小 /html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[1]/ul/li[3]/dl/dd[3]
//                // 单 /html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[1]/ul/li[3]/dl/dd[4]
//                // 双 /html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[1]/ul/li[3]/dl/dd[5]
//                if (number <= 8) {
//                    // 大
//                    driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[3]/dl/dd[2]")).click();
//                    fifoCache.put(sendBetKey, "5,6,7,8,9");
//                } else {
//                    // 单 /html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[1]/ul/li[3]/dl/dd[4]
//                    driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[3]/dl/dd[4]")).click();
//                    fifoCache.put(sendBetKey, "1,3,5,7,9");
//                }
//            } else {
//                if (number <= 8) {
//                    // 小
//                    driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[3]/dl/dd[3]")).click();
//                    fifoCache.put(sendBetKey, "0,1,2,3,4");
//                } else {
//                    // 双
//                    driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[3]/dl/dd[5]")).click();
//                    fifoCache.put(sendBetKey, "2,4,6,8,0");
//                }
//            }
            log.info("==================");
            log.info("获取缓存值 -> {}", fifoCache.get(sendBetKey));
            log.info("==================");
            if (flag) {
                // 填写金额
                fifoCache.put(sendBetAmountKey, "1");
                fifoCache.put(sendBetNumberKey, "0");
            } else {
                if (fifoCache.get(sendBetNumberKey) == null) {
                    fifoCache.put(sendBetNumberKey, "0");
                    fifoCache.put(sendBetAmountKey, "1");
                } else {
                    int sendBetNumber = Integer.parseInt(fifoCache.get(sendBetNumberKey)) + 1;
                    fifoCache.put(sendBetNumberKey, sendBetNumber + "");
                    String s = rl.get(sendBetNumber);
                    fifoCache.put(sendBetAmountKey, s);
                    // 获取输入款
                    int addbs = Integer.parseInt(s);
                    log.info("&&&&&&&&&&我是开始addbs参数&&&&&&&&&& -> {}", addbs);
                    // 根据比例扣减循环数
                    if (addbs == 17) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        SleepUtil.sleepUtil(500);
                        element.sendKeys("1");
                        addbs = addbs - 11;
                    } else if (addbs == 34) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        SleepUtil.sleepUtil(500);
                        element.sendKeys("3");
                        addbs = addbs - 31;
                    } else if (addbs == 69) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        SleepUtil.sleepUtil(500);
                        element.sendKeys("6");
                        addbs = addbs - 61;
                    } else if (addbs == 140) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        SleepUtil.sleepUtil(500);
                        element.sendKeys("13");
                        addbs = addbs - 131;
                    } else if (addbs == 282) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        SleepUtil.sleepUtil(500);
                        element.sendKeys("28");
                        addbs = addbs - 281;
                    } else if (addbs == 564) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        SleepUtil.sleepUtil(500);
                        element.sendKeys("56");
                        addbs = addbs - 561;
                    } else if (addbs == 1134) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        SleepUtil.sleepUtil(500);
                        element.sendKeys("113");
                        addbs = addbs - 1131;
                    } else if (addbs == 2281) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        SleepUtil.sleepUtil(500);
                        element.sendKeys("227");
                        addbs = addbs - 2271;
                    } else if (addbs == 2003) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        SleepUtil.sleepUtil(500);
                        element.sendKeys("200");
                        addbs = addbs - 2001;
                    } else if (addbs == 4010) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        SleepUtil.sleepUtil(500);
                        element.sendKeys("400");
                        addbs = addbs - 4001;
                    } else if (addbs == 6050) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        SleepUtil.sleepUtil(500);
                        element.sendKeys("604");
                        addbs = addbs - 6041;
                    } else if (addbs == 8050) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        SleepUtil.sleepUtil(500);
                        element.sendKeys("804");
                        addbs = addbs - 6041;
                    } else {
                        addbs = addbs - 1;
                    }
                    log.info("&&&&&&&&&&我是最终addbs参数&&&&&&&&&& -> {}", addbs);
                    for (int i = 0; i < addbs; i++) {
                        driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"addbs\"]")).click();
                    }
                }
            }
//            // 确认下注
//            Thread.sleep(100);
//            driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"sure\"]/input")).click();
//            Thread.sleep(100);
//            driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"msgDiv\"]/div[3]/div[1]")).click();
//            Thread.sleep(200);
//            log.info("====================================================================");
        } catch (Exception e) {
            log.info("====================== 下注报错了，跳过此次购买[购买单位：" + sendBetKey + "] ======================");
            log.info("********************************");
            log.info("下注报错了，错误信息" + e.getMessage() + "");
            log.info("********************************");
            DingUtil d = new DingUtil();
            String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
            d.sendMassage("下注报错了，可能应为国庆活动问题，请尽快解决，航行者,前来汇报 : " + text);
        }
    }

    /**
     * 下注指令
     *
     * @param ws               位数：万位，千位，百位，十位，个位
     * @param driver           浏览器
     * @param JB               句柄ID
     * @param sendBetKey       下注单双key
     * @param sendBetNumberKey 下注次数
     * @param sendBetWs        下注单双key
     * @param div              位数
     */
    public void sendBetDob(String ws, WebDriver driver, String JB, String sendBetKey, String sendBetNumberKey, String sendBetWs, int div) {
        try {
            // 点击选择分
            driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[4]/div[2]/div/div[2]/div[2]/div[3]/select/option[3]")).click();
            Thread.sleep(200);
            // 判断比赛是否走8位
            if (null == fifoCache.get(sendBetNumberKey)) {
                fifoCache.put(sendBetNumberKey, "0");
            }
            if (null == fifoCache.get(sendBetKey)) {
                fifoCache.put(sendBetKey, "红");
            }
            int numberKey = Integer.parseInt(fifoCache.get(sendBetNumberKey));
            log.info("=================== [" + div + "] 开始 ===================");
            log.info("我是购买次数[sendBetNumberKey] -> {}", numberKey);
            log.info(fifoCache.get(sendBetKey) + ".contains(" + ws + ") -> {}", fifoCache.get(sendBetKey).contains(ws));
            String str = "";
            if (fifoCache.get(sendBetKey).contains(ws)) {
                log.info("该比赛黑单，走加倍逻辑0000000000");
                str = sendEightDigitsErr(driver, JB, sendBetKey, sendBetNumberKey, div);
            } else {
                log.info("该比赛红单，走初倍逻辑1111111111");
                str = sendEightDigitsOk(driver, JB, sendBetKey, sendBetNumberKey, div);
            }
            // 点击下注使用
            sendBetOk(sendBetNumberKey, driver, JB, sendBetKey, div, str);
            log.info("=================== [" + div + "] 结束 ===================");
            System.out.println("");
        } catch (Exception e) {
            log.info("====================== 下注报错了，跳过此次购买[购买单位：" + sendBetKey + "] ======================");
        }

    }

    /**
     * 下注完成
     *
     * @param numberKey
     * @param driver
     * @param JB
     * @throws InterruptedException
     */
    public void sendBetOk(String sendBetNumberKey, WebDriver driver, String JB, String sendBetKey, int div, String str) throws InterruptedException {
        // 倍率
        List<String> rl = new ArrayList<>();
        rl.add("1");
        rl.add("13");
        rl.add("165");
        rl.add("1000");
        // 将倍率取出
        int numberKey = Integer.parseInt(fifoCache.get(sendBetNumberKey));
        String amount = rl.get(numberKey);
        log.info("---------------------------------");
        log.info("我是取出来的倍率次数和倍数[numberKey],[amount] -> {},{}", numberKey, amount);
        log.info("---------------------------------");
        int addbs = Integer.parseInt(amount);
        log.info("&&&&&&&&&&我是开始addbs参数&&&&&&&&&& -> {}", addbs);
        // 根据比例扣减循环数
        if (addbs > 100 && addbs < 200) {
            WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
            Thread.sleep(200);
            element.sendKeys("12");
            addbs = addbs - 121;
        } else if (addbs > 900 && addbs < 1200) {
            WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
            Thread.sleep(200);
            element.sendKeys("99");
            addbs = addbs - 991;
        } else {
            addbs = addbs - 1;
        }
        log.info("&&&&&&&&&&我是最终addbs参数&&&&&&&&&& -> {}", addbs);
        for (int i = 0; i < addbs; i++) {
            driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"addbs\"]")).click();
        }
        // 确认下注
        Thread.sleep(100);
        driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"sure\"]/input")).click();
        Thread.sleep(100);
        driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"msgDiv\"]/div[3]/div[1]")).click();
        driver.switchTo().window(JB).navigate().refresh();
        if (numberKey != 3) {
            Thread.sleep(2000);
            String[] split = fifoCache.get(sendBetKey).split(",");
            log.info("我是新增购买数据 - > {},{}", JSON.toJSONString(split), split[0]);
            // 点击
            driver.switchTo().window(JB).findElement(By.xpath(str.split(",")[0])).click();
            fifoCache.put(sendBetKey, split[1]);
            // 点击分
            driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[4]/div[2]/div/div[2]/div[2]/div[3]/select/option[3]")).click();
            Thread.sleep(200);
            // 根据比例扣减循环数
            String amount2 = rl.get(numberKey);
            int addbs2 = Integer.parseInt(amount2);
            if (addbs2 > 100 && addbs2 < 200) {
                WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                Thread.sleep(200);
                element.sendKeys("12");
                addbs2 = addbs2 - 121;
            } else if (addbs2 > 1000 && addbs2 < 2000) {
                WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                Thread.sleep(200);
                element.sendKeys("164");
                addbs2 = addbs2 - 1641;
            } else {
                addbs2 = addbs2 - 1;
            }
            for (int i = 0; i < addbs2; i++) {
                driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"addbs\"]")).click();
            }
            // 确认下注
            Thread.sleep(100);
            driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"sure\"]/input")).click();
            Thread.sleep(100);
            driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"msgDiv\"]/div[3]/div[1]")).click();
            Thread.sleep(100);
        }
    }

    /**
     * 校验单双是否过关
     *
     * @param ws
     * @param sendBetKey
     * @return
     */
    public Boolean checkOddAndEven(String ws, String sendBetWs) {
        if (Integer.parseInt(ws) % 2 == 1 && "单".equals(fifoCache.get(sendBetWs))) {
            // 上期比赛结果为单
            log.info("比赛单【单.equals(fifoCache.get(" + sendBetWs + "))】 -> " + "单".equals(fifoCache.get(sendBetWs)));
            log.info("ws -> :" + ws + " -> " + fifoCache.get(sendBetWs));
            return true;
        } else if (Integer.parseInt(ws) % 2 == 0 && "双".equals(fifoCache.get(sendBetWs))) {
            // 上期比赛结果为双
            log.info("比赛双【双.equals(fifoCache.get(\"+sendBetWs+\"))】 -> " + "双".equals(fifoCache.get(sendBetWs)));
            log.info("ws -> :" + ws + " -> " + fifoCache.get(sendBetWs));
            return true;
        } else {
            // 黑了
            log.info("比赛黑了");
            log.info("ww -> :" + ws + " -> " + fifoCache.get(sendBetWs));
            return false;
        }
    }

    /**
     * 八位数，成功方法
     *
     * @param driver
     * @param JB
     * @param numberKey
     * @param sendBetNumberKey
     * @param div
     */
    public String sendEightDigitsOk(WebDriver driver, String JB, String sendBetKey, String sendBetNumberKey, int div) {
        StringBuffer buf = new StringBuffer();
        String jzws = random1();
        String[] split = jzws.split(",");
        for (int i = 0; i < 10; i++) {
            if (Integer.parseInt(split[0]) == i || Integer.parseInt(split[1]) == i) {
                String str = "/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[2]/table/tbody/tr/td[" + (i + 1) + "]";
                buf.append(str).append(",");
                continue;
            }
            // 点击
            driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[2]/table/tbody/tr/td[" + (i + 1) + "]")).click();
        }
        int numberKey = 0;
        fifoCache.put(sendBetNumberKey, numberKey + "");
        fifoCache.put(sendBetKey, jzws);
        return buf.toString();
    }

    /**
     * 八位数，失败方法
     *
     * @param driver
     * @param JB
     * @param numberKey
     * @param sendBetNumberKey
     * @param div
     */
    public String sendEightDigitsErr(WebDriver driver, String JB, String sendBetKey, String sendBetNumberKey, int div) {
        StringBuffer buf = new StringBuffer();
        String jzws = random1();
        String[] split = jzws.split(",");
        for (int i = 0; i < 10; i++) {
            if (Integer.parseInt(split[0]) == i || Integer.parseInt(split[1]) == i) {
                String str = "/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[2]/table/tbody/tr/td[" + (i + 1) + "]";
                buf.append(str).append(",");
                continue;
            }
            // 点击
            driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[2]/table/tbody/tr/td[" + (i + 1) + "]")).click();
        }
        int numberKey = Integer.parseInt(fifoCache.get(sendBetNumberKey));
        if (numberKey == 2) {
            String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
            DingUtil dingUtil = new DingUtil();
            dingUtil.sendMassage("黑3场了，航行者,前来汇报 : " + text);
            numberKey = 0;
        } else {
            numberKey = numberKey + 1;
        }
        fifoCache.put(sendBetNumberKey, numberKey + "");
        fifoCache.put(sendBetKey, jzws);
        return buf.toString();
    }

    /**
     * 下注走单双
     *
     * @param driver
     * @param numberKey
     * @param div
     * @param sendBetKey
     * @param sendBetNumberKey
     * @param JB
     */
    public void sendOddAndEven(WebDriver driver, int div, String sendBetKey, String sendBetNumberKey, String JB) {
        log.info("走单双判断逻辑");
        if (random() == 1) {
            // 大 /html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[1]/ul/li[3]/dl/dd[2]
            driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[3]/dl/dd[2]")).click();
            fifoCache.put(sendBetKey, "5,6,7,8,9");
            log.info("fifoCache.get(" + sendBetKey + ")购买： -> {}", fifoCache.get(sendBetKey));
        } else {
            // 小 /html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[1]/ul/li[3]/dl/dd[3]
            driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[3]/dl/dd[3]")).click();
            fifoCache.put(sendBetKey, "0,1,2,3,4");
            log.info("fifoCache.get(" + sendBetKey + ")购买： -> {}", fifoCache.get(sendBetKey));
        }
        int numberKey = Integer.parseInt(fifoCache.get(sendBetNumberKey));
        if (numberKey == 6) {
            DingUtil d = new DingUtil();
            d.sendMassage("[ " + sendBetKey + " ]比赛黑6场，重新开始下");
            String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
            d.sendMassage("我是航行者,前来汇报 : " + text);
            numberKey = 0;
        } else {
            numberKey = numberKey + 1;
        }
        fifoCache.put(sendBetNumberKey, numberKey + "");
        log.info("我是重新设置的购买次数[sendBetNumberKey] -> {}", fifoCache.get(sendBetNumberKey));
    }

    /**
     * 获取俩个不确定数据
     *
     * @return
     */
    public static String random1() {
        int max = 10, min = 0;
        int ran1 = (int) (Math.random() * (max - min) + min);
        int ran2 = 0;
        Boolean flag = false;
        do {
            ran2 = (int) (Math.random() * (max - min) + min);
            if (ran1 != ran2) {
                flag = true;
            }
        } while (!flag);
        return ran1 + "," + ran2;
    }

    /**
     * 单双随机数
     *
     * @return
     */
    public static int random() {
        int max = 100, min = 1;
        int ran2 = (int) (Math.random() * (max - min) + min);
        return ran2 % 2;
    }
}
