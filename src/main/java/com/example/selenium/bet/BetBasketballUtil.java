package com.example.selenium.bet;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.example.selenium.spec.BetCacheSpec;
import com.example.selenium.spec.BuyRecordJson;
import com.example.selenium.spec.BuyRecordSpec;
import com.example.selenium.util.DingUtil;
import com.example.selenium.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.*;

/**
 * @company： 厦门宜车时代信息技术有限公司
 * @copyright： Copyright (C), 2020
 * @author： yucw
 * @date： 2020/10/13 13:59
 * @version： 1.0
 * @description:
 */
@Slf4j
public class BetBasketballUtil {

    // 获取黑单URL
    private static String GET_URL = "http://47.106.143.218:8081/buy/get/1";
    // 新增下注记录
    private static String ADD_URL = "http://47.106.143.218:8081/buy/add";
    // 删掉已下注黑单
    private static String DEL_URL = "http://47.106.143.218:8081/buy/del/1";
    // 系统缓存
    private static Cache<String, String> fifoCache = CacheUtil.newFIFOCache(1000);
    // 保存三黑的数据
    private static List<BetCacheSpec> map = new ArrayList<>();
    // 下注倍率
    private static String[] bl = new String[]{"10", "20", "40", "80", "160", "320", "640", "1280"};
    // 是否需要
    private static String FLAG_OK = "YES";
    // 点击篮球
    private static String BASKETBALL = "篮球";
    // 第一节
    private static String FIRST = "第1节";
    // 第二节
    private static String SECOND = "第2节";
    // 第三节
    private static String THIRD = "第3节";
    // 第四节
    private static String FOURTH = "第4节";
    // 开关
    private static int S_W = 0;

