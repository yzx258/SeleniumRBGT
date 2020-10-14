package com.example.selenium.bet;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.example.selenium.spec.BetCacheSpec;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

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

    // 系统缓存
    private static Cache<String, String> fifoCache = CacheUtil.newFIFOCache(1000);
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

    public static void main(String[] args) throws InterruptedException {
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
        BetCopyUtil betCopyUtil = new BetCopyUtil();
        // 登录信息
        betCopyUtil.login(driver);
        // 点击新BBIT赛事信息
        betCopyUtil.btnSend(driver);
        // 点击滚球按钮
        driver.findElement(By.xpath("//*[@id=\"nav\"]/a[1]")).click();
        System.out.println("点击滚球按钮...");
        Thread.sleep(4000);
        // 获取新的ifram
        driver.switchTo().frame("bcsportsbookiframe");
        System.out.println("获取新的ifram...");
        Thread.sleep(4000);
        do {
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
                Thread.sleep(15000);
                continue;
            }
            System.out.println("正常操作开始.....");
            Thread.sleep(4000);
            // 点击刷新按钮，确保正常连接
            driver.findElement(By.xpath("//*[@id=\"asianView\"]/div/div[3]/div[1]/div[2]/button")).click();
            List<WebElement> table = driver.findElements(By.tagName("table"));
            log.info("table头部数据 -> {}", table.size());
            // 循环判断最近的篮球赛事
            for (WebElement e : table) {
                Thread.sleep(2000);
                // 判断不为空的篮球赛事
                List<WebElement> trs = e.findElements(By.tagName("tr"));
                if (trs.size() >= 2) {
                    List<WebElement> td1 = trs.get(1).findElements(By.tagName("td"));
                    List<WebElement> td2 = trs.get(2).findElements(By.tagName("td"));
                    if (td1.size() > 2 && td2.size() > 2) {
                        // 比赛第几节/时间
                        String[] split = td1.get(0).getText().replaceAll("\r|\n", "P").split("P");
                        // 第几节
                        String djj = split[0];
                        // 比赛剩余时间
                        String sysj = split[1];
                        // 比赛队伍名称
                        String zd = td1.get(1).getText();
                        zd = zd.replaceAll("\r|\n", "P").split("P")[0];
                        String kd = td2.get(0).getText();
                        kd = kd.replaceAll("\r|\n", "P").split("P")[0];
                        if (StrUtil.isNotBlank(kd) && StrUtil.isNotBlank(zd) && !djj.contains("即将开赛")) {
                            // 点击下注
                            // String text = td2.get(5).getText();
                            // System.out.println("我是获取的数据：" + text);
                            td2.get(5).findElement(By.tagName("p")).click();
                            // 判断是否支持下注，或者获取比分
                            int check = checkBet(zd, djj, sysj);
                            if (check == 0) {
                                // 无需下注
                                System.out.println("&&&&&&&&&&&&&&&&");
                                System.out.println("无需下注....");
                                System.out.println("&&&&&&&&&&&&&&&&");
                                continue;
                            } else if (check == 1) {
                                // 获取比分，判断是否需要下注
                                if (checkScore(driver, zd, djj) == 1) {
                                    // 红单不需要下注
                                    System.out.println("&&&&&&&&&&&&&&&&");
                                    System.out.println("红单，无需下注....");
                                    System.out.println("&&&&&&&&&&&&&&&&");
                                    driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[1]/ul/li[2]/div/p")).click();
                                    break;
                                }
                            } else if (check == 2) {
                                // 支持下注
                                System.out.println("=================");
                                System.out.println("比赛进行中:" + djj);
                                System.out.println(zd + " VS " + kd);
                                Thread.sleep(4000);
                                System.out.println("下注完成....");
                                sendBet(driver,zd,djj);
                            }
                            System.out.println("正在点击退出......");
                            Thread.sleep(4000);
                        }
                    }
                }
            }
            System.out.println("重新获取最新table.....");
        } while (true);
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
            if (FIRST.equals(djj) && Integer.parseInt(sysj.split(";")[0]) >= 4) {
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
            } else {
                betCacheSpec.setIsRed(2);
                betCacheSpec.setMagnification(betCacheSpec.getMagnification() + 1);
                check = 2;
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
            } else {
                betCacheSpec.setIsRed(2);
                betCacheSpec.setMagnification(betCacheSpec.getMagnification() + 1);
                check = 2;
            }
        } else if (THIRD.equals(djj)) {
            // 主队第三节比分
            String zdtext3 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[5]/div/ng-include/live-game-scores/div[1]/div[1]/div/div/div/div/ng-include/div/div[1]/ul[2]/li[4]/span")).getText();
            // 客队第三节比分
            String kdtext3 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[5]/div/ng-include/live-game-scores/div[1]/div[1]/div/div/div/div/ng-include/div/div[1]/ul[3]/li[4]/span")).getText();
            System.out.println("我是获取比赛分数：" + zdtext3 + " : " + kdtext3);
            if (Integer.parseInt(zdtext3) % Integer.parseInt(kdtext3) == 1) {
                betCacheSpec.setIsRed(1);
                check = 1;
            } else {
                betCacheSpec.setIsRed(2);
                betCacheSpec.setMagnification(betCacheSpec.getMagnification() + 1);
                check = 2;
            }
        }
        // 根据是否红单，是否清楚缓存
        if (betCacheSpec.getIsRed() == 1) {
            fifoCache.remove(betCacheSpec.getHomeTeam());
        } else {
            fifoCache.put(betCacheSpec.getHomeTeam(), JSON.toJSONString(betCacheSpec));
        }
        return check;
    }

    /**
     * 描述 ： 下注
     *
     * @param driver 控制浏览器
     * @param zd     主队名称【犹他爵士】
     * @param djj    第几节
     * @return
     */
    public static void sendBet(WebDriver driver, String zd, String djj) {
        String cache = fifoCache.get(zd);
        BetCacheSpec betCacheSpec = new BetCacheSpec();
        if (StrUtil.isBlank(cache)) {
            betCacheSpec.setHomeTeam(zd);
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
            // 购买第一场比赛
            betCacheSpec.setIsRed(0);
            betCacheSpec.setNumber(1);
            fifoCache.put(zd, JSON.toJSONString(betCacheSpec));
            System.out.println("第一节比赛已购买,下注金额[" + bl[betCacheSpec.getMagnification()] + "]....");
        } else if (betCacheSpec.getNumber() == 1) {
            // 购买第二场比赛
            betCacheSpec.setIsRed(0);
            betCacheSpec.setNumber(2);
            fifoCache.put(zd, JSON.toJSONString(betCacheSpec));
            System.out.println("第二节比赛已购买,下注金额[" + bl[betCacheSpec.getMagnification()] + "]....");
        } else if (betCacheSpec.getNumber() == 2) {
            // 购买第三场比赛
            betCacheSpec.setIsRed(0);
            betCacheSpec.setNumber(3);
            fifoCache.put(zd, JSON.toJSONString(betCacheSpec));
            System.out.println("第三节比赛已购买,下注金额[" + bl[betCacheSpec.getMagnification()] + "]....");
        }
    }
}
