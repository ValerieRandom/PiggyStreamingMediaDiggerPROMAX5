import UI.UIBuilder;
import Util.SystemUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 檢測操作系統
        String osType = SystemUtils.detectOS();
        if (osType.equals("unsupported")) {
            SystemUtils.showAlert("錯誤", "小豬目前不支援此作業系統！");
            return;
        }

        // 確保工具存在
        try {
            SystemUtils.ensureDependencies(osType);
        } catch (RuntimeException e) {
            SystemUtils.showAlert("錯誤", e.getMessage());
            return;
        }

        // 建立一個共用的 TextArea 來顯示日誌
        TextArea logOutput = new TextArea();
        logOutput.setEditable(false);
        VBox.setVgrow(logOutput, Priority.ALWAYS);
        VBox root = UIBuilder.buildUI(osType, logOutput);

        // 顯示視窗
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setTitle("大壞豬影音挖掘機 PRO - MAX 超強五代");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}