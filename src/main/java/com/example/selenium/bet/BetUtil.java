package com.example.selenium.bet;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
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
            // 倍率数据
            List<String> rl = new ArrayList<>();
            rl.add("1");
            rl.add("2");
            rl.add("4");
            rl.add("8");
            rl.add("17");
            rl.add("33");
            rl.add("68");
            rl.add("132");
            rl.add("274");
            rl.add("555");
            rl.add("1110");
            rl.add("2258");
            rl.add("2003");
            rl.add("4000");
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
                sendBet(wws, driver, JB, qs, rl, "sendBetWw", "sendBetAmountWw", "sendBetNumberWw", 1);
                //sendBetDob(wws, driver, JB, "WW_BD", "WW_CS", "WW", 1);
                driver.switchTo().window(JB).navigate().refresh();
                Thread.sleep(2000);
                // 下注千位
                String qws = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/ul/li[2]")).getText();
                // log.info("千位数据 -> {}", qws);
                sendBet(qws, driver, JB, qs, rl, "sendBetQw", "sendBetAmountQw", "sendBetNumberQw", 2);
                //sendBetDob(qws, driver, JB, "QW_BD", "QW_CS", "QW", 2);
                driver.switchTo().window(JB).navigate().refresh();
                Thread.sleep(2000);
                // 下注百位
                String bws = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/ul/li[3]")).getText();
                //  log.info("百位数据 -> {}", bws);
                sendBet(bws, driver, JB, qs, rl, "sendBetBw", "sendBetAmountBw", "sendBetNumberBw", 3);
                //sendBetDob(bws, driver, JB, "BW_BD", "BW_CS", "BW", 3);
                driver.switchTo().window(JB).navigate().refresh();
                Thread.sleep(2000);
                // 下注十位
                String sws = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/ul/li[4]")).getText();
                // log.info("十位数据 -> {}", sws);
                sendBet(sws, driver, JB, qs, rl, "sendBetSw", "sendBetAmountSw", "sendBetNumberSw", 4);
                //sendBetDob(sws, driver, JB, "SW_BD", "SW_CS", "SW", 4);
                driver.switchTo().window(JB).navigate().refresh();
                Thread.sleep(2000);
                // 下注个位
                String gws = driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/div/div[1]/div[3]/ul/li[5]")).getText();
                // log.info("个位数据 -> {}", gws);
                sendBet(gws, driver, JB, qs, rl, "sendBetGw", "sendBetAmountGw", "sendBetNumberGw", 5);
                //sendBetDob(gws, driver, JB, "GW_BD", "GW_CS", "GW", 5);
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
            if (fifoCache.get(sendBetKey).contains(ws)) {
                // 上期比赛结果为单
                log.info("比赛单【fifoCache.get(sendBetKey).contains("+ws+")】 -> " + "单".equals(fifoCache.get(sendBetKey)));
                log.info("ws -> :" + ws + " -> " + fifoCache.get(sendBetKey));
                flag = true;
            } else {
                // 黑了
                log.info("比赛黑了");
                log.info("ww -> :" + ws + " -> " + fifoCache.get(sendBetKey));
                flag = false;
                if (null != fifoCache.get(sendBetNumberKey)) {
                    if (Integer.parseInt(fifoCache.get(sendBetNumberKey)) == 12) {
                        log.info("[ " + sendBetKey + " ]比赛黑12场，从第0场开始购开始购买");
                        Thread.sleep(2000);
                        DingUtil d = new DingUtil();
                        d.sendMassage("[ " + sendBetKey + " ]比赛黑10场，重新开始下");
                        String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
                        d.sendMassage("黑12场了，航行者,前来汇报 : " + text);
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
                fifoCache.put(sendBetKey, "1,3,5,7,9");
                // log.info("期数 ：" + qs + ",购买：单");
            } else {
                // 双
                driver.switchTo().window(JB).findElement(By.xpath("/html/body/div[1]/div[2]/div[5]/div[2]/div[3]/div/div[" + div + "]/ul/li[3]/dl/dd[5]")).click();
                fifoCache.put(sendBetKey, "2,4,6,8,0");
                // log.info("期数 ：" + qs + ",购买：双");
            }
            log.info("==================");
            log.info("获取缓存值 -> {}", fifoCache.get(sendBetKey));
            log.info("==================");
            if (flag) {
                // 填写金额
                fifoCache.put(sendBetAmountKey, "1");
                fifoCache.put(sendBetNumberKey, "0");
                driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"addbs\"]")).click();
            } else {
                if (fifoCache.get(sendBetNumberKey) == null) {
                    fifoCache.put(sendBetNumberKey, "0");
                    fifoCache.put(sendBetAmountKey, "1");
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
                    if (addbs > 30 && addbs < 40) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        Thread.sleep(500);
                        element.sendKeys("3");
                        addbs = addbs - 31;
                    } else if (addbs > 60 && addbs < 80) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        Thread.sleep(500);
                        element.sendKeys("6");
                        addbs = addbs - 61;
                    } else if (addbs > 100 && addbs < 140) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        Thread.sleep(500);
                        element.sendKeys("13");
                        addbs = addbs - 131;
                    } else if (addbs > 250 && addbs < 300) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        Thread.sleep(500);
                        element.sendKeys("27");
                        addbs = addbs - 271;
                    } else if (addbs > 500 && addbs < 600) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        Thread.sleep(500);
                        element.sendKeys("55");
                        addbs = addbs - 551;
                    } else if (addbs > 1000 && addbs < 2000) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        Thread.sleep(500);
                        element.sendKeys("110");
                        addbs = addbs - 1101;
                    } else if (addbs > 2000 && addbs < 2050) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        Thread.sleep(500);
                        element.sendKeys("200");
                        addbs = addbs - 2001;
                    } else if (addbs > 3000 && addbs < 4050) {
                        WebElement element = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"multiple\"]"));
                        Thread.sleep(500);
                        element.sendKeys("399");
                        addbs = addbs - 3991;
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
            Thread.sleep(200);
            // 判断比赛是否走8位
            if (null == fifoCache.get(sendBetNumberKey)) {
                fifoCache.put(sendBetNumberKey, "0");
            }
            if(null == fifoCache.get(sendBetKey)){
                fifoCache.put(sendBetKey, "红");
            }
            int numberKey = Integer.parseInt(fifoCache.get(sendBetNumberKey));
            log.info("=================== ["+div+"] 开始 ===================");
            log.info("我是购买次数[sendBetNumberKey] -> {}", numberKey);
            log.info(fifoCache.get(sendBetKey) + ".contains("+ws+") -> {}", fifoCache.get(sendBetKey).contains(ws));
            String str = "";
            if (fifoCache.get(sendBetKey).contains(ws)) {
                log.info("该比赛黑单，走加倍逻辑0000000000");
                str = sendEightDigitsErr(driver,JB,sendBetKey,sendBetNumberKey,div);
            } else {
                log.info("该比赛红单，走初倍逻辑1111111111");
                str = sendEightDigitsOk(driver,JB,sendBetKey,sendBetNumberKey,div);
            }
            // 点击下注使用
            sendBetOk(sendBetNumberKey,driver,JB,sendBetKey,div,str);
            log.info("=================== ["+div+"] 结束 ===================");
            System.out.println("");
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
    public void sendBetOk(String sendBetNumberKey,WebDriver driver,String JB,String sendBetKey,int div,String str) throws InterruptedException {
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
        log.info("我是取出来的倍率次数和倍数[numberKey],[amount] -> {},{}",numberKey,amount);
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
        }else{
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
        if(numberKey != 3){
            Thread.sleep(2000);
            String[] split = fifoCache.get(sendBetKey).split(",");
            log.info("我是新增购买数据 - > {},{}",JSON.toJSONString(split),split[0]);
            // 点击
            driver.switchTo().window(JB).findElement(By.xpath(str.split(",")[0])).click();
            fifoCache.put(sendBetKey,split[1]);
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
            }else{
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
     * @param ws
     * @param sendBetKey
     * @return
     */
    public Boolean checkOddAndEven(String ws,String sendBetWs){
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
     * @param driver
     * @param JB
     * @param numberKey
     * @param sendBetNumberKey
     * @param div
     */
    public String sendEightDigitsOk(WebDriver driver,String JB,String sendBetKey,String sendBetNumberKey,int div){
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
     * @param driver
     * @param JB
     * @param numberKey
     * @param sendBetNumberKey
     * @param div
     */
    public String sendEightDigitsErr(WebDriver driver,String JB,String sendBetKey,String sendBetNumberKey,int div){
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
        if(numberKey == 2){
            String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
            DingUtil dingUtil = new DingUtil();
            dingUtil.sendMassage("黑3场了，航行者,前来汇报 : " + text);
            numberKey = 0;
        }else{
            numberKey = numberKey + 1;
        }
        fifoCache.put(sendBetNumberKey, numberKey + "");
        fifoCache.put(sendBetKey, jzws);
        return buf.toString();
    }

    /**
     * 下注走单双
     * @param driver
     * @param numberKey
     * @param div
     * @param sendBetKey
     * @param sendBetNumberKey
     * @param JB
     */
    public void sendOddAndEven(WebDriver driver,int div,String sendBetKey,String sendBetNumberKey,String JB){
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
        if(numberKey == 6){
            DingUtil d = new DingUtil();
            d.sendMassage("[ " + sendBetKey + " ]比赛黑6场，重新开始下");
            String text = driver.switchTo().window(JB).findElement(By.xpath("//*[@id=\"content\"]/div[4]/div[1]/div[4]/span[2]")).getText();
            d.sendMassage("我是航行者,前来汇报 : " + text);
            numberKey = 0;
        }else{
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
