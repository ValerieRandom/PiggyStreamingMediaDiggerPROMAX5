package com.xxx.BottonsBehavior;

import com.xxx.Util.SystemUtils;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AvailableQualities {

    /**
     * 检测画质并推荐最佳组合（主逻辑）
     */
    public static void detectAndRecommend(String url, String osType, TextArea logOutput,
                                          ComboBox<String> formatCombo, ComboBox<String> qualityCombo) {
        new Thread(() -> {
            try {
                // 获取可用画质
                List<String> formatLines = getAvailableQualities(url, osType, logOutput);
                String recommendedCombination = recommendBestCombination(formatLines);

                // 更新 UI 元件
                Platform.runLater(() -> {
                    formatCombo.getItems().clear();
                    qualityCombo.getItems().clear();

                    for (String line : formatLines) {
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 3) {
                            String code = parts[0];
                            String description = line.replaceFirst("^\\d+\\s+", "").split("\\|")[0].trim();
                            qualityCombo.getItems().add(code + " - " + description);
                        }
                    }

                    if (recommendedCombination != null) {
                        formatCombo.getItems().add("推荐: " + recommendedCombination);
                        formatCombo.setValue("推荐: " + recommendedCombination);
                        logOutput.appendText("推荐下载组合: " + recommendedCombination + "\n");
                    } else {
                        logOutput.appendText("无法推荐下载组合，请手动选择。\n");
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> logOutput.appendText("检测失败：" + ex.getMessage() + "\n"));
            }
        }).start();
    }

    /**
     * 获取可用画质列表
     */
    public static List<String> getAvailableQualities(String url, String osType, TextArea logOutput) throws Exception {
        File ytDlp = SystemUtils.getToolPath("yt-dlp", osType);
        String command = String.format("\"%s\" -F %s", ytDlp.getAbsolutePath(), url);
        if (osType.equals("windows")) {
            command = "cmd /c " + command;
        }

        logOutput.appendText("执行命令: " + command + "\n");

        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        List<String> qualityMappings = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.matches("^\\d+\\s+.+")) { // 简单匹配画质行
                qualityMappings.add(line);
            }
        }

        process.waitFor();
        reader.close();
        return qualityMappings;
    }

    /**
     * 推荐最佳下载组合
     */
    public static String recommendBestCombination(List<String> formatLines) {
        // 示例：推荐第一个画质组合
        if (!formatLines.isEmpty()) {
            return formatLines.get(0).split("\\s+")[0]; // 返回第一个画质代码
        }
        return null;
    }
}
