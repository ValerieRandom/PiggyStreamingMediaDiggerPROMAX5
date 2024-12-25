module com.xxx.piggystreamingmediadiggerpromax5 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens com.xxx.piggystreamingmediadiggerpromax5 to javafx.fxml;
    exports com.xxx.piggystreamingmediadiggerpromax5;
}