package com.example.selenium.bet;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.CacheObj;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.example.selenium.spec.BetCacheSpec;
import com.example.selenium.spec.BuyRecordJson;
import com.example.selenium.spec.BuyRecordSpec;
import com.example.selenium.util.DingUtil;
import com.example.selenium.util.SleepUtil;
import lombok.Data;
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
    // 是否开始下注
    private static String OFF_URL = "http://47.106.143.218:8081/instruction/get/switch";
    // 是否开启下注 ON -> 开启
    private static String IS_ON = "ON";
    // 系统缓存 TimedCache<String, String> timedCache = CacheUtil.newTimedCache(14400);
    public static Cache<String, String> fifoCache = CacheUtil.newFIFOCache(1000);
    // 电子超时标识
    private static int K_G = 0;
    // 保存三黑的数据
    private static List<BetCacheSpec> map = new ArrayList<>();
    // 下注倍率:0.94倍率
    // private static String[] bl = new String[]{"10","20","40","80","165","335","700"};
    // private static String[] bl = new String[]{"12", "24", "48", "97", "199", "405", "840", "1680"};
    private static String[] bl = new String[]{"10","20","40","80","165","335","700"};
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
    public static void main(String[] args) {
        bet();
    }

    public static void bet() {
        if (!HttpUtil.get(OFF_URL).contains(IS_ON)) {
            DingUtil dingUtil = new DingUtil();
            dingUtil.sendMassage("下注通道已关闭，请时刻注意");
            return;
        }
        if (1 == S_W) {
            return;
        }
        S_W = 1;
        // chromeDriver服务地址，需要手动下载
        // 测试环境：[D:\00002YX\123\chromedriver.exe]地址需要自己给
        // String chromeDriverUrl = "C:\\software\\chrome\\chromedriver.exe";
        // 正式环境：System.getProperty("user.dir")+"\\src\\main\\resources\\chromedriver.exe";
        // 笔记本版本 D:\ChromeCoreDownloads\chromedriver.exe
        String chromeDriverUrl = System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverUrl);
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
            do {
                if (!HttpUtil.get(OFF_URL).contains(IS_ON)) {
                    DingUtil dingUtil = new DingUtil();
                    dingUtil.sendMassage("下注通道已关闭，请时刻注意");
                    driver.quit();
                }
                SleepUtil.sleepUtil(4000);
                driver.navigate().refresh();
                SleepUtil.sleepUtil(4000);
                // 滑动滚动条
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 240)");
                SleepUtil.sleepUtil(4000);
                // 存在iframe,首先需要进到iframe
                driver.switchTo().frame("iframe");
                SleepUtil.sleepUtil(4000);

                // 点击滚球按钮
                driver.findElement(By.xpath("//*[@id=\"nav\"]/a[1]")).click();
                SleepUtil.sleepUtil(4000);

                // 获取新的ifram
                driver.switchTo().frame("bcsportsbookiframe");
                SleepUtil.sleepUtil(4000);

                // 点击刷新按钮，确保正常连接
                driver.findElement(By.xpath("//*[@id=\"asianView\"]/div/div[3]/div[1]/div[2]/button")).click();
                // 点击篮球 sport-name-asia
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
                    SleepUtil.sleepUtil(15000);
                    continue;
                }
                SleepUtil.sleepUtil(4000);
                System.out.println("====================");
                System.out.println("我是判断电子筛选：" + K_G);
                System.out.println("====================");
                if (K_G == 0) {
                    try {
                        // 选择联赛 /html/body/div/div[2]/div/div/div/div/div/div[3]/div[1]/ul/li[4]
                        driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[1]/ul/li[4]")).click();
                        SleepUtil.sleepUtil(2000);
                        // 选择联赛排序：all-filter-competitions
                        List<WebElement> element = driver.findElements(By.className("all-filter-competitions"));
                        SleepUtil.sleepUtil(1000);
                        List<WebElement> lis = element.get(0).findElements(By.tagName("li"));
                        if (lis.size() > 1) {
                            for (WebElement li : lis) {
                                if (li.getText().contains("电子")) {
                                    li.click();
                                    SleepUtil.sleepUtil(1000);
                                    break;
                                }
                            }
                        }
                        SleepUtil.sleepUtil(1000);
                        // 点击OK /html/body/div/div[2]/div/div/div/div/div/div[3]/div[8]/div/div/div/div[1]/div[2]/ul/li[3]/button
                        List<WebElement> elements = driver.findElement(By.className("filter-function-b")).findElements(By.tagName("li"));
                        for (WebElement li : elements) {
                            if ("OK".equals(li.getText().trim())) {
                                li.click();
                                SleepUtil.sleepUtil(1000);
                            }
                        }
                        SleepUtil.sleepUtil(1000);
                    } catch (Exception e) {
                        // fifoCache.put("SXOK", "NOT", DateUnit.SECOND.getMillis() * 900);
                        ++K_G;
                        d.sendMassage("筛选电子比赛失败，请注意比赛！！！！！！");
                        driver.quit();
                    }
                } else {
                    ++K_G;
                    // 判断获取电子筛选失败，等于10，重置
                    if (K_G == 10) {
                        K_G = 0;
                    }
                }
                SleepUtil.sleepUtil(4000);
                // 点击刷新按钮，确保正常连接
                driver.findElement(By.xpath("//*[@id=\"asianView\"]/div/div[3]/div[1]/div[2]/button")).click();
                List<WebElement> table = driver.findElements(By.tagName("table"));
                int size = table.size();
                log.info("table头部数据 -> {}", size);
                // 循环判断最近的篮球赛事
                for (int i = 0; i < size; i++) {
                    SleepUtil.sleepUtil(1000);
                    driver.findElement(By.xpath("//*[@id=\"asianView\"]/div/div[3]/div[1]/div[2]/button")).click();
                    SleepUtil.sleepUtil(2000);
                    // 判断不为空的篮球赛事
                    List<WebElement> tbodys = new ArrayList<WebElement>();
                    try {
                        tbodys = table.get(i).findElements(By.tagName("tbody"));
                    } catch (Exception e1) {
                        continue;
                    }
                    for (WebElement tb : tbodys) {
                        List<WebElement> trs = tb.findElements(By.tagName("tr"));
                        if (trs.size() >= 2) {
                            List<WebElement> td1 = trs.get(1).findElements(By.tagName("td"));
                            List<WebElement> td2 = trs.get(2).findElements(By.tagName("td"));
                            if (td1.size() > 2 && td2.size() > 2) {
                                // 比赛队伍名称
                                String zd = td1.get(1).getText();
                                zd = zd.replaceAll("\r|\n", "P").split("P")[0];
                                String kd = td2.get(0).getText();
                                kd = kd.replaceAll("\r|\n", "P").split("P")[0];
                                // 添加不购买电子的比赛
                                if (StrUtil.isNotBlank(kd) && StrUtil.isNotBlank(zd) && StrUtil.isNotBlank(td1.get(0).getText()) && (td1.get(0).getText().contains(FIRST) || td1.get(0).getText().contains(SECOND) || td1.get(0).getText().contains(THIRD) || td1.get(0).getText().contains(FOURTH)) && !(zd.contains("电子") || kd.contains("电子"))) {
                                    // 比赛第几节/时间
                                    String[] split = td1.get(0).getText().replaceAll("\r|\n", "P").split("P");
                                    // 防止数组溢出
                                    if (split.length == 2) {
                                        // 第几节
                                        String djj = split[0];
                                        // 比赛剩余时间
                                        String sysj = split[1];
                                        // 判断是否支持下注，或者获取比分
                                        int check = checkBet(zd, djj, sysj);
                                        if (check == 0) {
                                            // 无需下注
                                            System.out.println("==============================");
                                            System.out.println("比赛进行中[0 : 无需下注]:" + djj + "/" + sysj);
                                            System.out.println(zd + " VS " + kd);
                                            System.out.println("==============================");
                                            System.out.println("|||||||||||||||||||||||||||||||");
                                        } else if (check == 1) {
                                            // 获取比分，判断是否需要下注
                                            // String text = td2.get(5).getText();
                                            // System.out.println("我是获取的数据：" + text);
                                            td2.get(5).findElement(By.tagName("p")).click();
                                            SleepUtil.sleepUtil(3000);
                                            if (checkScore(driver, zd, djj) == 1) {
                                                // 红单不需要下注
                                                System.out.println("==============================");
                                                System.out.println("比赛进行中[1 : 无需下注]:" + djj + "/" + sysj);
                                                System.out.println(zd + " VS " + kd);
                                                System.out.println("==============================");
                                                System.out.println("|||||||||||||||||||||||||||||||");
                                                driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[1]/ul/li[2]/div/p")).click();
                                            }
                                            break;
                                        } else if (check == 2) {
                                            // 支持下注
                                            SleepUtil.sleepUtil(3000);
                                            System.out.println("==============================");
                                            System.out.println("比赛进行中[2 : 需下注]:" + djj + "/" + sysj);
                                            System.out.println(zd + " VS " + kd);
                                            System.out.println("==============================");
                                            System.out.println("|||||||||||||||||||||||||||||||");
                                            sendBet(driver, zd, kd, djj, trs, td1, td2);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } while (true);
        } catch (Exception e) {
            d.sendMassage("遇到未知错误，关闭浏览器，重新打开[错误信息：" + e.getMessage() + "]");
            System.out.println(e);
            S_W = 0;
            driver.quit();
        }
    }

    /**
     * 获取账号金额
     *
     * @return
     */
    public static String getAmount(WebDriver driver) {
        // 获取金额  balance-int-row
        try {
            String text = driver.findElement(By.className("balance-int-row")).getText();
            String amount = text.trim().split("余额:")[1];
            SleepUtil.sleepUtil(500);
            return amount.split("C")[0].trim();
        } catch (Exception e) {
            DingUtil dingUtil = new DingUtil();
            dingUtil.sendMassage("获取账号金额失败，请注意！！！！");
        }
        return "0.00";
    }

    /**
     * 滑动滚动条
     *
     * @param size
     * @param driver
     */
    public static void scroll(int size, WebDriver driver) {
        if (size == 4) {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
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
            int size = Integer.parseInt(sysj.split(":")[0]);
            if (FIRST.equals(djj) && (size >= 4 && size < 12)) {
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
        DingUtil d = new DingUtil();
        BetCacheSpec betCacheSpec = new BetCacheSpec();
        try {
            int check = 0;
            String cache = fifoCache.get(zd);
            betCacheSpec = JSON.parseObject(cache, BetCacheSpec.class);
            // 获取当前对比节点
            String node = betCacheSpec.getNode();
            betCacheSpec.setNode(djj);
            if (SECOND.equals(djj)) {
                // 主队第一节比分
                String zdtext1 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[5]/div/ng-include/live-game-scores/div[1]/div[1]/div/div/div/div/ng-include/div/div[1]/ul[2]/li[2]/span")).getText();
                // 客队第一节比分
                String kdtext1 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[5]/div/ng-include/live-game-scores/div[1]/div[1]/div/div/div/div/ng-include/div/div[1]/ul[3]/li[2]/span")).getText();
                System.out.println("我是获取比赛分数：" + zdtext1 + " : " + kdtext1);
                if (StrUtil.isNotBlank(zdtext1) && StrUtil.isNotBlank(kdtext1)) {
                    if ((Integer.parseInt(zdtext1.trim()) + Integer.parseInt(kdtext1.trim())) % 2 == betCacheSpec.getOddAndEven()) {
                        betCacheSpec.setIsRed(1);
                        betCacheSpec.setScore(zdtext1 + ":" + kdtext1);
                        check = 1;
                        betCacheSpec.setNode(node);
                        d.sendMassage("该比赛已经红单，请关注该比赛,是否有出入[下注单双：" + (betCacheSpec.getOddAndEven() == 1 ? '单' : '双') + "][" + node + "][" + betCacheSpec.getMagnification() + "][" + betCacheSpec.getScore() + "]：[" + betCacheSpec.getHomeTeam() + " VS " + betCacheSpec.getAwayTeam() + "]");
                        d.sendMassage("[账户剩余金额：" + getAmount(driver) + "]");
                        addBuyRecord(betCacheSpec, 0);
                        fifoCache.remove(betCacheSpec.getHomeTeam());
                    } else {
                        betCacheSpec.setIsRed(2);
                        betCacheSpec.setScore(zdtext1 + ":" + kdtext1);
                        betCacheSpec.setNumber(1);
                        check = 2;
                        d.sendMassage("该比赛已经黑单，请关注该比赛,是否有出入[下注单双：" + (betCacheSpec.getOddAndEven() == 1 ? '单' : '双') + "][" + node + "][" + betCacheSpec.getMagnification() + "][" + betCacheSpec.getScore() + "]：[" + betCacheSpec.getHomeTeam() + " VS " + betCacheSpec.getAwayTeam() + "]");
                        // 设置四小时失效
                        fifoCache.put(betCacheSpec.getHomeTeam(), JSON.toJSONString(betCacheSpec), DateUnit.SECOND.getMillis() * 14400);
                    }
                }
            } else if (THIRD.equals(djj)) {
                // 主队第二节比分
                String zdtext2 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[5]/div/ng-include/live-game-scores/div[1]/div[1]/div/div/div/div/ng-include/div/div[1]/ul[2]/li[3]/span")).getText();
                // 客队第二节比分
                String kdtext2 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[5]/div/ng-include/live-game-scores/div[1]/div[1]/div/div/div/div/ng-include/div/div[1]/ul[3]/li[3]/span")).getText();
                System.out.println("我是获取比赛分数：" + zdtext2 + " : " + kdtext2);
                if (StrUtil.isNotBlank(zdtext2) && StrUtil.isNotBlank(kdtext2)) {
                    if ((Integer.parseInt(zdtext2.trim()) + Integer.parseInt(kdtext2.trim())) % 2 == betCacheSpec.getOddAndEven()) {
                        betCacheSpec.setIsRed(1);
                        betCacheSpec.setScore(zdtext2 + ":" + kdtext2);
                        check = 1;
                        betCacheSpec.setNode(node);
                        d.sendMassage("该比赛已经红单，请关注该比赛,是否有出入[下注单双：" + (betCacheSpec.getOddAndEven() == 1 ? '单' : '双') + "][" + node + "][" + betCacheSpec.getMagnification() + "][" + betCacheSpec.getScore() + "]：[" + betCacheSpec.getHomeTeam() + " VS " + betCacheSpec.getAwayTeam() + "]");
                        d.sendMassage("[账户剩余金额：" + getAmount(driver) + "]");
                        addBuyRecord(betCacheSpec, 0);
                        fifoCache.remove(betCacheSpec.getHomeTeam());
                    } else {
                        betCacheSpec.setIsRed(2);
                        betCacheSpec.setScore(zdtext2 + ":" + kdtext2);
                        betCacheSpec.setNumber(2);
                        d.sendMassage("该比赛已经黑单，请关注该比赛,是否有出入[下注单双：" + (betCacheSpec.getOddAndEven() == 1 ? '单' : '双') + "][" + node + "][" + betCacheSpec.getMagnification() + "][" + betCacheSpec.getScore() + "]：[" + betCacheSpec.getHomeTeam() + " VS " + betCacheSpec.getAwayTeam() + "]");
                        if (betCacheSpec.getMagnification() == 7) {
                            d.sendMassage("该比赛已经八黑，请关注该比赛,是否有出入[下注单双：" + (betCacheSpec.getOddAndEven() == 1 ? '单' : '双') + "][" + node + "][" + betCacheSpec.getMagnification() + "]：[" + betCacheSpec.getHomeTeam() + " VS " + betCacheSpec.getAwayTeam() + "]");
                            d.sendMassage("兄弟，很遗憾，江湖难免受阻，大势已去，一炮回到解放前，下次投注请慎重");
                            fifoCache.remove(betCacheSpec.getHomeTeam());
                        } else {
                            check = 2;
                            betCacheSpec.setScore(zdtext2 + ":" + kdtext2);
                            // 设置四小时失效
                            fifoCache.put(betCacheSpec.getHomeTeam(), JSON.toJSONString(betCacheSpec), DateUnit.SECOND.getMillis() * 14400);
                        }
                    }
                }
            } else if (FOURTH.equals(djj)) {
                // 主队第三节比分
                String zdtext3 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[5]/div/ng-include/live-game-scores/div[1]/div[1]/div/div/div/div/ng-include/div/div[1]/ul[2]/li[4]/span")).getText();
                // 客队第三节比分
                String kdtext3 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[5]/div/ng-include/live-game-scores/div[1]/div[1]/div/div/div/div/ng-include/div/div[1]/ul[3]/li[4]/span")).getText();
                System.out.println("我是获取比赛分数：" + zdtext3 + " : " + kdtext3);
                if (StrUtil.isNotBlank(zdtext3) && StrUtil.isNotBlank(kdtext3)) {
                    if ((Integer.parseInt(zdtext3.trim()) + Integer.parseInt(kdtext3.trim())) % 2 == betCacheSpec.getOddAndEven()) {
                        betCacheSpec.setIsRed(1);
                        betCacheSpec.setScore(zdtext3 + ":" + kdtext3);
                        check = 1;
                        betCacheSpec.setNode(node);
                        d.sendMassage("该比赛已经红单，请关注该比赛,是否有出入[下注单双：" + (betCacheSpec.getOddAndEven() == 1 ? '单' : '双') + "][" + node + "][" + betCacheSpec.getMagnification() + "][" + betCacheSpec.getScore() + "]：[" + betCacheSpec.getHomeTeam() + " VS " + betCacheSpec.getAwayTeam() + "]");
                        d.sendMassage("[账户剩余金额：" + getAmount(driver) + "]");
                        addBuyRecord(betCacheSpec, 0);
                        fifoCache.remove(betCacheSpec.getHomeTeam());
                    } else {
                        // 将三/六黑数据保存到黑集合中
                        betCacheSpec.setIsRed(2);
                        betCacheSpec.setScore(zdtext3 + ":" + kdtext3);
                        betCacheSpec.setNumber(3);
                        map.add(betCacheSpec);
                        check = 2;
                        d.sendMassage("该比赛已经黑单，请关注该比赛,是否有出入[下注单双：" + (betCacheSpec.getOddAndEven() == 1 ? '单' : '双') + "][" + node + "][" + betCacheSpec.getMagnification() + "][" + betCacheSpec.getScore() + "]：[" + betCacheSpec.getHomeTeam() + " VS " + betCacheSpec.getAwayTeam() + "]");
                        addBuyRecord(betCacheSpec, 1);
                        fifoCache.remove(betCacheSpec.getHomeTeam());
                        if (betCacheSpec.getMagnification() == 2) {
                            d.sendMassage("该比赛已经三黑，请关注该比赛,是否有出入[下注单双：" + (betCacheSpec.getOddAndEven() == 1 ? '单' : '双') + "][" + node + "][" + betCacheSpec.getMagnification() + "][" + betCacheSpec.getHomeTeam() + " VS " + betCacheSpec.getAwayTeam() + "]");
                        } else if (betCacheSpec.getMagnification() == 5) {
                            d.sendMassage("该比赛已经六黑，请关注该比赛,是否有出入[下注单双：" + (betCacheSpec.getOddAndEven() == 1 ? '单' : '双') + "][" + node + "][" + betCacheSpec.getMagnification() + "][" + betCacheSpec.getHomeTeam() + " VS " + betCacheSpec.getAwayTeam() + "]");
                        }
                    }
                }
            }
            return check;
        } catch (Exception e) {
            System.out.println("获取比赛分数报错 : " + e);
            d.sendMassage("获取比赛分数报错,重新打开浏览器[错误信息：" + e.getMessage() + "]：" + JSON.toJSONString(betCacheSpec));
            S_W = 0;
            driver.quit();
        }
        return 0;
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
     * 单双随机数
     *
     * @return
     */
    public static int random() {
        int max = 100, min = 1;
        int ran2 = (int) (Math.random() * (max - min) + min);
        return ran2 % 2;
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
    public static void sendBet(WebDriver driver, String zd, String kd, String djj, List<WebElement> trs, List<WebElement> td1, List<WebElement> td2) {
        // 点击下注
        List<WebElement> td0 = trs.get(0).findElements(By.tagName("th"));
        // 获取点击的数据
        List<WebElement> li = td0.get(1).findElements(By.tagName("li"));
        Boolean flag_2 = false;
        for (WebElement w : li) {
            if (djj.equals(w.getText())) {
                w.click();
                SleepUtil.sleepUtil(1000);
                flag_2 = true;
                break;
            }
        }
        // 查询不到对应节点数据，不下注
        DingUtil d = new DingUtil();
        if (!flag_2) {
            System.out.println("该比赛未获取滚球节数，请核对是否出入[下注节点：" + djj + "]：" + zd + " VS " + kd);
            // d.sendMassage("该比赛未获取滚球节数，请核对是否出入[下注节点："+djj+"]：" + zd + " VS " + kd);
            return;
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
        String amount = getAmount(driver);
        // 开始下注买
        if (betCacheSpec.getNumber() == 0) {
            if (map.size() == 0) {
                // 判断是否有三黑的数据
                String result = HttpUtil.get(GET_URL);
                List<BuyRecordJson> bls = JSON.parseArray(JSON.parseObject(result).get("data").toString(), BuyRecordJson.class);
                if (bls.size() > 0) {
                    for (BuyRecordJson br : bls) {
                        map.add(JSON.parseObject(br.getJson(), BetCacheSpec.class));
                    }
                }
            }
            Boolean flag_1 = false;
            if (map.size() > 0 && !(zd.contains("电子") || kd.contains("电子"))) {
                BetCacheSpec betCacheSpec1 = map.get(0);
                betCacheSpec.setMagnification(betCacheSpec1.getMagnification() + 1);
                flag_1 = true;
            }
            // 购买第一场比赛
            betCacheSpec.setIsRed(0);
            // 设置单双
            betCacheSpec.setOddAndEven(random());
            Boolean flag = sendBetOk(driver, td1, td2, betCacheSpec, zd, kd, djj);
            if (flag) {
                // 保存购买记录
                betCacheSpec.setNumber(0);
                addBuyRecord(betCacheSpec, 2);
                // 设置四小时失效
                fifoCache.put(zd, JSON.toJSONString(betCacheSpec), DateUnit.SECOND.getMillis() * 14400);
                // 点击刷新按钮，确保正常连接
                driver.findElement(By.xpath("//*[@id=\"asianView\"]/div/div[3]/div[1]/div[2]/button")).click();
                SleepUtil.sleepUtil(3000);
                d.sendMassage("第一节比赛已购买,下注比赛[下注单双：" + (betCacheSpec.getOddAndEven() == 1 ? '单' : '双') + "][下注节点：" + betCacheSpec.getNode() + "][下注金额：" + bl[betCacheSpec.getMagnification()] + "]：" + betCacheSpec.getHomeTeam() + " VS " + betCacheSpec.getAwayTeam());
                d.sendMassage("[账户原金额：" + amount + "][下注金额：" + bl[betCacheSpec.getMagnification()] + "]" + "[账户剩余金额：" + getAmount(driver) + "]");
                if (flag_1) {
                    // 清空已设置倍率的数据
                    map.remove(0);
                    HttpUtil.get(DEL_URL);
                }
            }
        } else if (betCacheSpec.getNumber() == 1) {
            // 购买第二场比赛
            betCacheSpec.setIsRed(0);
            // 设置单双
            betCacheSpec.setOddAndEven(random());
            betCacheSpec.setMagnification(betCacheSpec.getMagnification() + 1);
            Boolean flag = sendBetOk(driver, td1, td2, betCacheSpec, zd, kd, djj);
            if (flag) {
                // 保存购买记录
                betCacheSpec.setNumber(1);
                // 设置单双
                betCacheSpec.setOddAndEven(random());
                addBuyRecord(betCacheSpec, 2);
                // 设置四小时失效
                fifoCache.put(zd, JSON.toJSONString(betCacheSpec), DateUnit.SECOND.getMillis() * 14400);
                // 刷新数据
                driver.findElement(By.xpath("//*[@id=\"asianView\"]/div/div[3]/div[1]/div[2]/button")).click();
                SleepUtil.sleepUtil(3000);
                d.sendMassage("第二节比赛已购买,下注比赛[下注单双：" + (betCacheSpec.getOddAndEven() == 1 ? '单' : '双') + "][下注节点：" + betCacheSpec.getNode() + "][下注金额：" + bl[betCacheSpec.getMagnification()] + "]：" + betCacheSpec.getHomeTeam() + " VS " + betCacheSpec.getAwayTeam());
                d.sendMassage("[账户原金额：" + amount + "][下注金额：" + bl[betCacheSpec.getMagnification()] + "]" + "[账户剩余金额：" + getAmount(driver) + "]");
            }
        } else if (betCacheSpec.getNumber() == 2) {
            // 购买第三场比赛
            betCacheSpec.setIsRed(0);
            // 设置单双
            betCacheSpec.setOddAndEven(random());
            betCacheSpec.setMagnification(betCacheSpec.getMagnification() + 1);
            Boolean flag = sendBetOk(driver, td1, td2, betCacheSpec, zd, kd, djj);
            if (flag) {
                // 保存购买记录
                betCacheSpec.setNumber(2);
                addBuyRecord(betCacheSpec, 2);
                // 设置四小时失效
                fifoCache.put(zd, JSON.toJSONString(betCacheSpec), DateUnit.SECOND.getMillis() * 14400);
                // 刷新数据
                driver.findElement(By.xpath("//*[@id=\"asianView\"]/div/div[3]/div[1]/div[2]/button")).click();
                SleepUtil.sleepUtil(3000);
                d.sendMassage("第三节比赛已购买,下注比赛[下注单双：" + (betCacheSpec.getOddAndEven() == 1 ? '单' : '双') + "][下注节点：" + betCacheSpec.getNode() + "][下注金额：" + bl[betCacheSpec.getMagnification()] + "]：" + betCacheSpec.getHomeTeam() + " VS " + betCacheSpec.getAwayTeam());
                d.sendMassage("[账户原金额：" + amount + "][下注金额：" + bl[betCacheSpec.getMagnification()] + "]" + "[账户剩余金额：" + getAmount(driver) + "]");
            }
        }
    }

    /**
     * 描述：发送最终下注
     *
     * @param driver
     * @param td1
     * @param magnification
     */
    public static Boolean sendBetOk(WebDriver driver, List<WebElement> td1, List<WebElement> td2, BetCacheSpec betCacheSpec, String zd, String kd, String djj) {
        // 点击下注
        DingUtil d = new DingUtil();
        if (betCacheSpec.getOddAndEven() == 1) {
            if (StrUtil.isNotBlank(td1.get(5).getText()) && td1.get(5).getText().contains("单")) {
                td1.get(5).click();
                SleepUtil.sleepUtil(3000);
                WebElement elementZh = driver.findElement(By.xpath("//*[@id=\"express-bet-input\"]"));
                SleepUtil.sleepUtil(4000);
                // 清空输入框
                elementZh.clear();
                // elementZh.sendKeys(bl[magnification]);
                // 调试下注使用，默认为10
                elementZh.sendKeys(bl[betCacheSpec.getMagnification()]);
                SleepUtil.sleepUtil(2000);
                // //*[@id="betEventsContainer"]/ul/li[3]/p[1]/span
                String sfqc = "";
                try {
                    sfqc = driver.findElement(By.xpath("//*[@id=\"betEventsContainer\"]/ul/li[3]/p[1]/span")).getText();
                    if ("全场总得分单双".equals(sfqc)) {
                        d.sendMassage("下注前判断是否点击全场[是]，禁止该节下注，请关注该比赛[" + djj + "]：" + zd + " VS " + kd);
                        return false;
                    }
                } catch (Exception e) {
                    d.sendMassage("下注前判断是否点击全场，出错，请关注该比赛[" + djj + "]：" + zd + " VS " + kd);
                    return false;
                }
                // 点击确认按钮
                driver.findElement(By.xpath("//*[@id=\"asianView\"]/div/div[1]/div/div/div[1]/div[2]/div/div[5]/div[2]/button[3]")).click();
                SleepUtil.sleepUtil(5000);
                System.out.println("下注完成：" + JSON.toJSONString(betCacheSpec));
                return true;
            }
        } else {
            if (StrUtil.isNotBlank(td2.get(5).getText()) && td2.get(5).getText().contains("单")) {
                td2.get(5).click();
                SleepUtil.sleepUtil(3000);
                WebElement elementZh = driver.findElement(By.xpath("//*[@id=\"express-bet-input\"]"));
                SleepUtil.sleepUtil(4000);
                // 清空输入框
                elementZh.clear();
                // elementZh.sendKeys(bl[magnification]);
                // 调试下注使用，默认为10
                elementZh.sendKeys(bl[betCacheSpec.getMagnification()]);
                SleepUtil.sleepUtil(2000);
                // //*[@id="betEventsContainer"]/ul/li[3]/p[1]/span
                String sfqc = "";
                try {
                    sfqc = driver.findElement(By.xpath("//*[@id=\"betEventsContainer\"]/ul/li[3]/p[1]/span")).getText();
                    if ("全场总得分单双".equals(sfqc)) {
                        d.sendMassage("下注前判断是否点击全场[是]，禁止该节下注，请关注该比赛[" + djj + "]：" + zd + " VS " + kd);
                        return false;
                    }
                } catch (Exception e) {
                    d.sendMassage("下注前判断是否点击全场，出错，请关注该比赛[" + djj + "]：" + zd + " VS " + kd);
                    return false;
                }
                // 点击确认按钮
                driver.findElement(By.xpath("//*[@id=\"asianView\"]/div/div[1]/div/div/div[1]/div[2]/div/div[5]/div[2]/button[3]")).click();
                SleepUtil.sleepUtil(5000);
                System.out.println("下注完成：" + JSON.toJSONString(betCacheSpec));
                return true;
            }
        }
        return false;
    }
}
