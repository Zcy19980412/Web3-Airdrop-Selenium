package org.util;

import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

}
