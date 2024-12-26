package com.xxx.Util;

import javafx.scene.control.Alert;

import java.io.File;

public class SystemUtils {

    public static String detectOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "windows";
        } else if (os.contains("mac")) {
            return "mac";
        } else {
            return "unsupported";
        }
    }

    public static File getToolPath(String toolName, String osType) {
        // 取得目前執行程式所在資料夾
        String currentDir = System.getProperty("user.dir");
        File toolsDir = new File(currentDir, "TOOLS");
        String executableName = toolName + (osType.equals("windows") ? ".exe" : "");

        File toolFile = new File(toolsDir, executableName);

        if (!toolFile.exists()) {
            throw new RuntimeException("工具未找到: " + toolFile.getAbsolutePath());
        }
        System.out.println("找到工具: " + toolFile.getAbsolutePath());
        return toolFile;
    }

    public static void ensureDependencies(String osType) {
        try {
            getToolPath("yt-dlp", osType);
            getToolPath("ffmpeg", osType);
            System.out.println("所有工具已準備完畢！");
        } catch (Exception e) {
            System.err.println("工具檢查失敗：" + e.getMessage());
            throw new RuntimeException("工具檢查失敗，請確保 TOOLS 資料夾中存在正確的工具！");
        }
    }

    public static void showAlert(String title, String content) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public static String getCommandPrefix(String osType) {
        if (osType == null || osType.isEmpty()) {
            throw new IllegalArgumentException("作業系統類型不能為空！");
        }

        switch (osType.toLowerCase()) {
            case "windows":
                return "cmd /c";
            case "mac":
                return "/bin/bash -c"; // Unix/Linux/macOS 系統使用 bash
            default:
                throw new UnsupportedOperationException("不支持的操作系統類型: " + osType);
        }
    }
}
