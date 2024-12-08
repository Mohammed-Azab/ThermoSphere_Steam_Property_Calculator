module steamTables {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires org.apache.poi.ooxml;

    exports app;
    exports gui;
    opens app to javafx.fxml;
}
