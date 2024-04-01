package com.editor.texteditor.models;

import com.editor.texteditor.others.syntax.AutoComplete;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.editor.texteditor.others.syntax.SyntaxUtils.computeHighlighting;
import static javafx.geometry.Pos.CENTER_LEFT;

public class CodeEditor extends CodeArea {
    public CodeEditor() {
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));
        this.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())).subscribe(change -> {
            this.setStyleSpans(0, computeHighlighting(this.getText()));
        });
        // auto complete

    }
    public void openFileInNewTab(Path filePath, TabPane codeEditorTabPane) {
        // check if the file is already opened
        for (Tab tab : codeEditorTabPane.getTabs()) {
            VirtualizedScrollPane vsPane = (VirtualizedScrollPane) tab.getContent();
            CodeEditor codeArea = (CodeEditor) vsPane.getContent();
            codeArea.textProperty().addListener((obs, oldText, newText) -> {
                codeArea.setStyleSpans(0, computeHighlighting(newText));
            });
        }
        try {
            String content = Files.readString(filePath);
            String fileExtension = getFileExtension(filePath.toString());

            CodeEditor codeArea = new CodeEditor(); // create a new CodeEditor instance
            applySyntaxHighlighting(codeArea, fileExtension);
            codeArea.replaceText(0, 0, content);

            // Listen for key press events
            codeArea.setOnKeyPressed(event -> handleKeyPress(event, filePath, codeArea));

            Tab newTab = createTab(filePath, codeArea);
            codeEditorTabPane.getTabs().add(newTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
    private void applySyntaxHighlighting(CodeEditor codeArea, String fileExtension) {
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
    private static final Map<String, String[]> KEYWORDS = new HashMap<String, String[]>() {{
        put("java", new String[]{
                "abstract", "assert", "boolean", "break", "byte",
                "case", "catch", "char", "class", "const",
                "continue", "default", "do", "double", "else",
                "enum", "extends", "final", "finally", "float",
                "for", "goto", "if", "implements", "import",
                "instanceof", "int", "interface", "long", "native",
                "new", "package", "private", "protected", "public",
                "return", "short", "static", "strictfp", "super",
                "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while",
                // HTML tags
        });
        put("html", new String[]{
                "a", "abbr", "acronym", "address", "applet", "area", "article", "aside", "audio",
                "b", "base", "basefont", "bdi", "bdo", "big", "blockquote", "body", "br", "button",
                "canvas", "caption", "center", "cite", "code", "col", "colgroup", "data", "datalist", "dd",
                "del", "details", "dfn", "dialog", "dir", "div", "dl", "dt", "em", "embed", "fieldset",
                "figcaption", "figure", "font", "footer", "form", "frame", "frameset", "h1", "h2", "h3",
                "h4", "h5", "h6", "head", "header", "hr", "html", "i", "iframe", "img", "input", "ins",
                "kbd", "label", "legend", "li", "link", "main", "map", "mark", "meta", "meter", "nav",
                "noframes", "noscript", "object", "ol", "optgroup", "option", "output", "p", "param",
                "picture", "pre", "progress", "q", "rp", "rt", "ruby", "s", "samp", "script", "section",
                "select", "small", "source", "span", "strike", "strong", "style", "sub", "summary", "sup",
                "svg", "table", "tbody", "td", "template", "textarea", "tfoot", "th", "thead", "time",
                "title", "tr", "track", "tt", "u", "ul", "var", "video", "wbr",
        });
        put("css", new String[]{
                "align-content", "align-items", "align-self", "all", "animation", "animation-delay", "animation-direction", "animation-duration", "animation-fill-mode", "animation-iteration-count",
                "animation-name", "animation-play-state", "animation-timing-function", "backface-visibility", "background", "background-attachment", "background-blend-mode", "background-clip", "background-color", "background-image",
                "background-origin", "background-position", "background-repeat", "background-size", "border", "border-bottom", "border-bottom-color", "border-bottom-left-radius", "border-bottom-right-radius", "border-bottom-style",
                "border-bottom-width", "border-collapse", "border-color", "border-image", "border-image-outset", "border-image-repeat", "border-image-slice", "border-image-source", "border-image-width", "border-left",
                "border-left-color", "border-left-style", "border-left-width", "border-radius", "border-right", "border-right-color", "border-right-style", "border-right-width", "border-spacing", "border-style",
                "border-top", "border-top-color", "border-top-left-radius", "border-top-right-radius", "border-top-style", "border-top-width", "border-width", "bottom", "box-shadow", "box-sizing", "break-after",
                "break-before", "break-inside", "caption-side", "caret-color", "clear", "clip", "color", "column-count", "column-fill", "column-gap", "column-rule", "column-rule-color",
                "column-rule-style", "column-rule-width", "column-span", "column-width", "columns", "content", "counter-increment", "counter-reset", "cursor", "direction", "display", "empty-cells",
                "filter", "flex", "flex-basis", "flex-direction", "flex-flow", "flex-grow", "flex-shrink", "flex-wrap", "float", "font", "font-family", "font-feature-settings", "font-kerning",
                "font-language-override", "font-size", "font-size-adjust", "font-stretch", "font-style", "font-variant", "font-variant-alternates", "font-variant-caps", "font-variant-east-asian", "font-variant-ligatures",
                "font-variant-numeric", "font-variant-position", "font-weight", "grid", "grid-area", "grid-auto-columns", "grid-auto-flow", "grid-auto-rows", "grid-column", "grid-column-end", "grid-column-gap",
                "grid-column-start", "grid-gap", "grid-row", "grid-row-end", "grid-row-gap", "grid-row-start", "grid-template", "grid-template-areas", "grid-template-columns", "grid-template-rows", "hanging-punctuation",
                "height", "hyphens", "image-rendering", "justify-content", "left", "letter-spacing", "line-height", "list-style", "list-style-image", "list-style-position", "list-style-type", "margin",
                "margin-bottom", "margin-left", "margin-right", "margin-top", "max-height", "max-width", "min-height", "min-width", "mix-blend-mode", "object-fit", "object-position", "opacity",
                "order", "orphans", "outline", "outline-color", "outline-offset", "outline-style", "outline-width", "overflow", "overflow-x", "overflow-y", "padding", "padding-bottom", "padding-left",
                "padding-right", "padding-top", "page-break-after", "page-break-before", "page-break-inside", "perspective", "perspective-origin", "pointer-events", "position", "quotes", "resize", "right",
                "scroll-behavior", "tab-size", "table-layout", "text-align", "text-align-last", "text-combine-upright", "text-decoration", "text-decoration-color", "text-decoration-line", "text-decoration-style", "text-indent",
                "text-justify", "text-orientation", "text-overflow", "text-rendering", "text-shadow", "text-transform", "text-underline-position", "top", "transform", "transform-origin", "transform-style", "transition",
                "transition-delay", "transition-duration", "transition-property", "transition-timing-function", "unicode-bidi", "vertical-align", "visibility", "white-space", "widows", "width", "will-change", "word-break",
                "word-spacing", "word-wrap", "writing-mode", "z-index",
        });
        put("c", new String[]{
                "auto", "double", "int", "struct", "break", "else", "long", "switch",
                "case", "enum", "register", "typedef", "char", "extern", "return", "union",
                "const", "float", "short", "unsigned", "continue", "for", "signed", "void",
                "default", "goto", "sizeof", "volatile", "do", "if", "static", "while",
                "inline", "bool", "true", "false", "asm", "dynamic_cast", "namespace", "reinterpret_cast",
                "try", "catch", "typeid", "typename", "template", "explicit", "friend", "virtual",
                "using", "mutable", "nullptr", "static_cast", "constexpr", "thread_local", "alignas",
                "alignof", "static_assert", "noexcept", "override", "final", "decltype"
        });
        put("cpp", new String[]{
                "auto", "double", "int", "struct", "break", "else", "long", "switch",
                "case", "enum", "register", "typedef", "char", "extern", "return", "union",
                "const", "float", "short", "unsigned", "continue", "for", "signed", "void",
                "default", "goto", "sizeof", "volatile", "do", "if", "static", "while",
                "inline", "bool", "true", "false", "asm", "dynamic_cast", "namespace", "reinterpret_cast",
                "try", "catch", "typeid", "typename", "template", "explicit", "friend", "virtual",
                "using", "mutable", "nullptr", "static_cast", "constexpr", "thread_local", "alignas",
                "alignof", "static_assert", "noexcept", "override", "final", "decltype"
        });
        put("default", new String[]{
                "if", "else", "for", "while", "do", "switch", "case", "break", "continue", "return",
                "class", "function", "var", "let", "const", "new", "this", "try", "catch", "throw"
        });
        // ... add more languages as needed
    }};

    private void setupAutoCompletion(CodeEditor codeArea, String language) {
        AutoComplete autoComplete = new AutoComplete(codeArea);
        codeArea.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case TAB:
                    event.consume();
                    break;
                case ENTER:
                    autoComplete.getListView().setVisible(false);
                    break;
                default:
                    break;
            }
        });
    }

    private void applySyntaxHighlightingWithUtils(CodeEditor codeArea, String language) {
        String[] keywordsForLanguage = KEYWORDS.getOrDefault(language, KEYWORDS.get("default"));
        if (keywordsForLanguage != null) {
            codeArea.textProperty().addListener((obs, oldText, newText) -> {
                StyleSpans<Collection<String>> highlighting = computeHighlighting(newText);
                codeArea.setStyleSpans(0, highlighting);
            });
            setupAutoCompletion(codeArea, language);
        }
    }

    public String getCurrentWord() {
        int caretPosition = this.getCaretPosition();
        String text = this.getText();
        int start = caretPosition;
        while (start > 0 && !Character.isWhitespace(text.charAt(start - 1))) {
            start--;
        }
        return text.substring(start, caretPosition);
    }

    public int getCurrentWordLength() {
        int caretPosition = (int) this.getScene().getWindow().getX();
        int lastSpace = this.getText().substring(0, caretPosition).lastIndexOf(" ");
        return caretPosition - lastSpace;
    }
}