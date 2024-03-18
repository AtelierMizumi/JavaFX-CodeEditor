module com.editor.texteditor {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.editor.texteditor to javafx.fxml;
    exports com.editor.texteditor;
}