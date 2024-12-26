import com.xxx.UI.UIBuilder;
import com.xxx.Util.SystemUtils;
import javafx.application.Application;
import javafx.scene.Scene;
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

        // 使用 UIBuilder 建立 Scene
        Scene scene = UIBuilder.buildRoot(osType);

        // 顯示視窗
        primaryStage.setScene(scene);
        primaryStage.setTitle("大壞豬影音挖掘機 PRO - MAX 超強五代");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
