module com.xxx.piggystreamingmediadiggerpromax5 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens com.xxx.BottonsBehavior to javafx.fxml;
    exports com.xxx.BottonsBehavior;
    exports;
    opens to
    exports Utils;
    opens Utils to javafx.fxml;
    exports;
    opens to
}