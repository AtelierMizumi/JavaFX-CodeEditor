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
    requires batik.transcoder;

    opens com.editor.texteditor to javafx.fxml;
    exports com.editor.texteditor;
    exports com.editor.texteditor.others.utils;
    opens com.editor.texteditor.others.utils to javafx.fxml;
    exports com.editor.texteditor.others.syntax;
    opens com.editor.texteditor.others.syntax to javafx.fxml;
    exports com.editor.texteditor.controllers;
    opens com.editor.texteditor.controllers to javafx.fxml;
    exports com.editor.texteditor.models;
    opens com.editor.texteditor.models to javafx.fxml;
}