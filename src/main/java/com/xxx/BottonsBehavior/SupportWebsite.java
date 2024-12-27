package com.xxx.BottonsBehavior;

import com.xxx.UI.UIBuilder;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.xxx.Util.SystemUtils;

public class SupportWebsiteBehavior {

    private static final Map<Character, List<String>> SITE_MAP = new TreeMap<>();
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static boolean isDataFetched = false; // 標誌是否已加載網站數據


    // 處理字母按鈕的點擊事件
    public static void handleLetterButtonClick(TextArea logOutput, String osType, char letter) {
        if (SITE_MAP.isEmpty()) {
            // 如果緩存為空，第一次點擊需要獲取所有網站
            fetchAllSupportedSites(osType, logOutput, letter);
        } else {
            // 從緩存中顯示對應字母的網站
            showSitesByLetter(logOutput, letter);
        }
    }

    private static void fetchAllSupportedSites(String osType, TextArea logOutput, char initialLetter) {
        EXECUTOR.submit(() -> {
            try {

                File ytDlpPath = SystemUtils.getToolPath("yt-dlp", osType);
                String command = SystemUtils.getCommandPrefix(osType) + " " + ytDlpPath.getAbsolutePath() + " --list-extractors";
                System.out.println("執行命令: " + command);

                Process process = Runtime.getRuntime().exec(command);

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                String line;
                List<String> allSites = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    allSites.add(line.trim());
                }
                reader.close();

                StringBuilder errorOutput = new StringBuilder();
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }
                errorReader.close();

                if (errorOutput.length() > 0) {
                    System.err.println("命令執行錯誤: " + errorOutput);
                }
                process.waitFor();

                organizeSitesByLetter(allSites);
                isDataFetched = true;
                Platform.runLater(() -> showSitesByLetter(logOutput, initialLetter));
            } catch (Exception e) {
                Platform.runLater(() -> logOutput.appendText("無法獲取支援的網站清單：" + e.getMessage() + "\n"));
            }
        });
    }


    private static void showSitesByLetter(TextArea logOutput, char letter) {
        Platform.runLater(() -> {
            logOutput.clear();
            List<String> sites = SITE_MAP.get(letter);
            if (sites == null || sites.isEmpty()) {
                logOutput.appendText("目前沒有以 " + letter + " 開頭的網站。\n\n");
            } else {
                logOutput.appendText("本工具支援以下 " + letter + " 開頭的網站：\n\n");
                sites.forEach(site -> logOutput.appendText(site + "\n" ));
            }
        });
    }

    private static void organizeSitesByLetter(List<String> allSites) {
        SITE_MAP.clear();
        for (String site : allSites) {
            if (!site.isEmpty()) {
                char firstChar = Character.toUpperCase(site.charAt(0));
                SITE_MAP.computeIfAbsent(firstChar, k -> new ArrayList<>()).add(site);
            }
        }
    }

    public static void clearCache() {
        SITE_MAP.clear();
        isDataFetched = false;
        System.out.println("CacheCleaned");
    }
}
