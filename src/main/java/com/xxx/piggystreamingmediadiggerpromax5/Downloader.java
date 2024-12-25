package com.xxx.piggystreamingmediadiggerpromax5;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Downloader {

    /**
     * 檢測可用畫質並返回畫質清單 (含代碼與名稱的對應)
     */
    public static List<String> getAvailableQualities(
            String url,
            String osType,
            TextArea logOutput
    ) throws Exception {

        File ytDlp = Utils.getToolPath("yt-dlp", osType);
        if (!ytDlp.exists()) {
            throw new FileNotFoundException("未找到 yt-dlp 工具，請確保其存在於 TOOLS 中！");
        }

        String ytDlpPath = ytDlp.getAbsolutePath();
        String command = String.format("\"%s\" -F %s", ytDlpPath, url);
        if (osType.equals("windows")) {
            command = "cmd /c " + command;
        }

        logOutput.appendText("小豬在解析畫質有哪些選項!(跳埃及舞給你看打發時間): " + command + "\n");
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        List<String> qualityMappings = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            // 不再輸出每行資訊，只做邏輯判斷
            if (line.matches("^\\d+\\s+\\w+\\s+.+")) {
                String[] parts = line.split("\\s+");
                if (parts.length > 2) {
                    String qualityCode = parts[0];
                    String resolution = parts[2];
                    String[] resolutionParts = resolution.split("x");
                    if (resolutionParts.length == 2) {
                        // 組出 "1080p" 之類的
                        String height = resolutionParts[1];
                        String qualityName = height + "p";
                        qualityMappings.add(qualityCode + ":" + qualityName);
                    }
                }
            }
        }
        process.waitFor();
        reader.close();

        return qualityMappings;
    }


    /**
     * 下載影片
     *
     * @param url        影片網址
     * @param format     使用者選擇的格式 (實際可用來做後續判斷是否要轉檔)
     * @param quality    使用者選擇的畫質"代碼" (呼叫 yt-dlp 時 -f 的參數)
     * @param savePath   影片要下載到哪個資料夾
     * @param osType     作業系統類型
     * @param logOutput  日誌輸出區域
     */
    public static void downloadVideo(
            String url,
            String format,
            String quality,
            String savePath,
            String osType,
            TextArea logOutput
    ) throws Exception {

        File ytDlp = Utils.getToolPath("yt-dlp", osType);
        String finalFormatStr = "bestvideo[ext=mp4]+bestaudio[ext=m4a]";

        String outputPrefix = savePath + "/PiggyStreamingMediaDigger";
        String command = String.format("\"%s\" -f \"%s\" --merge-output-format mp4 -o \"%s.%%(ext)s\" %s",
                ytDlp.getAbsolutePath(), finalFormatStr, outputPrefix, url);


        if ("windows".equals(osType)) {
            command = "cmd /c " + command;
        }

        logOutput.appendText("小豬開始進行挖掘工程 轟轟轟 看我操作 ! " + command + "\n");
        Process process = Runtime.getRuntime().exec(command);

        // 讀取輸出防止阻塞，但不再逐行輸出到 TextArea
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while (reader.readLine() != null) {
                // 不做任何顯示
            }
        }

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            Platform.runLater(() -> logOutput.appendText("影片下載完成！\n"));
        } else {
            Platform.runLater(() -> logOutput.appendText("下載發生錯誤，退出代碼：" + exitCode + "\n"));
        }
    }

    public static void convertFormat(
            String inputPath,
            String outputPath,
            String osType,
            TextArea logOutput
    ) throws Exception {
        File ffmpeg = Utils.getToolPath("ffmpeg", osType);
        String ffmpegPath = ffmpeg.getAbsolutePath();

        String command = String.format("\"%s\" -i \"%s\" -c:v libx264 -c:a aac \"%s\"",
                ffmpegPath, inputPath, outputPath);

        if ("windows".equals(osType)) {
            command = "cmd /c " + command;
        }

        logOutput.appendText("捲捲尾巴變形金剛出動! " + command + "\n");
        Process process = Runtime.getRuntime().exec(command);

        // 同樣只為防止阻塞，不做逐行輸出
        try (BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            while (stdOutput.readLine() != null) {}
            while (stdError.readLine() != null) {}
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            Platform.runLater(() -> {
                logOutput.appendText("轉檔過程中發生錯誤，退出代碼：" + exitCode + "\n");
            });
            return;
        }

        if (new File(outputPath).exists()) {
            Platform.runLater(() -> {
                logOutput.appendText("已完成轉檔，輸出檔案：" + outputPath + "\n");
            });
        } else {
            Platform.runLater(() -> {
                logOutput.appendText("轉檔失敗！未找到輸出檔案：" + outputPath + "\n");
            });
        }
    }
}

