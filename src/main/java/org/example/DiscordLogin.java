package org.example;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Files;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DiscordLogin {

    private static final Random rnd = new Random();
    public static List<String> browsers = List.of("kxyw4oy");
    public static List<String> discordAccounts = Arrays.asList(
            "caerintisci1983@rambler.ru:Web3@Alex7281:wv7c 2ubb vry5 32s6 qwef gz7a r5jd ibgn"
    );
    private static final long TOTP_TIME_STEP_SECONDS = 30;
    private static final String DISCORD_INVITE_URL = "https://discord.com/invite/r2yield";

    public static void main(String[] args) throws InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        long start = System.currentTimeMillis();

        for (int i = 0; i < browsers.size(); i++) {
            String browser = browsers.get(i);
            String account = discordAccounts.get(i % discordAccounts.size());
            String[] credentials = account.split(":");

            System.out.println("正在处理账户: " + credentials[0]);

            String debugPort = "";
            String webDriver = "";
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://127.0.0.1:50325/api/v1/browser/start?user_id=" + browser))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("AdsPower API 响应: " + response.body());

                JSONObject jsonObject = JSON.parseObject(response.body());
                Map<String, String> data = (Map<String, String>) jsonObject.get("data");
                debugPort = data.get("debug_port");
                webDriver = data.get("webdriver");
            } catch (Exception e) {
                System.out.println("AdsPower API 错误: " + e.getMessage());
                e.printStackTrace();
                continue;
            }

            System.setProperty("webdriver.chrome.driver", webDriver);
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("debuggerAddress", "127.0.0.1:" + debugPort);
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-popup-blocking");
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");

            WebDriver driver = null;
            int retries = 2;
            while (retries > 0) {
                try {
                    driver = new ChromeDriver(options);
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    try {
                        js.executeScript("Object.defineProperty(navigator, 'webdriver', { get: () => undefined });");
                        js.executeScript("Object.defineProperty(navigator, 'platform', { get: () => 'Win32' });");
                    } catch (JavascriptException e) {
                        System.out.println("执行 JavaScript 隐藏自动化属性失败: " + e.getMessage());
                        throw e;
                    }

                    System.out.println("浏览器语言: " + js.executeScript("return navigator.language;"));
                    System.out.println("会话 ID: " + ((ChromeDriver) driver).getSessionId());

                    humanDelay(2000, 5000);

                    clearBrowserCache(driver);

                    humanDelay(2000, 5000);

                    loginToDiscord(driver, credentials[0], credentials[1], credentials.length > 2 ? credentials[2] : null);

                    System.out.println("Discord 登录操作完成: " + credentials[0]);
                    break;
                } catch (NoSuchSessionException e) {
                    System.out.println("会话断开，重试剩余: " + (retries - 1));
                    e.printStackTrace();
                    retries--;
                    if (driver != null) {
                        try {
                            driver.quit();
                        } catch (Exception ignored) {}
                    }
                } catch (Exception e) {
                    System.out.println("脚本执行出错 (" + credentials[0] + "): " + e.getMessage());
                    e.printStackTrace();
                    break;
                } finally {
                    if (driver != null) {
                        try {
                            driver.quit();
                        } catch (Exception ignored) {}
                    }
                }
            }
            humanDelay(5000, 10000);
        }
        long end = System.currentTimeMillis();
        System.out.println("所有操作完成，总耗时: " + (end - start) + "ms");
    }

    private static void clearBrowserCache(WebDriver driver) throws InterruptedException {
        try {
            driver.switchTo().defaultContent();
            long stepStart = System.currentTimeMillis();

            driver.get("chrome://settings/clearBrowserData");
            humanDelay(4000, 6000);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            String pageTitle = (String) js.executeScript("return document.title;");
            System.out.println("清除缓存页面标题: " + pageTitle);

            if (!pageTitle.contains("Privacy and security")) {
                System.out.println("未加载清理页面，尝试 JavaScript 清除存储");
                js.executeScript(
                        "document.cookie.split(';').forEach(c => document.cookie = c.replace(/^ +/, '').replace(/=.*/, '=;expires=Thu, 01 Jan 1970 00:00:00 GMT'));" +
                                "window.localStorage.clear();" +
                                "window.sessionStorage.clear();" +
                                "indexedDB.databases().then(dbs => dbs.forEach(db => indexedDB.deleteDatabase(db.name)));" +
                                "window.caches.keys().then(keys => keys.forEach(key => caches.delete(key)));"
                );
                humanDelay(1000, 2000);
                System.out.println("JavaScript 清除存储完成");
                return;
            }

            WebElement clearButton = null;
            try {
                clearButton = (WebElement) js.executeScript(
                        "function findInShadow(root) {" +
                                "  if (!root) return null;" +
                                "  let button = root.querySelector('#clearButton');" +
                                "  if (button) return button;" +
                                "  for (let el of root.querySelectorAll('*')) {" +
                                "    if (el.shadowRoot) {" +
                                "      let found = findInShadow(el.shadowRoot);" +
                                "      if (found) return found;" +
                                "    }" +
                                "  }" +
                                "  return null;" +
                                "}" +
                                "return findInShadow(document.querySelector('settings-ui')?.shadowRoot || " +
                                "document.querySelector('settings-clear-browsing-data')?.shadowRoot || " +
                                "document.querySelector('clear-browsing-data')?.shadowRoot || document);"
                );
                if (clearButton == null) {
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    clearButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("cr-button#clearButton")
                    ));
                }
            } catch (Exception e) {
                System.out.println("定位 clearButton 失败: " + e.getMessage());
                String shadowInfo = (String) js.executeScript(
                        "let shadowHost = document.querySelector('settings-ui') || " +
                                "document.querySelector('settings-clear-browsing-data') || " +
                                "document.querySelector('clear-browsing-data');" +
                                "return shadowHost?.shadowRoot?.querySelector('#clearButton')?.outerHTML || 'No Shadow DOM button found';"
                );
                String mainDomInfo = (String) js.executeScript(
                        "return document.querySelector('cr-button#clearButton')?.outerHTML || 'No main DOM button found';"
                );
                System.out.println("Shadow DOM 调试: " + shadowInfo);
                System.out.println("主 DOM 调试: " + mainDomInfo);
            }

            if (clearButton == null) {
                System.out.println("无法定位清除数据按钮，尝试 JavaScript 清除存储");
                js.executeScript(
                        "document.cookie.split(';').forEach(c => document.cookie = c.replace(/^ +/, '').replace(/=.*/, '=;expires=Thu, 01 Jan 1970 00:00:00 GMT'));" +
                                "window.localStorage.clear();" +
                                "window.sessionStorage.clear();" +
                                "indexedDB.databases().then(dbs => dbs.forEach(db => indexedDB.deleteDatabase(db.name)));" +
                                "window.caches.keys().then(keys => keys.forEach(key => caches.delete(key)));"
                );
                humanDelay(1000, 2000);
                System.out.println("JavaScript 清除存储完成");
            } else {
                js.executeScript("arguments[0].click();", clearButton);
                humanDelay(2000, 4000);
                System.out.println("浏览器缓存清理完成，耗时: " + (System.currentTimeMillis() - stepStart) + "ms");
            }
        } catch (Exception e) {
            System.out.println("清理缓存失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private static void loginToDiscord(WebDriver driver, String email, String password, String twoFaSecret) throws InterruptedException {
        try {
            long stepStart = System.currentTimeMillis();
            driver.get("https://discord.com/login");
            humanDelay(4000, 8000);

            System.out.println("当前 URL: " + driver.getCurrentUrl());
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean isOnline = (Boolean) js.executeScript("return navigator.onLine;");
            System.out.println("网络状态: " + (isOnline ? "在线" : "离线"));

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            if (driver.getCurrentUrl().contains("channels/@me")) {
                System.out.println("已登录状态，直接加入服务器");
                joinDiscordServer(driver);
                return;
            }

            WebElement emailField = null;
            try {
                emailField = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("input[aria-label='Email or Phone Number'], input[name='email']")
                ));
            } catch (Exception e) {
                System.out.println("邮箱输入框定位失败: " + e.getMessage());
                String pageSource = (String) js.executeScript("return document.documentElement.outerHTML;");
                System.out.println("登录页面 HTML (前1000字符): " + pageSource.substring(0, Math.min(pageSource.length(), 1000)));
                throw e;
            }

            humanMoveAndClick(driver, emailField);
            js.executeScript("arguments[0].value = '';", emailField);
            humanType(driver, emailField, email);
            humanDelay(1000, 2000);
            String emailValue = (String) js.executeScript("return arguments[0].value;", emailField);
            System.out.println("邮箱输入框值: " + emailValue);
            if (!emailValue.equals(email)) {
                System.out.println("邮箱输入失败，尝试 JavaScript 设置");
                js.executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input'));", emailField, email);
                emailValue = (String) js.executeScript("return arguments[0].value;", emailField);
                System.out.println("JavaScript 设置后邮箱值: " + emailValue);
            }

            WebElement passwordField = null;
            try {
                passwordField = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("input[type='password'][name='password']")
                ));
            } catch (Exception e) {
                System.out.println("密码输入框定位失败: " + e.getMessage());
                throw e;
            }

            humanMoveAndClick(driver, passwordField);
            js.executeScript("arguments[0].value = '';", passwordField);
            humanType(driver, passwordField, password);
            humanDelay(1000, 2000);
            String passwordValue = (String) js.executeScript("return arguments[0].value;", passwordField);
            System.out.println("密码输入框值: " + passwordValue);
            if (!passwordValue.equals(password)) {
                System.out.println("密码输入失败，尝试 JavaScript 设置");
                js.executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input'));", passwordField, password);
                passwordValue = (String) js.executeScript("return arguments[0].value;", passwordField);
                System.out.println("JavaScript 设置后密码值: " + passwordValue);
            }

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit']")
            ));
            humanMoveAndClick(driver, loginButton);
            humanDelay(4000, 8000);

            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div[class*='sidebar'], nav[class*='sidebar']")
                ));
                System.out.println("登录成功：检测到侧边栏");
            } catch (Exception e) {
                System.out.println("登录验证失败，可能未成功登录: " + e.getMessage());
                String pageSource = (String) js.executeScript("return document.documentElement.outerHTML;");
                System.out.println("登录后页面 HTML (前1000字符): " + pageSource.substring(0, Math.min(pageSource.length(), 1000)));
            }

            if (twoFaSecret != null) {
                handle2FA(driver, twoFaSecret);
            }

            System.out.println("登录成功，等待加入服务器...");
            humanDelay(4000, 8000);

            joinDiscordServer(driver);

            System.out.println("登录流程完成，耗时: " + (System.currentTimeMillis() - stepStart) + "ms");
        } catch (Exception e) {
            System.out.println("Discord 登录失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private static void handle2FA(WebDriver driver, String twoFaSecret) throws InterruptedException {
        try {
            long stepStart = System.currentTimeMillis();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement twoFaField = null;
            try {
                twoFaField = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("input[placeholder='6-digit authentication code']")
                ));
            } catch (Exception e) {
                System.out.println("2FA 输入框未找到: " + e.getMessage());
                JavascriptExecutor js = (JavascriptExecutor) driver;
                String pageSource = (String) js.executeScript("return document.documentElement.outerHTML;");
                System.out.println("登录页面 HTML (前1000字符): " + pageSource.substring(0, Math.min(pageSource.length(), 1000)));
                throw e;
            }

            String twoFaCode = TOTPUtil.generateTOTP(twoFaSecret);
            if (twoFaCode == null || !twoFaCode.matches("\\d{6}")) {
                System.out.println("生成 TOTP 验证码失败或格式不正确: " + twoFaCode);
                throw new RuntimeException("无法生成有效 2FA 验证码");
            }

            humanType(driver, twoFaField, twoFaCode);
            humanDelay(1000, 2000);

            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit']")
            ));
            humanMoveAndClick(driver, submitButton);
            System.out.println("2FA 验证完成，耗时: " + (System.currentTimeMillis() - stepStart) + "ms");
        } catch (Exception e) {
            System.out.println("2FA 处理失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private static void joinDiscordServer(WebDriver driver) throws InterruptedException {
        try {
            long stepStart = System.currentTimeMillis();
            System.out.println("开始加入 Discord 服务器: " + DISCORD_INVITE_URL);

            // 打开新标签页
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.open();");
            humanDelay(1000, 2000);

            // 切换到新标签页
            String originalWindow = driver.getWindowHandle();
            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(originalWindow)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            // 访问邀请链接
            driver.get(DISCORD_INVITE_URL);
            humanDelay(6000, 12000);

            System.out.println("当前 URL: " + driver.getCurrentUrl());
            String pageSource = (String) js.executeScript("return document.documentElement.outerHTML;");
            System.out.println("邀请页面 HTML (前1000字符): " + pageSource.substring(0, Math.min(pageSource.length(), 1000)));

            // 模拟人类行为：随机滚动和鼠标移动
            js.executeScript("window.scrollTo(0, document.body.scrollHeight * Math.random());");
            humanDelay(1000, 3000);
            js.executeScript("var evt = new MouseEvent('mousemove', {bubbles: true, cancelable: true, clientX: Math.random() * window.innerWidth, clientY: Math.random() * window.innerHeight}); document.dispatchEvent(evt);");
            humanDelay(1000, 3000);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            // 检查是否出现 CAPTCHA
            boolean hasCaptcha = false;
            try {
                driver.findElement(By.cssSelector("iframe[src*='hcaptcha.com'], iframe[src*='turnstile']"));
                hasCaptcha = true;
                System.out.println("检测到 CAPTCHA，请手动完成验证...");
                Thread.sleep(60000);
            } catch (NoSuchElementException e) {
                System.out.println("未检测到 CAPTCHA，继续点击加入按钮");
            }

            WebElement joinButton = null;
            try {
                // 主定位：基于按钮内的 div 文本
                joinButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[div[contains(text(), 'Accept Invite') or contains(text(), '接受邀请') or contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'join')]]")
                ));
            } catch (Exception e) {
                System.out.println("主定位失败: " + e.getMessage());
                try {
                    // 备用定位：基于类名
                    joinButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("button.button__201d5.lookFilled__201d5, button[class*='button'][class*='lookFilled'][class*='colorBrand']")
                    ));
                } catch (Exception e2) {
                    System.out.println("备用定位失败: " + e2.getMessage());
                    // 保存页面截图
                    File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    Files.copy(screenshot.toPath(), new File("error_screenshot.png").toPath());
                    System.out.println("已保存页面截图: error_screenshot.png");
                    throw e;
                }
            }

            humanMoveAndClick(driver, joinButton);
            humanDelay(4000, 8000);

            // 验证是否成功加入服务器
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div[class*='sidebar'], nav[class*='sidebar'], div[class*='guilds']")
                ));
                System.out.println("成功加入 Discord 服务器");
            } catch (Exception e) {
                System.out.println("验证服务器加入失败: " + e.getMessage());
                pageSource = (String) js.executeScript("return document.documentElement.outerHTML;");
                System.out.println("服务器页面 HTML (前1000字符): " + pageSource.substring(0, Math.min(pageSource.length(), 1000)));
                try {
                    WebElement agreeButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'agree') or contains(text(), '同意') or contains(text(), 'accept') or contains(text(), 'confirm') or contains(text(), '继续')]")
                    ));
                    humanMoveAndClick(driver, agreeButton);
                    humanDelay(4000, 8000);
                    System.out.println("已同意服务器规则");
                    wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("div[class*='sidebar'], nav[class*='sidebar'], div[class*='guilds']")
                    ));
                    System.out.println("成功加入 Discord 服务器（同意规则后）");
                } catch (Exception e2) {
                    System.out.println("无法定位同意规则按钮: " + e2.getMessage());
                    // 保存页面截图
                    File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    Files.copy(screenshot.toPath(), new File("error_screenshot_rules.png").toPath());
                    System.out.println("已保存页面截图: error_screenshot_rules.png");
                    throw e;
                }
            }

            // 关闭新标签页并切换回原窗口
            driver.close();
            driver.switchTo().window(originalWindow);

            System.out.println("加入服务器完成，耗时: " + (System.currentTimeMillis() - stepStart) + "ms");
        } catch (Exception e) {
            System.out.println("加入 Discord 服务器失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void humanType(WebDriver driver, WebElement element, String text) throws InterruptedException {
        humanMoveAndClick(driver, element);
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            Thread.sleep(100 + rnd.nextInt(200));
        }
    }

    private static void humanMoveAndClick(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            Point location = element.getLocation();
            int elementWidth = element.getSize().getWidth();
            int elementHeight = element.getSize().getHeight();
            int x = location.getX() + rnd.nextInt(elementWidth / 2) + elementWidth / 4;
            int y = location.getY() + rnd.nextInt(elementHeight / 2) + elementHeight / 4;

            int steps = 10;
            int startX = (int) (Math.random() * driver.manage().window().getSize().width);
            int startY = (int) (Math.random() * driver.manage().window().getSize().height);
            for (int i = 1; i <= steps; i++) {
                int currentX = startX + (x - startX) * i / steps + rnd.nextInt(10) - 5;
                int currentY = startY + (y - startY) * i / steps + rnd.nextInt(10) - 5;
                js.executeScript("var evt = new MouseEvent('mousemove', {bubbles: true, cancelable: true, clientX: arguments[0], clientY: arguments[1]}); document.dispatchEvent(evt);", currentX, currentY);
                Thread.sleep(50 + rnd.nextInt(50));
            }

            js.executeScript("var evt = new MouseEvent('mouseover', {bubbles: true, cancelable: true, clientX: arguments[0], clientY: arguments[1]}); arguments[2].dispatchEvent(evt);", x, y, element);
            Thread.sleep(500 + rnd.nextInt(1500));
            element.click();
        } catch (Exception e) {
            System.out.println("鼠标移动或点击失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void humanDelay(int minMs, int maxMs) throws InterruptedException {
        Thread.sleep(minMs + rnd.nextInt(maxMs - minMs));
    }
}