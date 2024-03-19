package com.editor.texteditor.syntax;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxUtils {

    public static final String[] KEYWORDS = new String[]{
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "false", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "null", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "true", "this", "throw", "throws",
            "transient", "try", "var", "void", "volatile", "while"
    };

    public static final List<String> KEYWORDS_lIST = new ArrayList<>();
    public static final HashMap<String,String> CLASSES_LIST = new HashMap<>();

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String CAST_PATTERN = "<[a-zA-Z0-9,<>]+>";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    public static final String TODO_SINGLE_COMMENT_PATTERN = "//TODO[^\n]*";
    public static final String WARN_SINGLE_COMMENT_PATTERN = "//WARN[^\n]*";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    private static final String ANNOTATION_PATTERN = "@.[a-zA-Z0-9]+";
    private static final String OPERATION_PATTERN = ":|==|>|<|!=|>=|<=|->|=|>|<|%|-|-=|%=|\\+|\\-|\\-=|\\+=|\\^|\\&|\\|::|\\?|\\*";
    private static final String HEX_PATTERN = "#[a-fA-F0-9]+";
    private static final String NUMBERS_PATTERN = "[0-9]+";
    private static final String METHOD_PATTERN = "\\.[a-zA-Z0-9_]+";

    public static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<TODO>" + TODO_SINGLE_COMMENT_PATTERN + ")"
                    + "|(?<WARN>" + WARN_SINGLE_COMMENT_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<ANNOTATION>" + ANNOTATION_PATTERN + ")"
                    + "|(?<CAST>" + CAST_PATTERN + ")"
                    + "|(?<OPERATION>" + OPERATION_PATTERN + ")"
                    + "|(?<HEX>" + HEX_PATTERN + ")"
                    + "|(?<NUMBER>" + NUMBERS_PATTERN + ")"
                    + "|(?<METHOD>" + METHOD_PATTERN + ")"
    );

    public static StyleSpans<Collection<String>> computeHighlighting(String newText, String language) {
        Matcher matcher = PATTERN.matcher(newText);
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
        spansBuilder.add(Collections.emptyList(), newText.length() - lastKwEnd);
        return spansBuilder.create();
    }
}