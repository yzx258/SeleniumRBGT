package com.example.selenium.bet;

import com.alibaba.fastjson.JSON;
import com.example.selenium.bo.YaBoAccountInfoBO;
import com.example.selenium.bo.YaBoInfoBO;
import com.example.selenium.util.CacheMapUtil;
import com.example.selenium.util.DingUtil;
import com.example.selenium.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-12 15:46:39
 * @description: 描述
 */
@Slf4j
@Component
public class BetYaBoUtil {

    @Autowired
    private CacheMapUtil cacheMapUtil;


    /**
     * 启动下注
     */
    public void bet() {

        // 定义 - 参数
        DingUtil d = new DingUtil();
        // competitionName-联赛名称;homeTeamName-主队;awayTeamName-客队;whichSection-第几节;screenings-场次;betAmount-下注金额
        String competitionName = null;
        String homeTeamName = null;
        String awayTeamName = null;
        String whichSection = null;
        Integer screenings = 1;
        BigDecimal betAmount = null;
        BigDecimal originalAmount = null;
        BigDecimal balanceAmount = null;
        YaBoInfoBO yb = null;
        YaBoAccountInfoBO ybc = null;

        Object bet_tag = cacheMapUtil.getMap("THREAD_EXECUTION");
        if (null != bet_tag) {
            System.out.println("正在执行，不允许新线程操作");
            return;
        }

        // 线程开始执行
        cacheMapUtil.putMap("THREAD_EXECUTION", "1");

        // 初始化 - 自动化浏览器
        String chromeDriverUrl = System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverUrl);
        // 自己本地最新的charm版本，需要添加启动参数
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        WebDriver driver = new ChromeDriver();

