package com.editor.texteditor;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.Objects;

public class FileIconProvider {
    public ImageView getIcon(File file) {
        ImageView icon;
        String fileType = getFileExtension(file);

        switch (fileType) {
            case "txt":
                icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/extension-icons/text-file-icon.png"))));
                break;
            case "png":
            case "jpg":
            case "jpeg":
                icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/extension-icons/image-file-icon.png"))));
                break;
            case "cpp":
                icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/extension-icons/cpp-file-icon.png"))));
                break;
            case "java":
                icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/extension-icons/java-file-icon.png"))));
                break;
            default:
                if (file.isDirectory()) {
                    icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/extension-icons/directory-icon.png"))));
                } else {
                    icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/extension-icons/default-file-icon.png"))));
                }
                break;
        }

        icon.setFitHeight(16);
        icon.setFitWidth(16);
        return icon;
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}