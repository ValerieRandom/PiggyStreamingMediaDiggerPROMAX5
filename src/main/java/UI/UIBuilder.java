package UI;

import com.xxx.BottonsBehavior.AvailableQualities;
import com.xxx.BottonsBehavior.Downloader;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class UIBuilder {

    public static VBox buildUI(String osType, TextArea logOutput) {
        VBox root = new VBox(10);

        // URL 输入框
        Label urlLabel = new Label("影片網址:");
        TextField urlField = new TextField();

        // 格式和画质下拉框
        ComboBox<String> formatCombo = new ComboBox<>();
        formatCombo.setPromptText("選擇格式");

        ComboBox<String> qualityCombo = new ComboBox<>();
        qualityCombo.setPromptText("選擇畫質");

        // URL 输入框监听逻辑（仅触发行为）
        urlField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                Platform.runLater(() -> logOutput.appendText("正在触发检测逻辑...\n"));

                // 调用 AvailableQualities 中的行为逻辑
                AvailableQualities.detectAndRecommend(
                        newValue, osType, logOutput, formatCombo, qualityCombo
                );
            }
        });

        // 下载按钮
        Button downloadButton = new Button("開始下載");
        downloadButton.setOnAction(e -> {
            String selectedFormat = formatCombo.getValue();
            String selectedQualityName = qualityCombo.getValue();

            if (selectedFormat == null || selectedQualityName == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("請選擇格式和畫質！");
                alert.showAndWait();
                return;
            }

            String selectedQualityCode = selectedQualityName.split(" - ")[0]; // 提取畫質代碼

            Downloader.downloadVideo(
                    urlField.getText(),
                    selectedFormat,
                    selectedQualityCode,
                    "C:/Users/Downloads",
                    osType,
                    logOutput
            );
        });

        // 将所有组件加入主容器
        root.getChildren().addAll(urlLabel, urlField, formatCombo, qualityCombo, downloadButton, logOutput);
        return root;
    }
}
