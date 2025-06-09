package org.util.chromeExtentions;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.util.PageUtil;

import java.time.Duration;
import java.util.Random;

public class OKXExtensionUtil {
    public static String loginOKXWallet(WebDriver driver, String password) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // 导航到 OKX 钱包扩展页面
            String okxExtensionId = "mcohilncbfahbmgdjkbpemcciiolgcge"; // 替换为实际扩展 ID
            String okxUrl = "chrome-extension://" + okxExtensionId + "/popup.html";
            driver.get(okxUrl);
            driver.navigate().refresh();
            Thread.sleep(3 * 1000);
            ((JavascriptExecutor) driver).executeScript("document.body.style.zoom='0.9'");
            System.out.println("导航到 OKX 钱包扩展页面: " + okxUrl);
            humanDelay(5000, 8000);

            // 定位密码输入框并输入密码
            try {
                WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[data-testid='okd-input']") // 使用提供的 HTML 选择器
                ));
                passwordInput.clear();
                passwordInput.clear();
                passwordInput.sendKeys(password);
                System.out.println("OKX 钱包输入密码成功！");
                humanDelay(1000, 2000);
            } catch (Exception e) {
                System.out.println("定位或输入密码失败: " + e.getMessage());
                throw e;
            }

            // 定位并点击“Unlock”按钮
            try {
                WebElement unlockButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[data-testid='okd-button']") // 使用提供的 HTML 选择器
                ));
                unlockButton.click();
                System.out.println("OKX 钱包登陆成功！");
                humanDelay(2000, 4000);
            } catch (Exception e) {
                System.out.println("点击 Unlock 按钮失败: " + e.getMessage());
                // 尝试 JavaScript 点击
                try {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("document.querySelector('button[data-testid=\"okd-button\"]').click();");
                    System.out.println("通过 JavaScript 点击 Unlock 按钮");
                    humanDelay(2000, 4000);
                } catch (Exception jsError) {
                    System.out.println("JavaScript 点击失败: " + jsError.getMessage());
                    throw e;
                }
            }
        } catch (Exception e) {
            System.out.println("OKX 钱包登录流程失败: " + e.getMessage());
            throw e;
        } finally {
            // 切回默认上下文
            driver.switchTo().defaultContent();
        }
        return driver.getWindowHandle();
    }

    // 随机延迟方法
    private static void humanDelay(int minMs, int maxMs) throws InterruptedException {
        Random rnd = new Random();
        Thread.sleep(minMs + rnd.nextInt(maxMs - minMs));
    }

    public static void confirm(WebDriver driver) {
        if (!needConfirm(driver)) {
            System.out.println("no need to confirm");
            return;
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement webElement;
        webElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//div[text()='Confirm']]") // 修正后的 CSS 选择器
        ));
        webElement.click();
        System.out.println("okx wallet confirm done");
    }

    public static void connect(WebDriver driver) throws InterruptedException {
        if (!needConnect(driver)) {
            System.out.println("no need to connect");
            confirm(driver);
            return;
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement webElement;
        webElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//div[text()='Connect']]") // 修正后的 CSS 选择器
        ));
        webElement.click();
        System.out.println("okx wallet connect done");
        humanDelay(1000, 2000);
        confirm(driver);
    }

    private static boolean needConnect(WebDriver driver) {
        return containsElement(driver, "//button[.//div[text()='Connect']]");
    }

    public static void approve(WebDriver driver) throws InterruptedException {
        if (needAddNetWork(driver)) {
            addNetWork(driver);
            humanDelay(1000, 3000);
        }
        if (!needApprove(driver)) {
            System.out.println("no need to approve");
            return;
        }
        //方式1
        if (containsElement(driver, "//div[text()='Set to unlimited']")) {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement webElement;
            webElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[type='text'][min='0']") // 修正后的 CSS 选择器
            ));
            webElement.click();
            PageUtil.sendKeysHumanLike(webElement, "1000000");

            webElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[.//span[text()='Confirm amount']]") // 修正后的 CSS 选择器
            ));
            webElement.click();

            confirm(driver);

            System.out.println("okx wallet approve sign done");
        } else if (containsElement(driver, "//div[text()='Approve']")) {
            //方式2
            confirm(driver);
            System.out.println("okx wallet approve sign done");
        }
    }

    public static void addNetWork(WebDriver driver) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement webElement;
        webElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//div[text()='Approve']]") // 修正后的 CSS 选择器
        ));
        webElement.click();
        System.out.println("Add Network done");
    }

    public static boolean needApprove(WebDriver driver) {
        return containsElement(driver, "//div[text()='Set to unlimited']")
                || containsElement(driver, "//div[text()='Approve']")
                || needAddNetWork(driver);
    }

    public static boolean needConfirm(WebDriver driver) {
        return containsElement(driver, "//button[.//div[text()='Confirm']]");
    }

    public static boolean needAddNetWork(WebDriver driver) {
        return containsElement(driver, "//div[text()='Add network']");
    }


    private static boolean containsElement(WebDriver driver, String xpath) {
        ((JavascriptExecutor) driver).executeScript("document.body.style.zoom='0.9'");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement webElement = null;
        try {
            webElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath(xpath) // 修正后的 CSS 选择器
            ));
        } catch (Exception e) {
            System.out.println("no webElement:" + xpath);
        }
        return webElement != null;
    }
}