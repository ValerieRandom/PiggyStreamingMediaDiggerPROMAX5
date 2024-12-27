package com.xxx.BottonsBehavior;

import com.xxx.Util.SystemUtils;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLParser {

    private static final String FORMAT_REGEX = "(\\d+)\\s+(\\w+)(\\s+[\\w\\s]+|audio only)?";

    public static void handleUrlAction(String url, String osType, TextArea logOutput,
                                       ComboBox<String> formatCombo, ComboBox<Object> qualityCombo) {
        new Thread(() -> {
            logOutput.clear();
            if (!url.isEmpty()) {
                logOutput.appendText("小豬拿放大鏡檢查你的 URL，這要一點點時間...請先不要巴巴巴 \n");
            }
            List<Map<String, String>> formats = fetchFormats(url, osType, logOutput);
            if (formats.isEmpty()) {
                Platform.runLater(() -> logOutput.appendText("錯誤錯誤!!這樣是不對的! 小豬很混怒! 小豬很混怒!! \n"));
                return;
            }

            // 更新畫質與格式選項
            Platform.runLater(() -> updateFormatOptions(formats, formatCombo, qualityCombo, logOutput));
        }).start();
    }

    // 獲取格式列表
// 獲取格式列表
    private static List<Map<String, String>> fetchFormats(String url, String osType, TextArea logOutput) {
        List<Map<String, String>> formats = new ArrayList<>();
        try {
            File ytDlpPath = SystemUtils.getToolPath("yt-dlp", osType);
            String command = SystemUtils.getCommandPrefix(osType) + " " + ytDlpPath.getAbsolutePath() + " --list-formats " + url;

            logOutput.appendText("執行命令: " + command + "\n");

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            StringBuilder errorOutput = new StringBuilder();
            Pattern pattern = Pattern.compile(FORMAT_REGEX);
            String line;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    Map<String, String> format = new LinkedHashMap<>();
                    format.put("ID", matcher.group(1)); // 格式 ID
                    format.put("EXT", matcher.group(2)); // 文件格式
                    String resolution = matcher.groupCount() >= 3 && matcher.group(3) != null
                            ? matcher.group(3).trim()
                            : "未知";
                    format.put("RESOLUTION", parseResolution(resolution));
                    formats.add(format);
                }
            }
            reader.close();

            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }
            errorReader.close();

            if (errorOutput.length() > 0) {
                logOutput.appendText("命令執行時出現錯誤: \n" + errorOutput + "\n");
            }
            process.waitFor();
        } catch (Exception e) {
            logOutput.appendText("解析格式時出現錯誤: " + e.getMessage() + "\n");
        }
        return formats;
    }

    // 更新畫質與格式選項
    private static void updateFormatOptions(List<Map<String, String>> formats, ComboBox<String> formatCombo,
                                            ComboBox<Object> qualityCombo, TextArea logOutput) {
        // 使用 LinkedHashMap 確保順序並去重
        Map<String, String> qualityToIdMap = new LinkedHashMap<>();
        Map<String, String> formatToIdMap = new LinkedHashMap<>();

        for (Map<String, String> format : formats) {
            String resolution = parseResolution(format.get("RESOLUTION"));
            String ext = format.get("EXT");
            String id = format.get("ID");

            if (resolution != null && !qualityToIdMap.containsKey(resolution)) {
                qualityToIdMap.put(resolution, id);
            }

            if (ext != null && !formatToIdMap.containsKey(ext)) {
                formatToIdMap.put(ext, id);
            }
        }

        Platform.runLater(() -> {
            // 更新畫質下拉選單
            qualityCombo.getItems().clear();
            qualityToIdMap.forEach((resolution, id) -> qualityCombo.getItems().add(resolution));
            if (!qualityCombo.getItems().isEmpty()) {
                qualityCombo.getSelectionModel().selectFirst();
            }

            // 更新格式下拉選單
            formatCombo.getItems().clear();
            formatToIdMap.forEach((ext, id) -> formatCombo.getItems().add(ext));
            if (!formatCombo.getItems().isEmpty()) {
                formatCombo.getSelectionModel().selectFirst();
            }

            logOutput.appendText("解析完成，可選擇畫質與格式。\n");
        });
    }

    // 解析畫質為用戶友好的格式
    private static String parseResolution(String resolution) {
        try {
            System.out.println("解析畫質: 原始值 -> " + resolution);

            if (resolution.toLowerCase().contains("audio")) {
                System.out.println("解析畫質結果: 音訊專用");
                return "音訊專用";
            } else if (resolution.matches("\\d+x\\d+")) {
                String[] dimensions = resolution.split("x");
                String height = dimensions[1].replaceAll("\\D", "");
                System.out.println("解析畫質結果: " + height + "p");
                return height + "p";
            }

            System.out.println("解析畫質結果: 未知");
            return "未知";
        } catch (Exception e) {
            System.err.println("解析畫質時出現錯誤: " + e.getMessage());
            return "未知";
        }
    }
}
