module steamTables {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.media;
    requires transitive javafx.graphics;
    requires org.apache.poi.ooxml;
    requires java.desktop;
    requires java.logging;

    exports app;
    exports gui;
    opens app to javafx.fxml;
}