    /**
     * BBIT下注
     */
    public void bet() {
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
        System.setProperty("webdriver.chrome.driver", "D:\\00002YX\\chromedriver.exe");
        // 自己本地最新的charm版本，需要添加启动参数
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        WebDriver driver = new ChromeDriver();
        DingUtil d = new DingUtil();
        BetCopyUtil betCopyUtil = new BetCopyUtil();
        try {
            // 登录信息
            betCopyUtil.login(driver);
            // 点击新BBIT赛事信息
            betCopyUtil.btnSend(driver);
            driver.navigate().refresh();
            System.out.println("走刷新逻辑....");
            SleepUtil.sleepUtil(8000);

            // 存在iframe,首先需要进到iframe
            driver.switchTo().frame("iframe");
            SleepUtil.sleepUtil(4000);

            // 点击滚球按钮
            driver.findElement(By.xpath("//*[@id=\"nav\"]/a[1]")).click();
            System.out.println("点击滚球按钮...");
            SleepUtil.sleepUtil(4000);

            // 获取新的ifram
            driver.switchTo().frame("bcsportsbookiframe");
            System.out.println("获取新的ifram...");
            SleepUtil.sleepUtil(4000);
            do {
                SleepUtil.sleepUtil(2000);
                // 点击刷新按钮，确保正常连接
                driver.findElement(By.xpath("//*[@id=\"asianView\"]/div/div[3]/div[1]/div[2]/button")).click();
                // 点击篮球 sport-name-asia
                System.out.println("开始新操作....");
                List<WebElement> pl = driver.findElements(By.className("sport-name-asia"));
                Boolean flag = false;
                for (WebElement wp : pl) {
                    // 判断是否有篮球
                    if (StrUtil.isNotBlank(wp.getText()) && BASKETBALL.equals(wp.getText().trim().replaceAll("\r|\n", "P").split("P")[0])) {
                        wp.click();
                        flag = true;
                        break;
                    }
                }
                // 判断是否存在篮球按钮
                if (!flag) {
                    System.out.println("结束此次do循环.....");
                    SleepUtil.sleepUtil(15000);
                    continue;
                }
                System.out.println("正常操作开始.....");
                SleepUtil.sleepUtil(4000);

                // 点击刷新按钮，确保正常连接
                driver.findElement(By.xpath("//*[@id=\"asianView\"]/div/div[3]/div[1]/div[2]/button")).click();
                List<WebElement> table = driver.findElements(By.tagName("table"));
                log.info("table头部数据 -> {}", table.size());
                // 循环判断最近的篮球赛事
                for (WebElement e : table) {
                    SleepUtil.sleepUtil(2000);
                    driver.findElement(By.xpath("//*[@id=\"asianView\"]/div/div[3]/div[1]/div[2]/button")).click();
                    SleepUtil.sleepUtil(2000);
                    // 判断不为空的篮球赛事
                    List<WebElement> trs = e.findElements(By.tagName("tr"));
                    if (trs.size() >= 2) {
                        List<WebElement> td1 = trs.get(1).findElements(By.tagName("td"));
                        List<WebElement> td2 = trs.get(2).findElements(By.tagName("td"));
                        if (td1.size() > 2 && td2.size() > 2) {
                            // 比赛队伍名称
                            String zd = td1.get(1).getText();
                            zd = zd.replaceAll("\r|\n", "P").split("P")[0];
                            String kd = td2.get(0).getText();
                            kd = kd.replaceAll("\r|\n", "P").split("P")[0];
                            if (StrUtil.isNotBlank(kd) && StrUtil.isNotBlank(zd) && !td1.get(0).getText().contains("即将开赛")) {
                                // 比赛第几节/时间
                                String[] split = td1.get(0).getText().replaceAll("\r|\n", "P").split("P");
                                // 第几节
                                String djj = split[0];
                                // 比赛剩余时间
                                String sysj = split[1];
                                // 判断是否支持下注，或者获取比分
                                int check = checkBet(zd, djj, sysj);
                                if (check == 0) {
                                    // 无需下注
                                    System.out.println("&&&&&&&&&&&&&&&&");
                                    System.out.println("无需下注....");
                                    System.out.println("比赛进行中:" + djj);
                                    System.out.println(zd + " VS " + kd);
                                    System.out.println("&&&&&&&&&&&&&&&&");
                                } else if (check == 1) {
                                    // 获取比分，判断是否需要下注
                                    // String text = td2.get(5).getText();
                                    // System.out.println("我是获取的数据：" + text);
                                    td2.get(5).findElement(By.tagName("p")).click();
                                    SleepUtil.sleepUtil(2000);
                                    if (checkScore(driver, zd, djj) == 1) {
                                        // 红单不需要下注
                                        System.out.println("&&&&&&&&&&&&&&&&");
                                        System.out.println("红单，无需下注....");
                                        System.out.println("比赛进行中:" + djj);
                                        System.out.println(zd + " VS " + kd);
                                        System.out.println("&&&&&&&&&&&&&&&&");
                                        driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[1]/ul/li[2]/div/p")).click();
                                        break;
                                    }
                                } else if (check == 2) {
                                    // 支持下注
                                    System.out.println("=================");
                                    System.out.println("比赛进行中:" + djj);
                                    System.out.println(zd + " VS " + kd);
                                    SleepUtil.sleepUtil(2000);
                                    System.out.println("下注完成....");
                                    sendBet(driver, zd, kd, djj, trs, td1);
                                }
                                SleepUtil.sleepUtil(2000);
                            }
                        }
                    }
                }
                System.out.println("重新获取最新table.....");
            } while (true);
        } catch (Exception e) {
            d.sendMassage("遇到未知错误，关闭浏览器，重新打开");
            S_W = 0;
            driver.quit();
        }
    }

    /**
     * 描述 ： 校验是否购买
     *
     * @param zd   主队名称【犹他爵士】
     * @param djj  第几节比赛【第1节】
     * @param sysj 比赛时间【10:20】
     * @return 0【无需下注】；1【获取篮球比分，判断是否红单】；2【可下注】
     */
    public static int checkBet(String zd, String djj, String sysj) {
        // 判断该场比赛是否已经购买
        String cache = fifoCache.get(zd);
        if (StrUtil.isBlank(cache)) {
            // 不存在,且比赛时间不能小于4分钟，则允许下注
            if (FIRST.equals(djj) && Integer.parseInt(sysj.split(":")[0]) >= 4) {
                return 2;
            }
        } else {
            // 存在判断逻辑
            BetCacheSpec betCacheSpec = JSON.parseObject(cache, BetCacheSpec.class);
            // 如果相等，则存在购买，则不需要下注
            if (betCacheSpec.getIsRed() == 0) {
                // 判断是否存在单节
                if (betCacheSpec.getNode().equals(djj)) {
                    return 0;
                } else {
                    // 判断是否需要获取篮球比分
                    return 1;
                }
            } else if (betCacheSpec.getIsRed() == 1) {
                // 不需要下注
                return 0;
            } else if (betCacheSpec.getIsRed() == 2) {
                // 需要下注
                return 2;
            }
        }
        return 0;
    }

