module com.xxx.piggystreamingmediadiggerpromax5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    // 对外导出的包
    exports com.xxx.BottonsBehavior;
    exports com.xxx.UI;
    exports com.xxx.Util;

    // 对 javafx.fxml 开放的包
    opens com.xxx.BottonsBehavior to javafx.fxml;
    opens com.xxx.UI to javafx.fxml;
    opens com.xxx.Util to javafx.fxml;
}
