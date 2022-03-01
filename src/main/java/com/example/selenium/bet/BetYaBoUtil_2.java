package com.example.selenium.bet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.example.selenium.bo.YaBoAccountInfoBO;
import com.example.selenium.bo.YaBoInfoBO;
import com.example.selenium.entity.BetGameInfo;
import com.example.selenium.handle.BetGameInfoHandle;
import com.example.selenium.util.CacheMapUtil;
import com.example.selenium.util.DingUtil;
import com.example.selenium.util.SleepUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-12 15:46:39
 * @description: 描述
 */
@Slf4j
@Component
public class BetYaBoUtil_2 {

    @Autowired
    private CacheMapUtil cacheMapUtil;

    @Autowired
    private BetGameInfoHandle betGameInfoHandle;

    /**
     * 启动下注
     */
    public void bet() {

        // 定义 - 参数
        DingUtil d = new DingUtil();
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
            String bet_url = cacheMapUtil.getBetUrl("BET_URL");
            driver.get(bet_url);
            // 操作 - 屏幕最大化
            SleepUtil.sleepUtil(2000);
            driver.manage().window().maximize();

            // 判断 - 是否允许操作
            WebElement main_wrapper = driver.findElement(By.className("main_wrapper"));
            for (int c = 0; c < 10; c++) {
                SleepUtil.sleepUtil(500);
                main_wrapper.click();
            }

            // 获取 - 当前页面
            driver = driver.switchTo().window(driver.getWindowHandle());
            SleepUtil.sleepUtil(5000);

            // 操作 - 赛选篮球联赛
            driver.findElement(By.xpath("//*[@id=\"left_panel\"]/div[5]/div/div[2]/div/div[3]/div/label/span")).click();
            SleepUtil.sleepUtil(2000);

            // 操作 - 获取账号信息
            YaBoAccountInfoBO yaBoAccountInfo = this.getYaBoAccountInfo(driver);
            log.info("========================================");
            log.info("账户金额 - originalAmount：{}；剩余金额 - balanceAmount：{}；下注金额 - betAmount：{}",
                yaBoAccountInfo.getOriginalAmount(), yaBoAccountInfo.getBalanceAmount(),
                yaBoAccountInfo.getBetAmount());
            log.info("========================================");

            // 操作 - 获取正在比赛的场次
            List<WebElement> event_listing = driver.findElements(By.className("event_listing"));
            log.info("event_listing.isEnabled()、event_listing.isDisplayed() - {},{}", event_listing.get(0).isEnabled(),
                event_listing.get(0).isDisplayed());
            SleepUtil.sleepUtil(2000);
            List<WebElement> competitionHeader = driver.findElements(By.className("competition_header"));
            log.info("competitionHeader.isEnabled()、competitionHeader.isDisplayed() - {},{}",
                competitionHeader.get(0).isEnabled(), competitionHeader.get(0).isDisplayed());
            int chSize = competitionHeader.size();
            for (int ch = 0; ch < chSize; ch++) {
                WebElement competition_header_team =
                    competitionHeader.get(ch).findElement(By.className("competition_header_team"));
                log.info("competition_header_team.isEnabled()、competition_header_team.isDisplayed() - {},{}",
                    competition_header_team.isEnabled(), competition_header_team.isDisplayed());
                // 判断 - 是否NCAA比赛，如果是则跳过
                if (competition_header_team.getText().contains("NCAA")) {
                    competition_header_team.click();
                    log.info("检测到NCAA比赛，终止此次比赛");
                    continue;
                }

                // 设定 - 联赛名称
                yaBoAccountInfo.setCompetitionName(competition_header_team.getText());

                // 获取 - 每个场次比赛
                List<WebElement> row_live = event_listing.get(ch).findElements(By.className("row_live"));
                log.info("row_live.isEnabled()、row_live.isDisplayed() - {},{}", row_live.get(0).isEnabled(),
                    row_live.get(0).isDisplayed());
                if (row_live != null && row_live.size() > 0) {

                    // 获取 - 场次赛事信息
                    List<WebElement> elements =
                        row_live.get(0).findElement(By.className("team")).findElements(By.className("teamname_inner"));
                    log.info("elements.isEnabled()、elements.isDisplayed() - {},{}", elements.get(0).isEnabled(),
                        elements.get(0).isDisplayed());
                    // 设定 - 主队、客队
                    yaBoAccountInfo.setHomeTeamName(elements.get(0).getText());
                    yaBoAccountInfo.setAwayTeamName(elements.get(1).getText());

                    // 获取 - 比分
                    WebElement score = row_live.get(0).findElement(By.className("team"))
                        .findElement(By.className("datetime")).findElement(By.tagName("div"));
                    WebElement node = row_live.get(0).findElement(By.className("team"))
                        .findElement(By.className("datetime")).findElement(By.tagName("span"));

                    // 操作 - 同时存在及进入
                    log.info("score.isEnabled()、score.isDisplayed() - {},{}", score.isEnabled(), score.isDisplayed());
                    log.info("node.isEnabled()、node.isDisplayed() - {},{}", node.isEnabled(), node.isDisplayed());
                    if ((score.isEnabled() && score.isDisplayed()) && (node.isEnabled() && node.isDisplayed())) {

                        // 获取 - 进入下注页面
                        WebElement aClick =
                            row_live.get(0).findElement(By.className("additional")).findElement(By.tagName("a"));
                        SleepUtil.sleepUtil(3000);
                        aClick.click();

                        // 获取 - 当前页面
                        driver = driver.switchTo().window(driver.getWindowHandle());
                        SleepUtil.sleepUtil(5000);

                        // 操作 - 点击已节为操作
                        WebElement sevmenu_tab_content = driver.findElement(By.className("sevmenu_tab_content"));
                        List<WebElement> sevmenu_tab_inner =
                            sevmenu_tab_content.findElements(By.className("sevmenu_tab_inner"));
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
                        List<WebElement> betList =
                            driver.findElement(By.className("group_wrap")).findElements(By.className("bet_type_wrap"));

                        // 操作 - 下注界面
                        this.bettingOperation(driver, betList, yaBoAccountInfo, d);
                    }
                    break;
                }
            }
            log.info("操作完成，退出该操作");
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

    /***
     * 操作 - 下注页面
     *
     * @param driver
     * @param betList
     * @param yaBoAccountInfo
     * @param d
     * @return void
     * @author yucw
     * @date 2022-02-24 15:07
     */
    public void bettingOperation(WebDriver driver, List<WebElement> betList, YaBoAccountInfoBO yaBoAccountInfo,
        DingUtil d) throws Exception {
        for (WebElement bet : betList) {
            WebElement betElement = bet.findElement(By.className("bet_type_row")).findElement(By.className("left"));
            if (betElement.getText().contains("单/双")) {

                // 设置 - 节数
                yaBoAccountInfo.setWhichSection(changeBetWhichSection(betElement.getText()));

                // 操作 - 将单双点开
                betElement.click();
                SleepUtil.sleepUtil(3000);

                // 获取 - 最新一节单双选项
                List<WebElement> dsList = bet.findElement(By.className("ReactCollapse--collapse"))
                    .findElement(By.className("ReactCollapse--content")).findElement(By.className("bet_type_content"))
                    .findElement(By.className("content_row")).findElement(By.className("odds_col"))
                    .findElements(By.className("col"));

                // 操作 - 看倍率大小下注
                WebElement bet_odd =
                    dsList.get(0).findElement(By.className("odds_wrap")).findElement(By.className("odds"));
                WebElement bet_double =
                    dsList.get(1).findElement(By.className("odds_wrap")).findElement(By.className("odds"));
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
                    WebElement input_wrap = bs.findElement(By.className("input_amount_wrap"))
                        .findElement(By.className("bet_amt_input_wrap")).findElement(By.className("input_wrap"))
                        .findElement(By.className("placebet_input"));

                    // 操作 - 下注
                    SleepUtil.sleepUtil(2000);
                    input_wrap.sendKeys(yaBoAccountInfo.getBetAmount() + "");

                    // 下注 - 按钮
                    SleepUtil.sleepUtil(1000);
                    WebElement btn_placebet_action = bet_slip_wrap.findElement(By.className("btn_placebet_action"));

                    // 操作 - 按钮存在且允许点击
                    log.info("btn_placebet_action.isEnabled()、btn_placebet_action.isDisplayed() - {},{}",
                        btn_placebet_action.isEnabled(), btn_placebet_action.isDisplayed());
                    if (btn_placebet_action.isEnabled() && btn_placebet_action.isDisplayed()) {
                        // 点击 - 确认下注
                        btn_placebet_action.click();

                        // 下注 - 确认按钮
                        SleepUtil.sleepUtil(10000);
                        // 操作 - 下注是否完成
                        WebElement isOk = driver
                            .findElement(By.xpath("//*[@id=\"left_panle_placebet\"]/div[2]/div/div/div[1]/div[8]"));
                        log.info("判断是否存在下注成功的WebElement:{}", isOk.isEnabled(), isOk.getText());
                        if (isOk.isEnabled() && "投注成功".equals(isOk.getText())) {
                            WebElement btn_placebet_inaction =
                                bet_slip_wrap.findElement(By.className("btn_placebet_inaction"));
                            log.info("btn_placebet_inaction.isEnabled()、btn_placebet_inaction.isDisplayed() - {},{}",
                                btn_placebet_inaction.isEnabled(), btn_placebet_inaction.isDisplayed());
                            if (btn_placebet_inaction.isEnabled() && btn_placebet_inaction.isDisplayed()) {
                                btn_placebet_inaction.click();

                                // 消息 - 推送钉钉服务
                                YaBoInfoBO bet_a = JSON.parseObject(cacheMapUtil.getMap("BET_A"), YaBoInfoBO.class);
                                if (!StringUtils.isEmpty(yaBoAccountInfo.getOperateName()) && bet_a != null) {
                                    // 消息 - 推送钉钉服务
                                    sendDingMsg(d, yaBoAccountInfo.getOperateName(), bet_a.getHomeTeamName(),
                                        bet_a.getAwayTeamName(), bet_a.getWhichSection(), bet_a.getOriginalAmount(),
                                        bet_a.getBetAmount(), bet_a.getBalanceAmount());
                                    SleepUtil.sleepUtil(2000);
                                }

                                YaBoInfoBO yb = new YaBoInfoBO();
                                yb.setCompetitionName(yaBoAccountInfo.getCompetitionName());
                                yb.setHomeTeamName(yaBoAccountInfo.getHomeTeamName());
                                yb.setAwayTeamName(yaBoAccountInfo.getAwayTeamName());
                                yb.setWhichSection(yaBoAccountInfo.getWhichSection());
                                yb.setScreenings(yaBoAccountInfo.getScreenings());
                                yb.setBetAmount(yaBoAccountInfo.getBetAmount());
                                yb.setBalanceAmount(yaBoAccountInfo.getBalanceAmount());
                                yb.setOriginalAmount(yaBoAccountInfo.getOriginalAmount());
                                String lockId = cacheMapUtil.putMap("BET_A", JSON.toJSONString(yb));
                                sendDingMsg(d, "下注完成", yaBoAccountInfo.getHomeTeamName(),
                                    yaBoAccountInfo.getAwayTeamName(), yaBoAccountInfo.getWhichSection(),
                                    yaBoAccountInfo.getOriginalAmount(), yaBoAccountInfo.getBetAmount(),
                                    yaBoAccountInfo.getBalanceAmount());

                                // 操作 - 新校验逻辑
                                BetGameInfo betGameInfo = new BetGameInfo();
                                betGameInfo.setLockId(lockId);
                                betGameInfo.setOperateThreadType(0);
                                betGameInfo.setCompetitionName(yaBoAccountInfo.getCompetitionName());
                                betGameInfo.setHomeTeamName(yaBoAccountInfo.getHomeTeamName());
                                betGameInfo.setAwayTeamName(yaBoAccountInfo.getAwayTeamName());
                                betGameInfo.setWhichSection(yaBoAccountInfo.getWhichSection());
                                betGameInfo.setScreenings(yaBoAccountInfo.getScreenings());
                                betGameInfo.setOriginalAmount(yaBoAccountInfo.getOriginalAmount());
                                betGameInfo.setBalanceAmount(yaBoAccountInfo.getBalanceAmount());
                                betGameInfo.setBetAmount(yaBoAccountInfo.getBetAmount());
                                betGameInfo.setIsSettlement(0);
                                betGameInfo.setCompetitionType(0);
                                betGameInfoHandle.save(betGameInfo);
                            }
                        } else {
                            // 操作 - 关闭失败下注
                            log.info("下注失败，重新下注");
                            WebElement close = bet_slip_wrap.findElement(By.className("icon-fi-status-close"));
                            close.click();
                        }
                        break;
                    }
                }
            }
            continue;
        }
    }