        try {
            // 操作 - 打开浏览器
            driver.get("https://im.1f873fef.com/?timestamp=ptNpCKdPZJsJwiRMww3Js8sJ1pmSMb//YbUF8z21Ceo=&token=d25b95674bc9ab9be21cff10356f5714&LanguageCode=CHS");

            // 操作 - 屏幕最大化
            SleepUtil.sleepUtil(2000);
            driver.manage().window().maximize();


            // 操作 - 赛选篮球联赛
            driver.findElement(By.xpath("//*[@id=\"left_panel\"]/div[5]/div/div[2]/div/div[3]/div/label/span")).click();
            SleepUtil.sleepUtil(2000);

            if (cacheMapUtil.getMap("BET_A") != null) {
                YaBoInfoBO bet_a = JSON.parseObject(cacheMapUtil.getMap("BET_A"), YaBoInfoBO.class);
                System.out.println(JSON.toJSONString(bet_a));
                // 判断 - 是否结算
                for (int ob = 0; ob < 10000000; ob++) {
                    // 操作 - 账号刷新操作
                    clickRefreshAccount(driver);
                    SleepUtil.sleepUtil(5000);

                    // 获取 - 账户总金额
                    BigDecimal totalAmount = getTotalAmount(driver);
                    log.info("总金额 - totalAmount：" + totalAmount);
                    // 获取 - 待结算金额
                    BigDecimal pendingSettlementAmount = getPendingSettlementAmount(driver);
                    log.info("待结算金额 - pendingSettlementAmount：" + pendingSettlementAmount);
                    log.info("结算金额是否为0.00 - pendingSettlementAmount.compareTo(new BigDecimal(0.00)) == 0 - {}", pendingSettlementAmount.compareTo(new BigDecimal(0.00)) == 0);
                    // 操作 - 结算金额为0，则项目结算完成
                    if (pendingSettlementAmount.compareTo(new BigDecimal(0.00)) == 0) {
                        // 解析 - 赛事信息
                        BigDecimal cacheOriginalAmount = bet_a.getOriginalAmount();
                        log.info("下注前账户金额 - cacheOriginalAmount：" + cacheOriginalAmount);
                        if (cacheOriginalAmount.compareTo(totalAmount) > 0) {
                            // 小于 - 说明上一局失败
                            screenings = bet_a.getScreenings() + 1;
                            betAmount = getBetAmount(false, screenings);
                            d.sendMassage("[比赛结论：黑单" + "[比赛队伍：" + bet_a.getHomeTeamName() + " VS " + bet_a.getAwayTeamName() + "]" + "[比赛节数：" + bet_a.getWhichSection() + "]" + "[账户原金额：" + bet_a.getOriginalAmount() + "][下注金额：" + bet_a.getBetAmount() + "]" + "[账户剩余金额：" + bet_a.getBalanceAmount() + "]");
                        } else if (cacheOriginalAmount.compareTo(totalAmount) < 0) {
                            // 大于 - 说明上一局成功
                            betAmount = getBetAmount(true, bet_a.getScreenings());
                            d.sendMassage("[比赛结论：红单" + "[比赛队伍：" + bet_a.getHomeTeamName() + " VS " + bet_a.getAwayTeamName() + "]" + "[比赛节数：" + bet_a.getWhichSection() + "]" + "[账户原金额：" + bet_a.getOriginalAmount() + "][下注金额：" + bet_a.getBetAmount() + "]" + "[账户剩余金额：" + cacheOriginalAmount + "]");
                            cacheMapUtil.delMap("BET_A");
                        }

                        // 定义 - 全新的数据
                        originalAmount = totalAmount;
                        balanceAmount = originalAmount.subtract(betAmount);
                        break;
                    } else {
                        SleepUtil.sleepUtil(20000);
                    }
                }
            } else {
                // 初始化操作
                log.info("===================第一次初始化操作======================");
                // 操作 - 刷新账号信息
                clickRefreshAccount(driver);
                SleepUtil.sleepUtil(5000);

                // 获取 - 账户总金额
                BigDecimal totalAmount = getTotalAmount(driver);
                originalAmount = totalAmount;
                betAmount = new BigDecimal("5");
                balanceAmount = totalAmount.subtract(betAmount);
                screenings = 1;
            }


            log.info("========================================");
            log.info("账户金额 - originalAmount：{}；剩余金额 - balanceAmount：{}；下注金额 - betAmount：{}", originalAmount, balanceAmount, betAmount);
            log.info("========================================");

            // 获取 - 联赛名称
            List<WebElement> competition_header = driver.findElements(By.className("competition_header"));
            WebElement competition_header_team = competition_header.get(0).findElement(By.className("competition_header_team"));
            // 设定 - 联赛名称
            competitionName = competition_header_team.getText();

            // 操作 - 获取正在比赛的场次
            List<WebElement> event_listing = driver.findElements(By.className("event_listing"));
            SleepUtil.sleepUtil(2000);

            for (WebElement el : event_listing) {

                // 获取 - 每个场次比赛
                List<WebElement> row_live = el.findElements(By.className("row_live"));
                if (row_live != null && row_live.size() > 0) {
                    // 获取 - 该模块下的所有比赛

                    // 获取 - 场次赛事信息
                    List<WebElement> elements = row_live.get(0).findElement(By.className("team")).findElements(By.className("teamname_inner"));
                    // 设定 - 主队、客队
                    homeTeamName = elements.get(0).getText();
                    awayTeamName = elements.get(1).getText();


                    // 操作 - 点击该场比赛进入投注界面
                    SleepUtil.sleepUtil(2000);

                    // 获取 - 比分
                    WebElement score = row_live.get(0).findElement(By.className("team")).findElement(By.className("datetime")).findElement(By.tagName("div"));
                    WebElement node = row_live.get(0).findElement(By.className("team")).findElement(By.className("datetime")).findElement(By.tagName("span"));

                    // 操作 - 同时存在及进入
                    if (score.isEnabled() && node.isEnabled()) {
                        // 设定 - 节数
                        whichSection = changeWhichSection(node.getText());

                        // 获取 - 进入下注页面
                        WebElement aClick = row_live.get(0).findElement(By.className("additional")).findElement(By.tagName("a"));
                        SleepUtil.sleepUtil(3000);
                        aClick.click();

                        // 获取 - 当前页面
                        driver = driver.switchTo().window(driver.getWindowHandle());
                        SleepUtil.sleepUtil(5000);

                        // 操作 - 点击已节为操作
                        WebElement sevmenu_tab_content = driver.findElement(By.className("sevmenu_tab_content"));
                        List<WebElement> sevmenu_tab_inner = sevmenu_tab_content.findElements(By.className("sevmenu_tab_inner"));
                        for (WebElement sti : sevmenu_tab_inner) {
                            if (sti.getText().contains("节")) {
                                sti.findElement(By.className("tab_label")).click();
                                SleepUtil.sleepUtil(2000);
                            }
                        }

                        // 操作 - 收起全部倍率
                        WebElement sxClick = driver.findElement(By.xpath("//*[@id=\"sev_bet_filter\"]/div[2]/div[2]"));
                        sxClick.click();
                        SleepUtil.sleepUtil(2000);

                        // 获取 - 所有下注头节点
                        List<WebElement> betList = driver.findElement(By.className("group_wrap")).findElements(By.className("bet_type_wrap"));
                        for (WebElement bet : betList) {
                            WebElement betElement = bet.findElement(By.className("bet_type_row")).findElement(By.className("left"));
                            if (betElement.getText().contains("单/双")) {
                                // 操作 - 将单双点开
                                betElement.click();
                                SleepUtil.sleepUtil(3000);

                                // 获取 - 最新一节单双选项
                                List<WebElement> dsList = bet.findElement(By.className("ReactCollapse--collapse"))
                                        .findElement(By.className("ReactCollapse--content"))
                                        .findElement(By.className("bet_type_content"))
                                        .findElement(By.className("content_row"))
                                        .findElement(By.className("odds_col"))
                                        .findElements(By.className("col"));

                                // 操作 - 看倍率大小下注
                                WebElement bet_odd = dsList.get(0).findElement(By.className("odds_wrap")).findElement(By.className("odds"));
                                WebElement bet_double = dsList.get(1).findElement(By.className("odds_wrap")).findElement(By.className("odds"));
                                // 单/双倍率
                                BigDecimal bet_odd_magnification = new BigDecimal(bet_odd.getText());
                                BigDecimal bet_double_magnification = new BigDecimal(bet_double.getText());
                                if (bet_odd_magnification.compareTo(bet_double_magnification) > 0) {
                                    // 选择 - 单
                                    bet_odd.click();
                                } else if (bet_odd_magnification.compareTo(bet_double_magnification) < 0) {
                                    bet_double.click();
                                } else {
                                    bet_odd.click();
                                }
                                SleepUtil.sleepUtil(2000);

                                // 下注 - 单
                                WebElement bet_slip_wrap = driver.findElement(By.className("bet_slip_wrap"));
                                List<WebElement> bet_slip = bet_slip_wrap.findElements(By.className("bet_slip"));
                                for (WebElement bs : bet_slip) {
                                    // 获取 - 下注input
                                    WebElement input_wrap = bs.findElement(By.className("input_amount_wrap")).findElement(By.className("bet_amt_input_wrap"))
                                            .findElement(By.className("input_wrap"))
                                            .findElement(By.className("placebet_input"));

                                    // 操作 - 下注
                                    SleepUtil.sleepUtil(2000);
                                    input_wrap.sendKeys(betAmount + "");

                                    // 下注 - 按钮
                                    SleepUtil.sleepUtil(1000);
                                    WebElement btn_placebet_action = bet_slip_wrap.findElement(By.className("btn_placebet_action"));

                                    // 操作 - 按钮存在且允许点击
                                    if (btn_placebet_action.isEnabled() && btn_placebet_action.isDisplayed()) {
                                        // 点击 - 确认下注
                                        btn_placebet_action.click();

                                        // 下注 - 确认按钮
                                        SleepUtil.sleepUtil(10000);
                                        WebElement btn_placebet_inaction = bet_slip_wrap.findElement(By.className("btn_placebet_inaction"));
                                        if (btn_placebet_inaction.isEnabled() && btn_placebet_inaction.isDisplayed()) {
                                            btn_placebet_inaction.click();

                                            yb = new YaBoInfoBO();
                                            yb.setCompetitionName(competitionName);
                                            yb.setHomeTeamName(homeTeamName);
                                            yb.setAwayTeamName(awayTeamName);
                                            yb.setWhichSection(whichSection);
                                            yb.setScreenings(screenings);
                                            yb.setBetAmount(betAmount);
                                            yb.setBalanceAmount(balanceAmount);
                                            yb.setOriginalAmount(originalAmount);

                                            cacheMapUtil.putMap("BET_A", JSON.toJSONString(yb));
                                            System.out.println(JSON.toJSONString(cacheMapUtil.getMap("BET_A")));

                                            // 消息 - 推送钉钉服务
                                            d.sendMassage("[比赛队伍：" + homeTeamName + " VS " + awayTeamName + "]" + "[比赛节数：" + whichSection + "]" + "[账户原金额：" + originalAmount + "][下注金额：" + betAmount + "]" + "[账户剩余金额：" + balanceAmount + "]");
                                        }
                                    } else {
                                        // 操作 - 关闭失败下注
                                        WebElement close = bet_slip_wrap.findElement(By.className("icon-fi-status-close"));
                                        close.click();
                                    }
                                }
                            }
                            continue;
                        }
                    }
                    break;
                }
            }
            cacheMapUtil.delMap("THREAD_EXECUTION");
            // 结束 - 此次操作
            driver.close();
            driver.quit();
        } catch (Exception e) {
            cacheMapUtil.delMap("THREAD_EXECUTION");
            System.out.println(e);
            driver.close();
            driver.quit();
        }
    }

    /**
     * 转换节数
     *
     * @param whichSection
     * @return
     */
    public static String changeWhichSection(String whichSection) throws Exception {
        if (whichSection.contains("第1节")) {
            return "第1节";
        } else if (whichSection.contains("第2节")) {
            return "第2节";
        } else if (whichSection.contains("第3节")) {
            return "第3节";
        } else if (whichSection.contains("第4节")) {
            return "第4节";
        } else {
            throw new Exception("查找不到节数");
        }
    }

    /**
     * 根据场次，获取下注金额
     *
     * @param flag
     * @param screenings
     * @return
     */
    public static BigDecimal getBetAmount(Boolean flag, Integer screenings) {
        if (flag) {
            return new BigDecimal("5");
        } else {
            if (screenings == 1) {
                return new BigDecimal("5");
            } else if (screenings == 2) {
                return new BigDecimal("10");
            } else if (screenings == 3) {
                return new BigDecimal("20");
            } else if (screenings == 4) {
                return new BigDecimal("40");
            } else if (screenings == 5) {
                return new BigDecimal("80");
            } else if (screenings == 6) {
                return new BigDecimal("165");
            } else if (screenings == 7) {
                return new BigDecimal("335");
            } else if (screenings == 8) {
                return new BigDecimal("700");
            } else {
                return new BigDecimal("0");
            }
        }
    }


    /**
     * 操作 - 点击刷新账号信息
     *
     * @param driver
     * @return
     */
    public static void clickRefreshAccount(WebDriver driver) {

        // 获取 - 账号刷新按钮
        WebElement element = driver.findElement(By.className("leftmenu_account")).findElement(By.className("leftmenu_header"))
                .findElement(By.className("float-right"))
                .findElement(By.className("leftmenu_icon"))
                .findElement(By.className("refresh_wrap"))
                .findElement(By.className("icon-fi-refresh"));

        // 操作 - 刷新
        element.click();
    }

    /**
     * 获取 - 账户总金额
     *
     * @param driver
     * @return
     */
    public static BigDecimal getTotalAmount(WebDriver driver) {
        // 获取 - 账号element
        List<WebElement> elementTotalAmountElement = driver.findElement(By.className("leftmenu_content")).findElement(By.className("leftmenu_content_balance"))
                .findElements(By.className("row"));

        // 获取 - 总金额
        WebElement elementTotalAmount = elementTotalAmountElement.get(0).findElement(By.className("text-right"));
        String totalAmount = elementTotalAmount.getText();
        log.info("总金额 - totalAmount：" + totalAmount);
        return new BigDecimal(totalAmount);
    }

    /**
     * 获取 - 待结算金额
     *
     * @param driver
     * @return
     */
    public static BigDecimal getPendingSettlementAmount(WebDriver driver) {
        // 获取 - 账号element
        List<WebElement> elementPendingSettlementElement = driver.findElement(By.className("leftmenu_content")).findElement(By.className("leftmenu_content_balance"))
                .findElements(By.className("row"));

        // 获取 - 待结算金额
        WebElement elementPendingSettlement = elementPendingSettlementElement.get(1).findElement(By.className("text-right"));
        String pendingSettlementAmount = elementPendingSettlement.getText();
        return new BigDecimal(pendingSettlementAmount);
    }
}



