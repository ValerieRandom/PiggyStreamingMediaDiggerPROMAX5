package com.xxx.UI;

import com.xxx.BottonsBehavior.SupportWebsiteBehavior;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

import static com.xxx.BottonsBehavior.SupportWebsiteBehavior.handleLetterButtonClick;


public class UIBuilder {

    public static VBox buildUI(String osType, TextArea logOutput) {

        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #ffe4e1;");

        NotificationService notificationManager = new NotificationService();

        Button supportButton = new Button("字母列表說明");
        supportButton.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff69b4; " +
                "-fx-background-color: transparent; -fx-border-color: #ff69b4; -fx-border-radius: 5;");
        supportButton.setOnAction(event ->
                notificationManager.showInfoNotification("SUPPORTED_SITES",null));


        Button refreshButton = new Button("刷新字母列表");
        refreshButton.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff69b4; -fx-background-color: transparent; -fx-border-color: #ff69b4; -fx-border-radius: 5;");
        refreshButton.setOnAction(event -> {
            SupportWebsiteBehavior.clearCache();
            logOutput.clear();
            notificationManager.showInfoNotification("CACHE_CLEARED", null);
        });

        HBox topButtons = new HBox(10);
        topButtons.getChildren().addAll(supportButton, refreshButton);

        /*
            -Hint 0-
        // 使用說明通知
        Button usageGuideButton = new Button("使用說明");
        usageGuideButton.setOnAction(event ->
                notificationManager.showInfoNotification("USAGE_GUIDE", null)
        );

        // 功能更新通知
        Button featureUpdateButton = new Button("功能更新");
        featureUpdateButton.setOnAction(event ->
                notificationManager.showInfoNotification("FEATURE_UPDATE", null)
        );
        */

        // A-Z 按鈕區域
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(5);
        buttonGrid.setVgap(5);
        buttonGrid.setStyle("-fx-background-color: #ffc0cb; -fx-padding: 10; -fx-border-color: #ffb6c1; -fx-border-radius: 5;");

        int col = 0, row = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            Button letterButton = new Button(String.valueOf(c));
            letterButton.setMinWidth(40);
            letterButton.setStyle("-fx-background-color: #ffb6c1; -fx-text-fill: #ffffff; -fx-border-radius: 3;");

            buttonGrid.add(letterButton, col++, row);
            if (col == 13) {
                col = 0;
                row++;
            }

            char finalC = c;
            letterButton.setOnAction(e -> handleLetterButtonClick(logOutput, osType, finalC));
        }

        Label urlLabel = new Label("影片網址:");
        urlLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff69b4;");
        TextField urlField = new TextField();
        urlField.setPromptText("請輸入影片網址");
        urlField.setStyle("-fx-border-color: #ffb6c1; -fx-border-radius: 3; -fx-padding: 5; -fx-background-color: #fff0f5;");
        HBox urlBox = new HBox(10);
        urlBox.getChildren().addAll(urlLabel, urlField);
        HBox.setHgrow(urlField, Priority.ALWAYS);
        urlBox.setStyle("-fx-padding: 5;");


        Label formatLabel = new Label("影片格式:");
        formatLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff69b4;");
        ComboBox<String> formatCombo = new ComboBox<>();
        formatCombo.setPromptText("選擇格式");
        formatCombo.setPrefWidth(140);
        formatCombo.setStyle("-fx-background-color: #fff0f5; -fx-border-color: #ffb6c1;");

        Label qualityLabel = new Label("畫質:");
        qualityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff69b4;");
        ComboBox<String> qualityCombo = new ComboBox<>();
        qualityCombo.setPromptText("選擇畫質");
        qualityCombo.setPrefWidth(185);
        qualityCombo.setStyle("-fx-background-color: #fff0f5; -fx-border-color: #ffb6c1;");


        /*
            -Hint-
            setMinWidth()	設置元件的「最小寬度」，寬度不能比這個值更小。
            setPrefWidth()	設置元件的「首選寬度」，當父容器大小允許時，元件會嘗試使用這個寬度。
            setMaxWidth()	設置元件的「最大寬度」，寬度不能比這個值更大。
            setWidth()	（靜態布局使用）直接設置元件的固定寬度，不允許拉伸或壓縮。

         */
        Region spacer = new Region();
        spacer.setPrefWidth(135);

        HBox optionsBox = new HBox(10);
        optionsBox.getChildren().addAll(

                formatLabel,
                formatCombo,
                spacer,
                qualityLabel,
                qualityCombo
        );

        optionsBox.setStyle("-fx-padding: 5;");

        // 日誌輸出區域
        logOutput.setStyle("-fx-font-size: 14px; " +
                "-fx-font-family: 'Consolas'; " +
                "-fx-text-fill: black; " +
                "-fx-background-color: #f4f4f4; " +
                "-fx-line-spacing: 5px;");
        logOutput.setWrapText(true); // 自動換行
        logOutput.setPadding(new Insets(10)); // 添加內邊距
        logOutput.setEditable(false);
        VBox.setVgrow(logOutput, Priority.ALWAYS);

        root.getChildren().addAll(

                topButtons,
                buttonGrid,
                urlBox,
                optionsBox,
                logOutput

        );

        return root;
    }

    public static Scene buildRoot(String osType) {
        TextArea logOutput = new TextArea();
        VBox root = buildUI(osType, logOutput);
        return new Scene(root, 625, 645);
    }
}
