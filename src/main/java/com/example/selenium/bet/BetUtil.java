package com.example.selenium.bet;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.example.selenium.dto.InstructionDTO;
import com.example.selenium.util.DingUtil;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

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
public class BetUtil {

    private static String URL_ERROR = "http://47.106.143.218:8081/instruction/update/error/";
    private static String URL_SUCCESS = "http://47.106.143.218:8081/instruction/update/success/";
    private static Cache<String, String> fifoCache = CacheUtil.newFIFOCache(100);
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
            elementMm.sendKeys("ycw15659512376");
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
            Thread.sleep(3000);
            // 点击世博彩票
            driver.findElement(By.xpath("//*[@id=\"lottery-wrap\"]/div[2]/div/div[20]")).click();

            Thread.sleep(2000);
            // 获取新窗口句柄，才能操作新弹出的窗口
            Set<String> allWindowsId = driver.getWindowHandles();
            List<String> list = new ArrayList<>(allWindowsId);
            String JB = "";
            for (int i = 0; i < list.size(); i++) {
                if (i == 1) {
                    JB = list.get(i);
                }
            }
            // 全屏操作
            Thread.sleep(1000);
            driver.switchTo().window(JB).manage().window().maximize();
            List<String> rl = new ArrayList<>();
            rl.add("2");
            rl.add("6");
            rl.add("14");
            rl.add("30");
            rl.add("62");
            rl.add("120");
            rl.add("244");
            rl.add("502");
            rl.add("1020");
            rl.add("2072");
            // 循环调用即可
            do {
                // 判断当前时间是否在这个事件段
                Thread.sleep(500);
                if (checkTime() && null == fifoCache.get("sendMassage")) {
                    String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
                    DingUtil dingUtil = new DingUtil();
                    dingUtil.sendMassage("我是航行者,前来汇报 : " + text);
                    fifoCache.put("sendMassage", "OK", DateUnit.SECOND.getMillis() * 70);
                }
                String str = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/div/span[1]")).getText();
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
                log.info("************* 开始下注期数 : " + qs + " *************");
                log.info("*******************************");
                Thread.sleep(200);
                driver.switchTo().window(JB).navigate().refresh();
                Thread.sleep(2000);
                // 下注万位
                String wws = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/ul/li[1]")).getText();
                // log.info("万位数据 -> {}", wws);
                // sendBet(wws, driver, JB, qs, rl, "sendBetWw", "sendBetAmountWw", "sendBetNumberWw", 1);
                sendBetDob(wws, driver, JB, "WW_BD", "WW_CS", "WW", 1);
                Thread.sleep(1000);
                driver.switchTo().window(JB).navigate().refresh();
                Thread.sleep(2000);
                // 下注千位
                String qws = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/ul/li[2]")).getText();
                // log.info("千位数据 -> {}", qws);
                // sendBet(qws, driver, JB, qs, rl, "sendBetQw", "sendBetAmountQw", "sendBetNumberQw", 2);
                sendBetDob(qws, driver, JB, "QW_BD", "QW_CS", "QW", 2);
                Thread.sleep(1000);
                driver.switchTo().window(JB).navigate().refresh();
                Thread.sleep(2000);
                // 下注百位
                String bws = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/ul/li[3]")).getText();
                //  log.info("百位数据 -> {}", bws);
                // sendBet(bws, driver, JB, qs, rl, "sendBetBw", "sendBetAmountBw", "sendBetNumberBw", 3);
                sendBetDob(bws, driver, JB, "BW_BD", "BW_CS", "BW", 3);
                Thread.sleep(1000);
                driver.switchTo().window(JB).navigate().refresh();
                Thread.sleep(2000);
                // 下注十位
                String sws = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/ul/li[4]")).getText();
                // log.info("十位数据 -> {}", sws);
                //sendBet(sws, driver, JB, qs, rl, "sendBetSw", "sendBetAmountSw", "sendBetNumberSw", 4);
                sendBetDob(sws, driver, JB, "SW_BD", "SW_CS", "SW", 4);
                Thread.sleep(1000);
                driver.switchTo().window(JB).navigate().refresh();
                Thread.sleep(2000);
                // 下注个位
                String gws = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/ul/li[5]")).getText();
                // log.info("个位数据 -> {}", gws);
                // sendBet(gws, driver, JB, qs, rl, "sendBetGw", "sendBetAmountGw", "sendBetNumberGw", 5);
                sendBetDob(gws, driver, JB, "GW_BD", "GW_CS", "GW", 5);
                driver.switchTo().window(JB).navigate().refresh();
            } while (true);
        } catch (Exception e) {
            System.out.println("====================== 报错了 ======================");
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
            log.info("====================================================================");
            log.info("***********");
            log.info("下注场次[" + sendBetKey + "] -> {}", fifoCache.get(sendBetKey));
            log.info("***********");
            // 判断是否红[fifoCache.get("sendBet")]
            if (Integer.parseInt(ws) % 2 == 1 && "单".equals(fifoCache.get(sendBetKey))) {
                // 上期比赛结果为单
                log.info("比赛单【单.equals(fifoCache.get(" + sendBetKey + "))】 -> " + "单".equals(fifoCache.get(sendBetKey)));
                log.info("ws -> :" + ws + " -> " + fifoCache.get(sendBetKey));
                flag = true;
            } else if (Integer.parseInt(ws) % 2 == 0 && "双".equals(fifoCache.get(sendBetKey))) {
                // 上期比赛结果为双
                log.info("比赛双【双.equals(fifoCache.get(\"+sendBetKey+\"))】 -> " + "双".equals(fifoCache.get(sendBetKey)));
                log.info("ws -> :" + ws + " -> " + fifoCache.get(sendBetKey));
                flag = true;
            } else {
                // 黑了
                log.info("比赛黑了");
                log.info("ww -> :" + ws + " -> " + fifoCache.get(sendBetKey));
                flag = false;
                if (null != fifoCache.get(sendBetNumberKey)) {
                    if (Integer.parseInt(fifoCache.get(sendBetNumberKey)) == 10) {
                        log.info("[ " + sendBetKey + " ]比赛黑10场，从第9场开始购开始购买");
                        Thread.sleep(2000);
                        DingUtil d = new DingUtil();
                        d.sendMassage("[ " + sendBetKey + " ]比赛黑10场，重新开始下");
                        fifoCache.remove(sendBetNumberKey);
                    }
                }
            }
            // 点击选择分
            Thread.sleep(500);
            driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[4]/div[2]/div/div[2]/div[2]/div[3]/select/option[3]")).click();
            // 点击单
            Thread.sleep(500);
            if (random() == 1) {
                // 单
                driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[3]/dl/dd[4]")).click();
                fifoCache.put(sendBetKey, "单");
                // log.info("期数 ：" + qs + ",购买：单");
            } else {
                // 双
                driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[3]/dl/dd[5]")).click();
                fifoCache.put(sendBetKey, "双");
                // log.info("期数 ：" + qs + ",购买：双");
            }
            log.info("==================");
            log.info("获取缓存值 -> {}", fifoCache.get(sendBetKey));
            log.info("==================");
            if (flag) {
                // 填写金额
                fifoCache.put(sendBetAmountKey, "2");
                fifoCache.put(sendBetNumberKey, "0");
                driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"addbs\"]")).click();
            } else {
                if (fifoCache.get(sendBetNumberKey) == null) {
                    fifoCache.put(sendBetNumberKey, "0");
                    fifoCache.put(sendBetAmountKey, "2");
                    driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"addbs\"]")).click();
                } else {
                    int sendBetNumber = Integer.parseInt(fifoCache.get(sendBetNumberKey)) + 1;
                    fifoCache.put(sendBetNumberKey, sendBetNumber + "");
                    String s = rl.get(sendBetNumber);
                    fifoCache.put(sendBetAmountKey, s);
                    // 获取输入款
                    int addbs = Integer.parseInt(s);
                    log.info("&&&&&&&&&&我是开始addbs参数&&&&&&&&&& -> {}", addbs);
                    // 根据比例扣减循环数
                    if (addbs > 100 && addbs < 200) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        Thread.sleep(500);
                        element.sendKeys("10");
                        addbs = addbs - 101;
                    } else if (addbs > 200 && addbs < 300) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        Thread.sleep(500);
                        element.sendKeys("20");
                        addbs = addbs - 201;
                    } else if (addbs > 500 && addbs < 600) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        Thread.sleep(500);
                        element.sendKeys("50");
                        addbs = addbs - 501;
                    } else if (addbs > 1000 && addbs < 1200) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        Thread.sleep(500);
                        element.sendKeys("100");
                        addbs = addbs - 1001;
                    } else if (addbs > 2000 && addbs < 2100) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        Thread.sleep(500);
                        element.sendKeys("200");
                        addbs = addbs - 2001;
                    } else {
                        addbs = addbs - 1;
                    }
                    log.info("&&&&&&&&&&我是最终addbs参数&&&&&&&&&& -> {}", addbs);
                    for (int i = 0; i < addbs; i++) {
                        driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"addbs\"]")).click();
                    }
                }
            }
            // 确认下注
            Thread.sleep(100);
            driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"sure\"]/input")).click();
            Thread.sleep(100);
            driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"msgDiv\"]/div[3]/div[1]")).click();
            Thread.sleep(200);
            log.info("====================================================================");
        } catch (Exception e) {
            log.info("====================== 下注报错了，跳过此次购买[购买单位：" + sendBetKey + "] ======================");
        }
    }

    /**
     *下注指令
     * @param ws                位数：万位，千位，百位，十位，个位
     * @param driver            浏览器
     * @param JB                句柄ID
     * @param sendBetKey        下注单双key
     * @param sendBetNumberKey  下注次数
     * @param sendBetWs         下注单双key
     * @param div               位数
     */
    public void sendBetDob(String ws, WebDriver driver, String JB, String sendBetKey, String sendBetNumberKey, String sendBetWs, int div) {
        try {
            // 点击选择分
            driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[4]/div[2]/div/div[2]/div[2]/div[3]/select/option[3]")).click();
            Thread.sleep(1000);
            // 判断比赛是否走8位
            if (null == fifoCache.get(sendBetNumberKey)) {
                fifoCache.put(sendBetNumberKey, "0");
            }
            if(null == fifoCache.get(sendBetKey)){
                fifoCache.put(sendBetKey, "0,9");
            }
            int numberKey = Integer.parseInt(fifoCache.get(sendBetNumberKey));
            log.info("=================== ["+div+"] 开始 ===================");
            log.info("我是购买次数[sendBetNumberKey] -> {}", numberKey);
            if (numberKey <= 2) {
                // 判断WS是否红单
                log.info(fifoCache.get(sendBetKey) + ".contains("+ws+") -> {}", fifoCache.get(sendBetKey).contains(ws));
                if (fifoCache.get(sendBetKey).contains(ws)) {
                    log.info("该比赛黑单，走加倍逻辑0000000000");
                    if (numberKey == 2) {
                        // 走单双下注逻辑
                        sendOddAndEven(driver,div,sendBetWs,sendBetNumberKey,JB);
                    } else {
                        sendEightDigitsErr(driver,JB,sendBetKey,sendBetNumberKey,div);
                    }
                } else {
                    log.info("该比赛红单，走初倍逻辑1111111111");
                    sendEightDigitsOk(driver,JB,sendBetKey,sendBetNumberKey,div);
                }
            } else {
                // 判断单双是否中
                if(checkOddAndEven(ws,sendBetKey)){
                    log.info("该比赛红单，走初倍逻辑2222222222");
                    sendEightDigitsOk(driver,JB,sendBetKey,sendBetNumberKey,div);
                }else{
                    log.info("该比赛黑单，走加倍逻辑3333333333");
                    if(Integer.parseInt(fifoCache.get(sendBetNumberKey)) == 6){
                        DingUtil d = new DingUtil();
                        d.sendMassage("[ " + sendBetKey + " ]比赛黑7场，重新开始下");
                        sendEightDigitsOk(driver,JB,sendBetKey,sendBetNumberKey,div);
                    }else{
                        // 点击单双
                        log.info("点击单双");
                        sendOddAndEven(driver,div,sendBetWs,sendBetNumberKey,JB);
                    }
                }
            }
            // 点击下注使用
            sendBetOk(sendBetNumberKey,driver,JB);
            log.info("=================== ["+div+"] 结束 ===================");
        } catch (Exception e) {
            log.info("====================== 下注报错了，跳过此次购买[购买单位：" + sendBetKey + "] ======================");
        }

    }

    /**
     * 下注完成
     * @param numberKey
     * @param driver
     * @param JB
     * @throws InterruptedException
     */
    public void sendBetOk(String sendBetNumberKey,WebDriver driver,String JB) throws InterruptedException {
        // 倍率
        List<String> rl = new ArrayList<>();
        rl.add("5");
        rl.add("30");
        rl.add("165");
        rl.add("330");
        rl.add("660");
        rl.add("1340");
        rl.add("2710");
        // 将倍率取出
        int numberKey = Integer.parseInt(fifoCache.get(sendBetNumberKey));
        String amount = rl.get(numberKey);
        log.info("---------------------------------");
        log.info("我是取出来的倍率次数和倍数[numberKey],[amount] -> {},{}",numberKey,amount);
        log.info("---------------------------------");
        int addbs = Integer.parseInt(amount);
        log.info("&&&&&&&&&&我是开始addbs参数&&&&&&&&&& -> {}", addbs);
        // 根据比例扣减循环数
        if (addbs > 100 && addbs < 200) {
            WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
            Thread.sleep(500);
            element.sendKeys("10");
            addbs = addbs - 101;
        } else if (addbs > 200 && addbs < 300) {
            WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
            Thread.sleep(500);
            element.sendKeys("20");
            addbs = addbs - 201;
        } else if (addbs > 500 && addbs < 600) {
            WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
            Thread.sleep(500);
            element.sendKeys("50");
            addbs = addbs - 501;
        } else if (addbs > 1000 && addbs < 1200) {
            WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
            Thread.sleep(500);
            element.sendKeys("100");
            addbs = addbs - 1001;
        } else if (addbs > 2000 && addbs < 2100) {
            WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
            Thread.sleep(500);
            element.sendKeys("270");
            addbs = addbs - 2701;
        } else {
            addbs = addbs - 1;
        }
        log.info("&&&&&&&&&&我是最终addbs参数&&&&&&&&&& -> {}", addbs);
        for (int i = 0; i < addbs; i++) {
            driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"addbs\"]")).click();
        }
        // 确认下注
        Thread.sleep(200);
        driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"sure\"]/input")).click();
        Thread.sleep(200);
        driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"msgDiv\"]/div[3]/div[1]")).click();
        Thread.sleep(200);
    }

    /**
     * 校验单双是否过关
     * @param ws
     * @param sendBetKey
     * @return
     */
    public Boolean checkOddAndEven(String ws,String sendBetKey){
        if (Integer.parseInt(ws) % 2 == 1 && "单".equals(fifoCache.get(sendBetKey))) {
            // 上期比赛结果为单
            log.info("比赛单【单.equals(fifoCache.get(" + sendBetKey + "))】 -> " + "单".equals(fifoCache.get(sendBetKey)));
            log.info("ws -> :" + ws + " -> " + fifoCache.get(sendBetKey));
            return true;
        } else if (Integer.parseInt(ws) % 2 == 0 && "双".equals(fifoCache.get(sendBetKey))) {
            // 上期比赛结果为双
            log.info("比赛双【双.equals(fifoCache.get(\"+sendBetKey+\"))】 -> " + "双".equals(fifoCache.get(sendBetKey)));
            log.info("ws -> :" + ws + " -> " + fifoCache.get(sendBetKey));
            return true;
        } else {
            // 黑了
            log.info("比赛黑了");
            log.info("ww -> :" + ws + " -> " + fifoCache.get(sendBetKey));
            return false;
        }
    }

    /**
     * 八位数，成功方法
     * @param driver
     * @param JB
     * @param numberKey
     * @param sendBetNumberKey
     * @param div
     */
    public void sendEightDigitsOk(WebDriver driver,String JB,String sendBetKey,String sendBetNumberKey,int div){
        String jzws = random1();
        String[] split = jzws.split(",");
        for (int i = 0; i < 10; i++) {
            if (Integer.parseInt(split[0]) == i || Integer.parseInt(split[1]) == i) {
                continue;
            }
            // 点击
            driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[2]/table/tbody/tr/td[" + (i + 1) + "]")).click();
        }
        int numberKey = 0;
        fifoCache.put(sendBetNumberKey, numberKey + "");
        fifoCache.put(sendBetKey, jzws);
    }

    /**
     * 八位数，失败方法
     * @param driver
     * @param JB
     * @param numberKey
     * @param sendBetNumberKey
     * @param div
     */
    public void sendEightDigitsErr(WebDriver driver,String JB,String sendBetKey,String sendBetNumberKey,int div){
        String jzws = random1();
        String[] split = jzws.split(",");
        for (int i = 0; i < 10; i++) {
            if (Integer.parseInt(split[0]) == i || Integer.parseInt(split[1]) == i) {
                continue;
            }
            // 点击
            driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[2]/table/tbody/tr/td[" + (i + 1) + "]")).click();
        }
        int numberKey = Integer.parseInt(fifoCache.get(sendBetNumberKey)) + 1;
        fifoCache.put(sendBetNumberKey, numberKey + "");
        fifoCache.put(sendBetKey, jzws);
    }

    /**
     * 下注走单双
     * @param driver
     * @param numberKey
     * @param div
     * @param sendBetWs
     * @param sendBetNumberKey
     * @param JB
     */
    public void sendOddAndEven(WebDriver driver,int div,String sendBetWs,String sendBetNumberKey,String JB){
        log.info("走单双判断逻辑");
        if (random() == 1) {
            // 单
            driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[3]/dl/dd[4]")).click();
            fifoCache.put(sendBetWs, "单");
            log.info("fifoCache.get(" + sendBetWs + ")购买： -> {}", fifoCache.get(sendBetWs));
        } else {
            // 双
            driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[3]/dl/dd[5]")).click();
            fifoCache.put(sendBetWs, "双");
            log.info("fifoCache.get(" + sendBetWs + ")购买： -> {}", fifoCache.get(sendBetWs));
        }
        int numberKey = Integer.parseInt(fifoCache.get(sendBetNumberKey)) + 1;
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
            System.out.println(ran1 + " == " + ran2 + " " + flag);
        } while (!flag);
        System.out.println(ran1 + " != " + ran2);
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