    /**
     * 描述 ： 校验比分，是否红单
     *
     * @param driver 控制浏览器
     * @param zd     主队名称【犹他爵士】
     * @param djj    第几节
     * @return
     */
    public static int checkScore(WebDriver driver, String zd, String djj) {
        // 获取缓存数据
        int check = 0;
        String cache = fifoCache.get(zd);
        BetCacheSpec betCacheSpec = JSON.parseObject(cache, BetCacheSpec.class);
        betCacheSpec.setNode(djj);
        if (SECOND.equals(djj)) {
            // 主队第一节比分
            String zdtext1 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[5]/div/ng-include/live-game-scores/div[1]/div[1]/div/div/div/div/ng-include/div/div[1]/ul[2]/li[2]/span")).getText();
            // 客队第一节比分
            String kdtext1 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[5]/div/ng-include/live-game-scores/div[1]/div[1]/div/div/div/div/ng-include/div/div[1]/ul[3]/li[2]/span")).getText();
            System.out.println("我是获取比赛分数：" + zdtext1 + " : " + kdtext1);
            if (Integer.parseInt(zdtext1) % Integer.parseInt(kdtext1) == 1) {
                betCacheSpec.setIsRed(1);
                check = 1;
                addBuyRecord(betCacheSpec, 0);
                fifoCache.remove(betCacheSpec.getHomeTeam());
            } else {
                betCacheSpec.setIsRed(2);
                betCacheSpec.setMagnification(betCacheSpec.getMagnification() + 1);
                check = 2;
                addBuyRecord(betCacheSpec, 1);
                fifoCache.put(betCacheSpec.getHomeTeam(), JSON.toJSONString(betCacheSpec));
            }
        } else if (THIRD.equals(djj)) {
            // 主队第二节比分
            String zdtext2 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[5]/div/ng-include/live-game-scores/div[1]/div[1]/div/div/div/div/ng-include/div/div[1]/ul[2]/li[3]/span")).getText();
            // 客队第二节比分
            String kdtext2 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[5]/div/ng-include/live-game-scores/div[1]/div[1]/div/div/div/div/ng-include/div/div[1]/ul[3]/li[3]/span")).getText();
            System.out.println("我是获取比赛分数：" + zdtext2 + " : " + kdtext2);
            if (Integer.parseInt(zdtext2) % Integer.parseInt(kdtext2) == 1) {
                betCacheSpec.setIsRed(1);
                check = 1;
                addBuyRecord(betCacheSpec, 0);
                fifoCache.remove(betCacheSpec.getHomeTeam());
            } else {
                betCacheSpec.setIsRed(2);
                betCacheSpec.setMagnification(betCacheSpec.getMagnification() + 1);
                check = 2;
                addBuyRecord(betCacheSpec, 1);
                fifoCache.put(betCacheSpec.getHomeTeam(), JSON.toJSONString(betCacheSpec));
            }
        } else if (FOURTH.equals(djj)) {
            // 主队第三节比分
            String zdtext3 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[5]/div/ng-include/live-game-scores/div[1]/div[1]/div/div/div/div/ng-include/div/div[1]/ul[2]/li[4]/span")).getText();
            // 客队第三节比分
            String kdtext3 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[5]/div/ng-include/live-game-scores/div[1]/div[1]/div/div/div/div/ng-include/div/div[1]/ul[3]/li[4]/span")).getText();
            System.out.println("我是获取比赛分数：" + zdtext3 + " : " + kdtext3);
            if (Integer.parseInt(zdtext3) % Integer.parseInt(kdtext3) == 1) {
                betCacheSpec.setIsRed(1);
                check = 1;
                addBuyRecord(betCacheSpec, 0);
                fifoCache.remove(betCacheSpec.getHomeTeam());
            } else {
                // 将三黑数据保存到黑集合中
                betCacheSpec.setIsRed(2);
                betCacheSpec.setMagnification(betCacheSpec.getMagnification() + 1);
                map.add(betCacheSpec);
                check = 2;
                addBuyRecord(betCacheSpec, 1);
                fifoCache.remove(betCacheSpec.getHomeTeam());
            }
        }
        return check;
    }

    /**
     * 发送下注记录
     *
     * @param betCacheSpec
     */
    public static void addBuyRecord(BetCacheSpec betCacheSpec, int type) {
        BuyRecordSpec b = new BuyRecordSpec();
        b.setJson(JSON.toJSONString(betCacheSpec));
        b.setType(type);
        try {
            HttpUtil.post(ADD_URL, JSON.toJSONString(b));
        } catch (Exception e) {
            DingUtil d = new DingUtil();
            d.sendMassage("保存下注记录有问题，请关注该比赛：" + JSON.toJSONString(betCacheSpec));
        }
    }

