package com.editor.texteditor;

import com.editor.texteditor.syntax.SyntaxUtils;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.geometry.Pos.CENTER_LEFT;

public class CodeEditor {
    public void openFileInNewTab(Path filePath, TabPane codeEditorTabPane) {
        // check if the file is already opened
        for (Tab tab : codeEditorTabPane.getTabs()) {
            VirtualizedScrollPane vsPane = (VirtualizedScrollPane) tab.getContent();
            CodeArea codeArea = (CodeArea) vsPane.getContent();
            codeArea.textProperty().addListener((obs, oldText, newText) -> {
                codeArea.setStyleSpans(0, computeHighlighting(newText));
            });
        }
        try {
            String content = Files.readString(filePath);
            String fileExtension = getFileExtension(filePath.toString());

            CodeArea codeArea = new CodeArea();
            applySyntaxHighlighting(codeArea, fileExtension);
            codeArea.replaceText(0, 0, content);

            // Listen for key press events
            codeArea.setOnKeyPressed(event -> handleKeyPress(event, filePath, codeArea));

            Tab newTab = createTab(filePath, codeArea);
            codeEditorTabPane.getTabs().add(newTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applySyntaxHighlighting(CodeArea codeArea, String fileExtension) {
        System.out.println("File extension: " + fileExtension);
        Map<String, Runnable> syntaxHighlightingStrategies = new HashMap<>();
        syntaxHighlightingStrategies.put("java", () -> applySyntaxHighlightingWithUtils(codeArea, "java"));
        syntaxHighlightingStrategies.put("xml", () -> applySyntaxHighlightingWithUtils(codeArea, "xml"));
        syntaxHighlightingStrategies.put("cpp", () -> applySyntaxHighlightingWithUtils(codeArea, "cpp"));
        syntaxHighlightingStrategies.put("html", () -> applySyntaxHighlightingWithUtils(codeArea, "html"));
        syntaxHighlightingStrategies.put("css", () -> applySyntaxHighlightingWithUtils(codeArea, "css"));
        syntaxHighlightingStrategies.put("c", () -> applySyntaxHighlightingWithUtils(codeArea, "c"));
        syntaxHighlightingStrategies.put("sh", () -> applySyntaxHighlightingWithUtils(codeArea, "sh"));
        // Add more cases as needed

        Runnable syntaxHighlightingStrategy = syntaxHighlightingStrategies.get(fileExtension);
        if (syntaxHighlightingStrategy != null) {
            syntaxHighlightingStrategy.run();
        }
    }


    private void handleKeyPress(KeyEvent event, Path filePath, CodeArea codeArea) {
        if (event.isControlDown()) {
            switch (event.getCode()) {
                case S:
                    saveFile(filePath, codeArea);
                    break;
                case O:
                    break;
                // Add more cases as needed
            }
        }
    }

    private Tab createTab(Path filePath, CodeArea codeArea) {
        Tab newTab = new Tab(filePath.getFileName().toString());
        newTab.setContent(new VirtualizedScrollPane<>(codeArea));

        HBox tabHeader = new HBox();
        tabHeader.setAlignment(CENTER_LEFT);
        Label titleLabel = new Label(filePath.getFileName().toString());
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: #000;");
        closeButton.setOnAction(event -> handleTabClose(filePath, codeArea, newTab));

        tabHeader.getChildren().setAll(titleLabel, closeButton);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        newTab.setGraphic(tabHeader);
        newTab.setText("");

        return newTab;
    }

    private void handleTabClose(Path filePath, CodeArea codeArea, Tab newTab) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Do you want to save changes to " + filePath.getFileName() + "?");

        ButtonType buttonTypeSave = new ButtonType("Save");
        ButtonType buttonTypeDontSave = new ButtonType("Don't Save");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeDontSave, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType == buttonTypeSave) {
                saveFile(filePath, codeArea);
            }
            if (buttonType == buttonTypeSave || buttonType == buttonTypeDontSave) {
                newTab.getTabPane().getTabs().remove(newTab);
            }
        });
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    public void saveFile(Path filePath, CodeArea codeArea) {
        try {
            Files.writeString(filePath, codeArea.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applySyntaxHighlightingWithUtils(CodeArea codeArea, String language) {
        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            StyleSpans<Collection<String>> highlighting = SyntaxUtils.computeHighlighting(newText, language);
            codeArea.setStyleSpans(0, highlighting);
        });
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = SyntaxUtils.PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("TODO") != null ? "todo" :
                    matcher.group("WARN") != null ? "warn" :
                    matcher.group("COMMENT") != null ? "comment" :
                    matcher.group("ANNOTATION") != null ? "annotation" :
                    matcher.group("CAST") != null ? "cast" :
                    matcher.group("OPERATION") != null ? "operation" :
                    matcher.group("HEX") != null ? "hex" :
                    matcher.group("NUMBER") != null ? "number" :
                    matcher.group("METHOD") != null ? "method" :
                    null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}