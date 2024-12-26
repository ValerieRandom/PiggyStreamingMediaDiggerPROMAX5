package com.xxx.BottonsBehavior;

import Util.SystemUtils;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Downloader {

    /**
     * 执行视频下载
     *
     * @param url       视频链接
     * @param format    视频格式
     * @param quality   视频画质
     * @param savePath  保存路径
     * @param osType    操作系统类型
     * @param logOutput 日志输出区域
     */
    public static void downloadVideo(String url, String format, String quality, String savePath, String osType, TextArea logOutput) {
        new Thread(() -> {
            try {
                File ytDlp = SystemUtils.getToolPath("yt-dlp", osType);
                String command = String.format("\"%s\" -f \"%s\" -o \"%s.%%(ext)s\" %s",
                        ytDlp.getAbsolutePath(), format, savePath + "/PiggyStreamingMediaDigger", url);

                if ("windows".equals(osType)) {
                    command = "cmd /c " + command;
                }

                logOutput.appendText("小豬開始進行挖掘工程...\n");
                logOutput.appendText("執行命令: " + command + "\n");

                Process process = Runtime.getRuntime().exec(command);

                // 处理输出日志，避免阻塞
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String finalLine = line;
                        Platform.runLater(() -> logOutput.appendText(finalLine + "\n"));
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    Platform.runLater(() -> logOutput.appendText("影片下載完成！\n"));
                } else {
                    Platform.runLater(() -> logOutput.appendText("下載失敗，退出代碼：" + exitCode + "\n"));
                }
            } catch (Exception ex) {
                Platform.runLater(() -> logOutput.appendText("下載過程發生錯誤：" + ex.getMessage() + "\n"));
            }
        }).start();
    }
}
