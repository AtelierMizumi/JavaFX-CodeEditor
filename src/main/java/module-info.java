module com.editor.texteditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.fontawesome;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires java.logging;
    requires reactfx;
    requires com.kodedu.terminalfx;
    requires org.slf4j;
    requires com.sun.jna;
    requires pty4j;

    opens com.editor.texteditor to javafx.fxml;
    exports com.editor.texteditor;
    exports com.editor.texteditor.utils;
    opens com.editor.texteditor.utils to javafx.fxml;
    exports com.editor.texteditor.syntax;
    opens com.editor.texteditor.syntax to javafx.fxml;
}