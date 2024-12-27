package com.xxx.BottonsBehavior;

import com.xxx.Util.SystemUtils;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class ConvertFromat {

    public static void convertFormat(
            String inputPath,
            String outputPath,
            String osType,
            TextArea logOutput
    ) throws Exception {
        File ffmpeg = SystemUtils.getToolPath("ffmpeg", osType);
        String ffmpegPath = ffmpeg.getAbsolutePath();

        String command = String.format("\"%s\" -i \"%s\" -c:v libx264 -c:a aac \"%s\"",
                ffmpegPath, inputPath, outputPath);

        if (SystemUtils.WINDOWS.equals(osType)) {
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
