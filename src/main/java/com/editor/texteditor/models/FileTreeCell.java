package com.editor.texteditor.models;

import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class FileTreeCell extends TreeCell<File> {
    private final ImageView folderIcon = new ImageView(new Image(getClass().getResourceAsStream("/folder-icon.png")));
    private final ImageView fileIcon = new ImageView(new Image(getClass().getResourceAsStream("/file-icon.png")));

    @Override
    protected void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getName());
            setGraphic(item.isDirectory() ? folderIcon : fileIcon);
        }
    }
}
