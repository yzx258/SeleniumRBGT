package com.example.selenium.bet;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.example.selenium.dto.InstructionDTO;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author yucw
 * @date 2020/8/14 14:14
 * 描述：188下注平台
 */
@Slf4j
public class BetUtil {

    private static String URL_ERROR = "http://47.106.143.218:8081/instruction/update/error/";
    private static String URL_SUCCESS = "http://47.106.143.218:8081/instruction/update/success/";
    private static Cache<String, String> fifoCache = CacheUtil.newFIFOCache(10);


    /**
     * @throws InterruptedException
     */
    public void sz() {
        // 比赛进行中
        if ("ON".equals(fifoCache.get("statusSend"))) {
            System.out.println("比赛进行中 -> " + fifoCache.get("statusSend"));
            return;
        }
        fifoCache.put("statusSend", "ON");
        // chromeDriver服务地址，需要手动下载
        // 测试环境：[D:\00002YX\chromedriver.exe]地址需要自己给
        // String chromeDriverUrl = "C:\\software\\chrome\\chromedriver.exe";
        // 正式环境：System.getProperty("user.dir")+"\\src\\main\\resources\\chromedriver.exe";
        String chromeDriverUrl = System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe";
        System.out.println(chromeDriverUrl);
        System.setProperty("webdriver.chrome.driver", chromeDriverUrl);
        // 自己本地最新的charm版本，需要添加启动参数
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        WebDriver driver = new ChromeDriver();
        try {
            // 登录操作
            driver.get("https://www.52365v.com");
            driver.manage().window().maximize();
            // 获取用户输入框
            WebElement elementZh = driver.findElement(By.xpath("//*[@id=\"login\"]/div[1]/div[1]/input"));
            // 清空输入框
            elementZh.clear();
            // 获取用户输入框
            WebElement elementMm = driver.findElement(By.xpath("//*[@id=\"login\"]/div[1]/div[2]/input"));
            // 清空输入框
            elementMm.clear();

            elementZh.sendKeys("dhxm2376");
            elementMm.sendKeys("dhxm2376");
            //点击确定按钮
            driver.findElement(By.xpath("//*[@id=\"login\"]/div[1]/div[3]/button")).click();
            Thread.sleep(1000);
            // 点击体育赛制界面
            driver.findElement(By.xpath("//*[@id=\"index\"]/div[2]/div/div/ul/li/a/div/div")).click();
            Thread.sleep(2000);
            // 点击彩票界面
            driver.findElement(By.xpath("//*[@id=\"header-wrap\"]/div[1]/div[2]/ul/li[9]/a")).click();
            Thread.sleep(2000);
            //滚动到最底端
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,document.body.scrollHeight)");
            Thread.sleep(2000);
            // 点击世博彩票
            driver.findElement(By.xpath("//*[@id=\"lottery-wrap\"]/div[2]/div/div[20]")).click();
            System.out.println("第二次点击=================================");
            Thread.sleep(3000);
            // 点击世博彩票
            driver.findElement(By.xpath("//*[@id=\"lottery-wrap\"]/div[2]/div/div[20]")).click();

            Thread.sleep(2000);
            // 获取新窗口句柄，才能操作新弹出的窗口
            Set<String> allWindowsId = driver.getWindowHandles();
            List<String> list = new ArrayList<>(allWindowsId);
            String JB = "";
            for (int i = 0; i < list.size(); i++) {
                System.out.println("我是句柄ID【windowId】：" + list.get(i));
                System.out.println("i == list.size() : " + (i == list.size()));
                if (i == 1) {
                    JB = list.get(i);
//                System.out.println("点击了全屏");
//                Thread.sleep(3000);
//                driver.switchTo().window(list.get(i)).manage().window().maximize();
                }
            }
            // 全屏操作
            System.out.println("我是句柄ID【JB】：" + JB);
            System.out.println("点击了全屏");
            Thread.sleep(1000);
            driver.switchTo().window(JB).manage().window().maximize();
            List<String> rl = new ArrayList<>();
            rl.add("1");
            rl.add("3");
            rl.add("7");
            rl.add("15");
            rl.add("31");
            rl.add("60");
            rl.add("122");
            rl.add("250");
            rl.add("509");
            rl.add("1034");
            // 循环调用即可
            do {
                Thread.sleep(2000);
                String str = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/div/span[1]")).getText();
                System.out.println("str -> " + str);
                String ww = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"num4\"]")).getText();
                System.out.println("万位是否符合:" + ww);
                // 判断是否进行中
                if (str.equals("准备开奖") || StrUtil.isEmpty(ww)) {
                    System.out.println("正在开奖，跳过");
                    continue;
                }
                String qs = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"kjdates_mc_txt\"]")).getText();
                System.out.println("期数:" + qs);
                if (null == fifoCache.get("QS")) {
                    fifoCache.put("QS", qs);
                }
                if (qs.equals(fifoCache.get("QS"))) {
                    System.out.println("期数相同，跳过买卖 -> " + qs);
                    continue;
                }
                fifoCache.put("QS", qs);
                // 等待结果开启
                Thread.sleep(6000);
                System.out.println("====================================================================");
                // 红单标识
                Boolean flag = true;
                ww = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"num4\"]")).getText();
                // 判断是否红
                if (Integer.parseInt(ww) % 2 == 1 && "单".equals(fifoCache.get("sendBet"))) {
                    // 上期比赛结果为单
                    System.out.println("比赛单【单.equals(fifoCache.get(sendBet))】 -> "+"单".equals(fifoCache.get("sendBet")));
                    System.out.println("购买比赛结果[fifoCache.get(\"sendBet\")]:" + fifoCache.get("sendBet"));
                    System.out.println("ww -> :" + ww + " -> " + fifoCache.get("sendBet"));
                    flag = true;
                } else if (Integer.parseInt(ww) % 2 == 0 && "双".equals(fifoCache.get("sendBet"))) {
                    // 上期比赛结果为双
                    System.out.println("比赛双【单.equals(fifoCache.get(sendBet))】 -> "+"单".equals(fifoCache.get("sendBet")));
                    System.out.println("购买比赛结果[fifoCache.get(sendBet)]:" + fifoCache.get("sendBet"));
                    System.out.println("ww -> :" + ww + " -> " + fifoCache.get("sendBet"));
                    flag = true;
                } else {
                    // 黑了
                    System.out.println("比赛黑了");
                    System.out.println("ww -> :" + ww + " -> " + fifoCache.get("sendBet"));
                    flag = false;
                }
                // 点击选择分
                Thread.sleep(1000);
                driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[4]/div[2]/div/div[2]/div[2]/div[3]/select/option[3]")).click();
                // 点击单
                Thread.sleep(1000);
                // 随机数
                if (random() == 1) {
                    // 单
                    driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[1]/ul/li[3]/dl/dd[4]")).click();
                    fifoCache.put("sendBet", "单");
                    System.out.println("期数 ："+qs+",购买：单");
                } else {
                    // 双
                    driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[1]/ul/li[3]/dl/dd[5]")).click();
                    fifoCache.put("sendBet", "双");
                    System.out.println("期数 ："+qs+",购买：双");
                }
                if (flag) {
                    // 填写金额
                    fifoCache.put("sendBetAmount", "1");
                    fifoCache.put("sendBetNumber", "0");
                } else {
                    if (fifoCache.get("sendBetNumber") == null) {
                        fifoCache.put("sendBetNumber", "0");
                        fifoCache.put("sendBetAmount", "1");
                    }else{
                        int sendBetNumber = Integer.parseInt(fifoCache.get("sendBetNumber"))+1;
                        fifoCache.put("sendBetNumber", sendBetNumber + "");
                        String s = rl.get(sendBetNumber);
                        fifoCache.put("sendBetAmount", s);
                        // 获取输入款
                        int addbs = Integer.parseInt(s) - 1;
                        System.out.println("我是addbs参数：" + addbs);
                        for (int i = 0; i < addbs; i++) {
                            Thread.sleep(10);
                            driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"addbs\"]")).click();
                        }
                    }
                }
                System.out.println("====================================================================");
                // 确认下注
                Thread.sleep(2000);
                driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"sure\"]/input")).click();
                Thread.sleep(2000);
                driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"msgDiv\"]/div[3]/div[1]")).click();
                Thread.sleep(2000);
                driver.switchTo().window(JB).navigate().refresh();
            } while (true);

        } catch (InterruptedException e) {
            fifoCache.put("statusSend","OFF");
            fifoCache.remove("sendBetAmount");
            fifoCache.remove("sendBetNumber");
            fifoCache.remove("sendBet");
            driver.quit();
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
}