    /**
     * 描述 ： 下注
     *
     * @param driver 控制浏览器
     * @param zd     主队名称【犹他爵士】
     * @param djj    第几节
     * @param trs
     * @param td1
     */
    public static void sendBet(WebDriver driver, String zd, String kd, String djj, List<WebElement> trs, List<WebElement> td1) {
        // 点击下注
        List<WebElement> td0 = trs.get(0).findElements(By.tagName("th"));
        // 获取点击的数据
        List<WebElement> li = td0.get(1).findElements(By.tagName("li"));
        for (WebElement w : li) {
            if (djj.equals(w.getText())) {
                System.out.println("我是查询出来的滚球节数：" + w.getText());
                w.click();
                SleepUtil.sleepUtil(2000);
                break;
            }
        }
        String cache = fifoCache.get(zd);
        BetCacheSpec betCacheSpec = new BetCacheSpec();
        if (StrUtil.isBlank(cache)) {
            betCacheSpec.setHomeTeam(zd);
            betCacheSpec.setAwayTeam(kd);
            betCacheSpec.setNode(FIRST);
            betCacheSpec.setIsRed(0);
            betCacheSpec.setOddAndEven(0);
            betCacheSpec.setNumber(0);
            betCacheSpec.setMagnification(0);
        } else {
            betCacheSpec = JSON.parseObject(cache, BetCacheSpec.class);
        }
        // 开始下注买
        if (betCacheSpec.getNumber() == 0) {
            if(map.size() == 0){
                // 判断是否有三黑的数据
                String result = HttpUtil.get(GET_URL);
                System.out.println("result:" + JSON.parseObject(result).get("data"));
                List<BuyRecordJson> bls = JSON.parseArray(JSON.parseObject(result).get("data").toString(), BuyRecordJson.class);
                if(bls.size() > 0){
                    System.out.println("将查询出来的黑单，设置在发送盒子上");
                    for(BuyRecordJson br : bls){
                        map.add(JSON.parseObject(br.getJson(), BetCacheSpec.class));
                    }
                }
            }
            if (map.size() > 0) {
                BetCacheSpec betCacheSpec1 = map.get(0);
                betCacheSpec.setMagnification(betCacheSpec1.getMagnification());
                // 清空已设置倍率的数据
                map.remove(0);
                HttpUtil.get(DEL_URL);
            }
            // 购买第一场比赛
            betCacheSpec.setIsRed(0);
            betCacheSpec.setNumber(1);
            sendBetOk(driver, td1, betCacheSpec.getMagnification());
            fifoCache.put(zd, JSON.toJSONString(betCacheSpec));
            System.out.println("第一节比赛已购买,下注金额[" + bl[betCacheSpec.getMagnification()] + "]....");
        } else if (betCacheSpec.getNumber() == 1) {
            // 购买第二场比赛
            betCacheSpec.setIsRed(0);
            betCacheSpec.setNumber(2);
            sendBetOk(driver, td1, betCacheSpec.getMagnification());
            fifoCache.put(zd, JSON.toJSONString(betCacheSpec));
            System.out.println("第二节比赛已购买,下注金额[" + bl[betCacheSpec.getMagnification()] + "]....");
        } else if (betCacheSpec.getNumber() == 2) {
            // 购买第三场比赛
            betCacheSpec.setIsRed(0);
            betCacheSpec.setNumber(3);
            sendBetOk(driver, td1, betCacheSpec.getMagnification());
            fifoCache.put(zd, JSON.toJSONString(betCacheSpec));
            System.out.println("第三节比赛已购买,下注金额[" + bl[betCacheSpec.getMagnification()] + "]....");
        }
    }

    /**
     * 描述：发送最终下注
     *
     * @param driver
     * @param td1
     * @param magnification
     */
    public static void sendBetOk(WebDriver driver, List<WebElement> td1, int magnification) {
        // 点击下注
        System.out.println("我点击购买单：" + td1.get(5).getText());
        td1.get(5).click();
        SleepUtil.sleepUtil(2000);
        WebElement elementZh = driver.findElement(By.xpath("//*[@id=\"express-bet-input\"]"));
        // 清空输入框
        elementZh.clear();
        elementZh.sendKeys(bl[magnification]);
        SleepUtil.sleepUtil(2000);
        // 点击确认按钮
        driver.findElement(By.xpath("//*[@id=\"asianView\"]/div/div[1]/div/div/div[1]/div[2]/div/div[5]/div[2]/button[3]")).click();
        SleepUtil.sleepUtil(40000);
    }
}