    /***
     * 获取 - 账号操作信息
     *
     * @param driver
     * @return com.example.selenium.bo.YaBoAccountInfoBO
     * @author yucw
     * @date 2022-02-24 14:49
     */
    public YaBoAccountInfoBO getYaBoAccountInfo(WebDriver driver) {
        // 定义 - 参数
        String operateName = null;
        Integer screenings = 1;
        BigDecimal betAmount = null;
        BigDecimal originalAmount = null;
        BigDecimal balanceAmount = null;
        if (cacheMapUtil.getMap("BET_A") != null) {
            YaBoInfoBO bet_a = JSON.parseObject(cacheMapUtil.getMap("BET_A"), YaBoInfoBO.class);
            log.info("上一局 - YaBoInfoBO：{}", JSON.toJSONString(bet_a));
            // 判断 - 是否结算
            for (int ob = 0; ob < 10000000; ob++) {
                // 操作 - 账号刷新操作
                clickRefreshAccount(driver);
                SleepUtil.sleepUtil(5000);

                // 获取 - 账户总金额
                BigDecimal totalAmount = getTotalAmount(driver);
                // 获取 - 待结算金额
                BigDecimal pendingSettlementAmount = getPendingSettlementAmount(driver);
                log.info("结算金额是否为0.00 - pendingSettlementAmount.compareTo(new BigDecimal(0.00)) == 0 - {}",
                    pendingSettlementAmount.compareTo(new BigDecimal(0.00)) == 0);
                // 操作 - 结算金额为0，则项目结算完成
                if (pendingSettlementAmount.compareTo(new BigDecimal(0.00)) == 0) {
                    // 解析 - 赛事信息
                    BigDecimal cacheOriginalAmount = bet_a.getOriginalAmount();
                    log.info("下注前账户金额 - cacheOriginalAmount：" + cacheOriginalAmount);
                    if (cacheOriginalAmount.compareTo(totalAmount) > 0) {
                        // 小于 - 说明上一局失败
                        screenings = bet_a.getScreenings() + 1;
                        betAmount = getBetAmount(false, screenings);
                        operateName = "黑单";
                    } else if (cacheOriginalAmount.compareTo(totalAmount) < 0) {
                        // 大于 - 说明上一局成功
                        betAmount = getBetAmount(true, 1);
                        operateName = "红单";
                        screenings = 1;
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
            betAmount = getBetAmount(true, 1);
            balanceAmount = totalAmount.subtract(betAmount);
            screenings = 1;
        }

        // 整合 - 数据
        YaBoAccountInfoBO info = new YaBoAccountInfoBO();
        info.setScreenings(screenings);
        info.setOriginalAmount(originalAmount);
        info.setBalanceAmount(balanceAmount);
        info.setBetAmount(betAmount);
        info.setOperateName(operateName);
        return info;
    }

    /**
     * 转换节数
     *
     * @param whichSection
     * @return
     */
    public String changeBetWhichSection(String whichSection) throws Exception {
        if (whichSection.contains("第一节")) {
            return "第一节";
        } else if (whichSection.contains("第二节")) {
            return "第二节";
        } else if (whichSection.contains("第三节")) {
            return "第三节";
        } else if (whichSection.contains("第四节")) {
            return "第四节";
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
                return new BigDecimal("170");
            } else if (screenings == 7) {
                return new BigDecimal("345");
            } else if (screenings == 8) {
                return new BigDecimal("690");
            } else if (screenings == 9) {
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
        WebElement element =
            driver.findElement(By.className("leftmenu_account")).findElement(By.className("leftmenu_header"))
                .findElement(By.className("float-right")).findElement(By.className("leftmenu_icon"))
                .findElement(By.className("refresh_wrap")).findElement(By.className("icon-fi-refresh"));

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
        List<WebElement> elementTotalAmountElement =
            driver.findElement(By.className("leftmenu_account")).findElement(By.className("leftmenu_content"))
                .findElement(By.className("leftmenu_content_balance")).findElements(By.className("row"));

        // 获取 - 总金额
        WebElement elementTotalAmount = elementTotalAmountElement.get(0).findElement(By.className("text-right"));
        String totalAmount = elementTotalAmount.getText().replaceAll(",", "");
        log.info("总金额 - totalAmount：" + totalAmount);
        return new BigDecimal(Optional.ofNullable(totalAmount).orElse("0.00"));
    }

    /**
     * 获取 - 待结算金额
     *
     * @param driver
     * @return
     */
    public static BigDecimal getPendingSettlementAmount(WebDriver driver) {
        // 获取 - 账号element
        List<WebElement> elementPendingSettlementElement =
            driver.findElement(By.className("leftmenu_account")).findElement(By.className("leftmenu_content"))
                .findElement(By.className("leftmenu_content_balance")).findElements(By.className("row"));

        // 获取 - 待结算金额
        WebElement elementPendingSettlement =
            elementPendingSettlementElement.get(1).findElement(By.className("text-right"));
        String pendingSettlementAmount = elementPendingSettlement.getText().replaceAll(",", "");;
        log.info("待结算金额 - pendingSettlementAmount：" + pendingSettlementAmount);
        return new BigDecimal(Optional.ofNullable(pendingSettlementAmount).orElse("0.00"));
    }

    /***
     * 推送消息
     *
     * @param d
     * @param homeTeamName
     * @param awayTeamName
     * @param whichSection
     * @param originalAmount
     * @param betAmount
     * @param balanceAmount
     * @return java.lang.String
     * @author yucw
     * @date 2022-02-22 17:06
     */
    public static void sendDingMsg(DingUtil d, String operateName, String homeTeamName, String awayTeamName,
        String whichSection, BigDecimal originalAmount, BigDecimal betAmount, BigDecimal balanceAmount) {
        d.sendMassage("\n---------------------" + "\n比赛结论：" + operateName + "\n---------------------" + "\n比赛队伍："
            + homeTeamName + " VS " + awayTeamName + "\n比赛节数：" + whichSection + "\n---------------------" + "\n账户原始金额："
            + originalAmount + "\n当前下注金额：" + betAmount + "\n账户剩余金额：" + balanceAmount + "");
    }
}
