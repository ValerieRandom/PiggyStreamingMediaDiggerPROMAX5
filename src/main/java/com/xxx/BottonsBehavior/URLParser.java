package com.xxx.BottonsBehavior;

import com.xxx.Util.SystemUtils;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLParser {

    public static void handleUrlAction(String url, String osType, TextArea logOutput,
                                       ComboBox<String> formatCombo, ComboBox<Object> qualityCombo) {
        if (logOutput == null || formatCombo == null || qualityCombo == null) {
            System.out.println("UI 元件未正確初始化！");
            return;
        }

        new Thread(() -> {
            Platform.runLater(logOutput::clear);
            Platform.runLater(() -> logOutput.appendText("小豬拿放大鏡檢查你的 URL，這要一點點時間...\n"));

            List<Map<String, String>> formats = parseFormats(url, osType, logOutput);
            if (formats.isEmpty()) {
                Platform.runLater(() -> logOutput.appendText("錯誤錯誤!!小豬很混怒!!\n"));
                return;
            }
            Platform.runLater(() -> updateFormatOptions(formats, formatCombo, qualityCombo, logOutput));
        }).start();
    }

    private static void updateFormatOptions(List<Map<String, String>> formats, ComboBox<String> formatCombo,
                                            ComboBox<Object> qualityCombo, TextArea logOutput) {
        Platform.runLater(() -> {
            // 清空舊選項
            formatCombo.getItems().clear();
            qualityCombo.getItems().clear();

            // 生成畫質選項（去重並排序）
            formats.stream()
                    .map(format -> formatResolution(format.get("RESOLUTION"))) // 轉換解析度格式
                    .distinct() // 去重
                    .sorted() // 按順序排列
                    .forEach(qualityCombo.getItems()::add);

            // 生成格式選項，將 ID、EXT 和 RESOLUTION 結合展示
            formats.stream()
                    .map(format -> String.format("ID: %s | Format: %s | Resolution: %s",
                            format.get("ID"),
                            format.get("EXT"),
                            formatResolution(format.get("RESOLUTION")))) // 組合選項
                    .distinct() // 去重
                    .forEach(formatCombo.getItems()::add);

            // 更新完成提示
            logOutput.appendText("選單更新完成！可供選擇的格式與畫質已更新。\n");
        });
    }

    private static List<Map<String, String>> parseFormats(String url, String osType, TextArea logOutput) {
        List<Map<String, String>> formats = new ArrayList<>();

        try {
            // 執行 yt-dlp 命令列，獲取格式清單
            File ytDlpPath = SystemUtils.getToolPath("yt-dlp", osType);
            String command = SystemUtils.getCommandPrefix(osType) + " " + ytDlpPath.getAbsolutePath() + " --list-formats " + url;

            Process process = Runtime.getRuntime().exec(command);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;

                // 定義正則表達式匹配格式數據
                Pattern formatPattern = Pattern.compile(
                        "(\\S+)\\s+(\\S+)\\s+(\\S+|audio only).*"
                );

                while ((line = reader.readLine()) != null) {
                    String finalLine = line.trim();
                    Platform.runLater(() -> logOutput.appendText(finalLine + "\n"));
                    System.out.println("原始行: " + finalLine);

                    // 使用正則表達式解析每行數據
                    Matcher matcher = formatPattern.matcher(finalLine);
                    if (matcher.matches()) {
                        Map<String, String> format = new HashMap<>();
                        format.put("ID", matcher.group(1)); // 提取 ID
                        format.put("EXT", matcher.group(2)); // 提取格式
                        format.put("RESOLUTION", matcher.group(3)); // 提取解析度（或 audio only）

                        formats.add(format); // 保存格式化數據
                        System.out.println("提取成功的格式: " + format);
                    } else {
                        System.out.println("未匹配的行: " + finalLine);
                    }
                }

            } catch (IOException e) {
                Platform.runLater(() -> logOutput.appendText("執行命令失敗: " + e.getMessage() + "\n"));
                System.err.println("執行命令失敗: " + e.getMessage());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return formats;
    }

    public static String formatResolution(String resolution) {
        if (resolution.contains("x")) {
            return resolution.split("x")[1] + "p";
        }
        if ("audio only".equals(resolution)) {
            return "Audio Only";
        }
        return resolution;
    }
}