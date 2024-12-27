package com.xxx.BottonsBehavior;

import com.xxx.Util.SystemUtils;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

public class Downloader {

    //最佳組合下載

    // 生成下載命令
    private static String buildDownloadCommand(String url, String selectedQuality, String selectedFormat,
                                               Map<String, String> qualityToIdMap, Map<String, String> formatToIdMap,
                                               String osType) {
        try {
            String qualityId = qualityToIdMap.get(selectedQuality);
            String formatId = formatToIdMap.get(selectedFormat);

            if (qualityId == null || formatId == null) {
                throw new IllegalArgumentException("無法匹配選擇的畫質或格式");
            }

            File ytDlpPath = SystemUtils.getToolPath("yt-dlp", osType);
            return SystemUtils.getCommandPrefix(osType) + " " + ytDlpPath.getAbsolutePath()
                    + " -f " + qualityId + "+" + formatId + " " + url;
        } catch (Exception e) {
            System.err.println("生成下載命令時出現錯誤: " + e.getMessage());
            return null;
        }
    }

    // 手動下載
    public static void BestDownloadVideo(String url, String osType, TextArea logOutput) {
        new Thread(() -> {
            try {
                logOutput.clear();
                if (url.isEmpty()) {
                    logOutput.appendText(" URL 是空的就不要點下載喇! 白癡喔! 掃興ㄟ!\n");
                } else {
                    logOutput.appendText("小豬開始進行最佳組合下載工程...看我操作! 轟轟轟! \n");


                    String downloadDir = setupDownloadDirectory(osType);
                    logOutput.appendText("下載的檔案將存放在: " + downloadDir + "\n");

                    File ytDlpPath = SystemUtils.getToolPath("yt-dlp", osType);
                    String command = SystemUtils.getCommandPrefix(osType) + " " + ytDlpPath.getAbsolutePath()
                            + " -f bestvideo+bestaudio"
                            + " -o \"" + downloadDir + "/%(title)s [%(id)s].%(ext)s\""
                            + " " + url;

                    logOutput.appendText("執行命令: " + command + "\n");

                    // 執行命令
                    Process process = Runtime.getRuntime().exec(command);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String finalLine = line;
                        Platform.runLater(() -> logOutput.appendText(finalLine + "\n"));
                    }
                    reader.close();

                    StringBuilder errorOutput = new StringBuilder();
                    while ((line = errorReader.readLine()) != null) {
                        errorOutput.append(line).append("\n");
                    }
                    errorReader.close();

                    if (errorOutput.length() > 0) {
                        Platform.runLater(() -> logOutput.appendText("command失敗! 白癡喔! 掃興ㄟ! 低級錯誤叫小豬來看!: " + errorOutput.toString() + "\n"));
                    } else {
                        Platform.runLater(() -> logOutput.appendText("下載完成囉，小豬挖土機真厲害！檔案存放在: " + downloadDir + "\n"));
                    }

                    process.waitFor();
                }

            } catch (Exception e) {
                Platform.runLater(() -> logOutput.appendText("出了小豬也搞不清楚的錯誤 ! 但小豬知道這是錯誤  !但不知道是哪種錯誤! 快叫小豬來看!: " + e.getMessage() + "\n"));
            }
        }).start();
    }


    // 直接下載邏輯
    public static void directDownload(String url, String osType, TextArea logOutput) {
        new Thread(() -> {
            try {
                logOutput.clear();
                if (url.isEmpty()) {
                    logOutput.appendText(" URL 是空的就不要點直接下載喇! 白癡喔! 掃興ㄟ!\n");
                } else {
                    logOutput.appendText("小豬直接開始進行下載工程...看我操作! 轟轟轟! \n");

                    // 設定下載目錄
                    String downloadDir = setupDownloadDirectory(osType);
                    logOutput.appendText("下載的檔案將存放在: " + downloadDir + "\n");

                    // 構造下載命令，包含 --output
                    File ytDlpPath = SystemUtils.getToolPath("yt-dlp", osType);
                    String command = SystemUtils.getCommandPrefix(osType) + " " + ytDlpPath.getAbsolutePath()
                            + " --output \"" + downloadDir + "/%(title)s [%(id)s].%(ext)s\" " + url;

                    logOutput.appendText("執行命令: " + command + "\n");

                    // 執行命令
                    Process process = Runtime.getRuntime().exec(command);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String finalLine = line;
                        Platform.runLater(() -> logOutput.appendText(finalLine + "\n"));
                    }
                    reader.close();

                    StringBuilder errorOutput = new StringBuilder();
                    while ((line = errorReader.readLine()) != null) {
                        errorOutput.append(line).append("\n");
                    }
                    errorReader.close();

                    if (errorOutput.length() > 0) {
                        Platform.runLater(() -> logOutput.appendText("command失敗! 白癡喔! 掃興ㄟ! 低級錯誤叫小豬來看!: " + errorOutput.toString() + "\n"));
                    } else {
                        Platform.runLater(() -> logOutput.appendText("下載完成囉，小豬挖土機真厲害！檔案存放在: " + downloadDir + "\n"));
                    }

                    process.waitFor();
                }

            } catch (Exception e) {
                Platform.runLater(() -> logOutput.appendText("出了小豬也搞不清楚的錯誤 ! 但小豬知道這是錯誤  !但不知道是哪種錯誤! 快叫小豬來看!: " + e.getMessage() + "\n"));
            }
        }).start();
    }

    // 設定下載目錄
    private static String setupDownloadDirectory(String osType) {
        String userHome = System.getProperty("user.home");
        String downloadDir;

        // 根據操作系統設置目錄
        if (osType.equalsIgnoreCase("windows")) {
            downloadDir = userHome + "\\PiggyDownloads";
        } else {
            downloadDir = userHome + "/PiggyDownloads";
        }

        File downloadFolder = new File(downloadDir);
        if (!downloadFolder.exists()) {
            boolean created = downloadFolder.mkdirs(); // 創建目錄
            if (!created) {
                throw new RuntimeException("無法創建下載目錄: " + downloadDir);
            }
        }

        return downloadDir;
    }
}