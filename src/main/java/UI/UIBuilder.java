package UI;

import com.xxx.piggystreamingmediadiggerpromax5.Downloader;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIBuilder {

    /**
     * 建立使用者介面，並使用呼叫端傳入的 TextArea 進行日誌輸出。
     *
     * @param osType    作業系統類型 ("windows" 或 "mac")
     * @param logOutput 已在外部建立好的 TextArea，用於顯示日誌
     * @return          組裝後的主介面 VBox
     */
    public static VBox buildUI(String osType, TextArea logOutput) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // 標籤與輸入框
        Label urlLabel = new Label(" 你希望小豬挖哪個影片給你哇 URL:");
        TextField urlField = new TextField();

        // 格式選擇
        Label formatLabel = new Label(" 請你選一下要什麼格式ㄟ: ");
        ComboBox<String> formatCombo = new ComboBox<>();
        formatCombo.getItems().addAll("MP4 (H.264)", "WMV", "MOV (H.264)");
        formatCombo.setPromptText("選擇影片格式");

        // 畫質選擇
        Label qualityLabel = new Label(" 你要什麼畫質哇: ");
        ComboBox<String> qualityCombo = new ComboBox<>();
        qualityCombo.setPromptText("選擇畫質");

        // 用於保存畫質名稱與實際參數（代碼）的對應關係
        Map<String, String> qualityMapping = new HashMap<>();

        // 設定 TextArea 在 VBox 中可自動擴張
        VBox.setVgrow(logOutput, Priority.ALWAYS);

        // 當用戶在 urlField 輸入新網址時，自動偵測可用畫質
        urlField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                Platform.runLater(() -> logOutput.appendText("小豬正在檢測畫質...不要吵ㄟ\n"));

                new Thread(() -> {
                    try {
                        // 獲取畫質列表 (「畫質代碼:畫質名稱」)
                        List<String> qualities = Downloader.getAvailableQualities(newValue, osType, logOutput);

                        Platform.runLater(() -> {
                            qualityCombo.getItems().clear();
                            qualityMapping.clear();

                            if (!qualities.isEmpty()) {
                                for (String qualityEntry : qualities) {
                                    // 例如 "137:1080p"
                                    String[] parts = qualityEntry.split(":");
                                    if (parts.length == 2) {
                                        String code = parts[0];
                                        String qName = parts[1];
                                        if (!qualityMapping.containsKey(qName)) {
                                            qualityCombo.getItems().add(qName);
                                            qualityMapping.put(qName, code);
                                        }
                                    }
                                }
                                logOutput.appendText("檢測完成，請選擇所需畫質。\n");
                            } else {
                                logOutput.appendText("未偵測到可用畫質！\n");
                            }
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            logOutput.appendText("畫質檢測失敗：" + ex.getMessage() + "\n");
                        });
                    }
                }).start();
            }
        });


        // 下載按鈕
        Button downloadButton = new Button("開始下載");
        downloadButton.setOnAction(e -> {
            String selectedFormat = formatCombo.getValue();
            String selectedQualityName = qualityCombo.getValue();

            if (selectedFormat == null || selectedFormat.isEmpty()) {
                NotificationManager.showErrorNotification(
                        "大錯誤!",
                        "請先選擇影片格式！一個一個選喇! (豬蹄揮舞)"
                );
                return;
            }

            if (selectedQualityName == null || selectedQualityName.isEmpty()) {
                NotificationManager.showErrorNotification(
                        "大錯誤!",
                        "請接著選擇畫質！不然小豬沒辦法做事ㄟ!"
                );
                return;
            }

            // 先從畫質名稱對應表中拿到實際的畫質代碼
            String selectedQualityCode = qualityMapping.get(selectedQualityName);
            if (selectedQualityCode == null) {
                NotificationManager.showErrorNotification(
                        "大錯誤!",
                        "無法對應所選的畫質代碼，請重新選擇或重新偵測畫質。"
                );
                return;
            }

            new Thread(() -> {
                try {
                    // 1. 下載影片 (預設MP4)
                    Downloader.downloadVideo(
                            urlField.getText(),
                            selectedFormat,
                            selectedQualityCode,
                            "C:/Users/valer/Downloads",
                            osType,
                            logOutput
                    );

                    // 2. 若選擇非 MP4，進行轉檔
                    if (!"MP4 (H.264)".equals(selectedFormat)) {
                        // 假設下載的檔案為 PiggyStreamingMediaDigger.mp4
                        String downloadedFilePath = "C:/Users/valer/Downloads/PiggyStreamingMediaDigger.mp4";

                        // 根據選擇的格式決定輸出副檔名
                        String outputExtension = "";
                        if ("MOV (H.264)".equals(selectedFormat)) {
                            outputExtension = ".mov";
                        } else if ("WMV".equals(selectedFormat)) {
                            outputExtension = ".wmv";
                        }
                        String outputFilePath = downloadedFilePath.replace(".mp4", outputExtension);

                        Downloader.convertFormat(downloadedFilePath, outputFilePath, osType, logOutput);
                    }
                } catch (Exception ex1) {
                    Platform.runLater(() -> {
                        logOutput.appendText("下載或轉檔過程發生錯誤：" + ex1.getMessage() + "\n");
                    });
                }
            }).start();
        });

        root.getChildren().addAll(
                urlLabel, urlField,
                formatLabel, formatCombo,
                qualityLabel, qualityCombo,
                downloadButton,
                logOutput
        );

        return root;
    }

    // 若需要 App 圖示可以在這裡加載
    public static Image getAppIcon() {
        return new Image("file:icon.png");
    }
}
