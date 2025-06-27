package org.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.openqa.selenium.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class PageUtil {

    public static SearchContext expandShadowRoots(WebDriver driver, String... selectors) {
        SearchContext context = driver;
        for (String selector : selectors) {
            WebElement host = context.findElement(By.cssSelector(selector));
            context = host.getShadowRoot();
        }
        return context;
    }

    public static void closeExtraTabsSafe(WebDriver driver, int keepCount) {
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        for (int i = keepCount; i < handles.size(); i++) {
            try {
                driver.switchTo().window(handles.get(i));
                Thread.sleep(500); // 稍作延迟
                driver.close();
            } catch (Exception e) {
                System.out.println("关闭窗口失败: " + e.getMessage());
            }
        }
        driver.switchTo().window(handles.get(0));
    }

    public static String openNewWindow(WebDriver driver, String url) {
        Set<String> windowHandlesOriginal = driver.getWindowHandles();
        ((JavascriptExecutor) driver).executeScript("window.open(\"" + url + "\")");
        Set<String> windowHandlesNew = driver.getWindowHandles();
        windowHandlesNew.removeAll(windowHandlesOriginal);
        String windowHandle = windowHandlesNew.iterator().next();
        driver.switchTo().window(windowHandle);
        System.out.println("打开新标签页：" + url);
        return windowHandle;
    }

    public static void sendKeysHumanLike(WebElement element, String text) {
        Random random = new Random();
        int minDelay = 80;   // 最快每个字符 80ms
        int maxDelay = 160;  // 最慢每个字符 160ms

        for (char c : text.toCharArray()) {
            element.sendKeys(Character.toString(c));
            try {
                int delay = minDelay + random.nextInt(maxDelay - minDelay + 1);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 保持中断状态
            }
        }
    }

    public static <R> R retry(WebDriver driver, Function<WebDriver, R> function, int retryTime) {
        int count = 0;
        RuntimeException lastException = null;

        while (count < retryTime) {
            try {
                return function.apply(driver);
            } catch (RuntimeException e) {
                lastException = e;
                count++;
                System.out.println("Retry " + count + "/" + retryTime + " failed: " + e.getMessage());
                try {
                    Thread.sleep(1000); // 可选：每次重试之间暂停一下
                    driver.navigate().refresh();
                    Thread.sleep(3 * 1000); // 可选：每次重试之间暂停一下
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }

        throw new RuntimeException("Function failed after " + retryTime + " retries", lastException);
    }


    public static <R> R retry(Supplier<R> supplier, int retryTime) {
        int count = 0;
        RuntimeException lastException = null;

        while (count < retryTime) {
            try {
                return supplier.get();
            } catch (RuntimeException e) {
                lastException = e;
                count++;
                System.out.println("Retry " + count + "/" + retryTime + " failed: " + e.getMessage());
                try {
                    Thread.sleep(10 * 1000); // 可选：每次重试之间暂停一下
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }

        throw new RuntimeException("Function failed after " + retryTime + " retries", lastException);
    }


    public static Map<String, String> startBrowser(String browserId) {
        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://127.0.0.1:50325/api/v1/browser/start?user_id=" + browserId))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("启动浏览器响应: " + response.body());

            JSONObject json = JSON.parseObject(response.body());
            Integer code = json.getInteger("code");

            if (code != null && code == 0) {
                // 启动成功，返回数据
                Map<String, String> data = (Map<String, String>) json.get("data");
                return data;
            } else {
                throw new RuntimeException("启动浏览器响应: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("启动浏览器失败: " + e.getMessage());
            throw new RuntimeException("启动浏览器失败: " + e.getMessage());
        }
    }

    public static void stopBrowser(String browserId) {
        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://127.0.0.1:50325/api/v1/browser/stop?user_id=" + browserId))
                    .GET()
                    .build();

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("关闭浏览器响应: " + response.body());

            JSONObject json = JSON.parseObject(response.body());
            Integer code = json.getInteger("code");
            if (code == null || code != 0) {
                throw new RuntimeException("code == null || code != 0");
            }
        } catch (Exception e) {
            System.out.println("关闭浏览器失败：" + e.getMessage());
            throw new RuntimeException("Failed to stop browser for user_id=" + browserId, e);
        }
    }
}
