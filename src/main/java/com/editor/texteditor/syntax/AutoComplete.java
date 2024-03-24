package com.editor.texteditor.syntax;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.fxmisc.richtext.CodeArea;

import com.editor.texteditor.CodeEditor;
import java.util.List;
import java.util.stream.Collectors;

public class AutoComplete {
    private final ListView<String> listView = new ListView<>();
    private final List<String> suggestions = FXCollections.observableArrayList();

    public AutoComplete(CodeEditor codeArea) {
        listView.setPrefWidth(200);
        listView.setPrefHeight(200);
        listView.setStyle("-fx-background-color: #2b2b2b; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: 'Fira Code';");
        listView.setOnMouseClicked(event -> {
            if (listView.getSelectionModel().getSelectedItem() != null) {
                String selected = listView.getSelectionModel().getSelectedItem();
                int currentWordLength = codeArea.getCurrentWord().length();
                codeArea.replaceText(codeArea.getCaretPosition() - currentWordLength, codeArea.getCaretPosition(), selected);
                listView.setVisible(false);
            }
        });

        codeArea.caretPositionProperty().addListener((observable, oldValue, newValue) -> {
            if (!codeArea.getText().isEmpty()) {
                String currentWord = codeArea.getCurrentWord();
                if (currentWord.length() > 0) {
                    suggestions.clear();
                    suggestions.addAll(SyntaxUtils.KEYWORDS_lIST.stream().filter(s -> s.startsWith(currentWord)).collect(Collectors.toList()));
                    listView.setItems((ObservableList<String>) suggestions);
                    if (suggestions.size() > 0) {
                        listView.setVisible(true);
                    } else {
                        listView.setVisible(false);
                    }
                } else {
                    listView.setVisible(false);
                }
            } else {
                listView.setVisible(false);
            }
        });
    }

    public ListView<String> getListView() {
        return listView;
    }
}