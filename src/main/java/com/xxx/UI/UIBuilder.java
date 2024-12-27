package com.xxx.UI;

import com.xxx.BottonsBehavior.Downloader;
import com.xxx.BottonsBehavior.SupportWebsite;
import com.xxx.BottonsBehavior.URLParser;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

import static com.xxx.BottonsBehavior.SupportWebsite.handleLetterButtonClick;


public class UIBuilder {

    public static VBox buildUI(String osType, TextArea logOutput) {

        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #ffe4e1;");

        NotificationService notificationManager = new NotificationService();

        Button supportButton = new Button("字母列表說明");
        supportButton.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff69b4; " +
                "-fx-background-color: transparent; -fx-border-color: #ff69b4; -fx-border-radius: 5;");
        supportButton.setOnAction(event -> notificationManager.showInfoNotification("SUPPORTED_SITES", null));

        Button refreshButton = new Button("刷新字母列表");
        refreshButton.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff69b4; " +
                "-fx-background-color: transparent; -fx-border-color: #ff69b4; -fx-border-radius: 5;");
        refreshButton.setOnAction(event -> {
            SupportWebsite.clearCache();
            logOutput.clear();
            notificationManager.showInfoNotification("CACHE_CLEARED", null);
        });

        Button BestCombinationButton = new Button("最佳下載組合說明");
        BestCombinationButton.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff69b4; " +
                "-fx-background-color: transparent; -fx-border-color: #ff69b4; -fx-border-radius: 5;");
        BestCombinationButton.setOnAction(event -> notificationManager.showInfoNotification("BEST_COMBINATION", null));

        Button StartFormat = new Button("開始解析說明");
        StartFormat.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff69b4; " +
                "-fx-background-color: transparent; -fx-border-color: #ff69b4; -fx-border-radius: 5;");
        StartFormat.setOnAction(event -> notificationManager.showInfoNotification("START_FORMAT", null));

        Button DirectDownloadButton = new Button("直接下載說明");
        DirectDownloadButton.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff69b4; " +
                "-fx-background-color: transparent; -fx-border-color: #ff69b4; -fx-border-radius: 5;");
        DirectDownloadButton.setOnAction(event ->
                notificationManager.showInfoNotification("DIRECT_DOWNLOAD", null));


        HBox topButtons = new HBox(10);
        topButtons.getChildren().addAll(supportButton, refreshButton, StartFormat, DirectDownloadButton,BestCombinationButton);


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

        Label urlLabel = new Label("URL:");
        urlLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff69b4;");
        TextField urlField = new TextField();
        urlField.setPromptText("請複製貼上影片網址");
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
        ComboBox<Object> qualityCombo = new ComboBox<>();
        qualityCombo.setPromptText("選擇畫質");
        qualityCombo.setPrefWidth(185);
        qualityCombo.setStyle("-fx-background-color: #fff0f5; -fx-border-color: #ffb6c1;");


        // 手動下載按鈕

        Button DDButton = new Button("直接下載");
        DDButton.setStyle("-fx-background-color: #ff69b4; -fx-text-fill: white; -fx-font-size: 14px;");
        DDButton.setOnAction(event ->
        {String url = urlField.getText().trim();
            Downloader.directDownload(url, osType, logOutput);
        });

        // 開始解析按鈕
        Button StartFormatButton = new Button("開始解析");
        StartFormatButton.setStyle("-fx-background-color: #ff69b4; -fx-text-fill: white; -fx-font-size: 14px;");
        StartFormatButton.setOnAction(event ->
        {String url = urlField.getText().trim();
            URLParser. handleUrlAction(url, osType, logOutput, formatCombo, qualityCombo);
        });

        Button BCBotton= new Button("最佳下載組合");
        BCBotton.setStyle("-fx-background-color: #ff69b4; -fx-text-fill: white; -fx-font-size: 14px;");
        BCBotton.setOnAction(event ->
        {String url = urlField.getText().trim();
            Downloader.BestDownloadVideo(url, osType, logOutput);
        });

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


        Region Spacerfordownload = new Region();
        Spacerfordownload.setPrefWidth(135);
        HBox DownLoadBox = new HBox(10);
        DownLoadBox.getChildren().addAll(

                DDButton,
                StartFormatButton,
                BCBotton

        );
        DownLoadBox.setStyle("-fx-padding: 5;");


        logOutput.setStyle("-fx-font-size: 18px; " +
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
                DownLoadBox,
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
