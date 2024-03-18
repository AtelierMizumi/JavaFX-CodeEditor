package com.editor.texteditor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.Objects;

public class MainController {
    @FXML
    private Label welcomeText;
    @FXML
    private TextArea terminalArea;
    @FXML
    private TextArea codeEditorArea;
    @FXML
    private TreeView<String> directoryTree;

    @FXML
    public void initialize() {
        File rootDirectory = new File("/home/thuanc177/");
        TreeItem<String> rootNode = createNode(rootDirectory);
        directoryTree.setRoot(rootNode);
    }


    private TreeItem<String> createNode(final File file) {
        ImageView icon;
        String fileType = getFileExtension(file);

        switch (fileType) {
            case "txt":
                icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/text-file-icon.png"))));
                break;
            case "png":
            case "jpg":
            case "jpeg":
                icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image-file-icon.png"))));
                break;
            case "cpp":
                icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/cpp-file-icon.png"))));
                break;
            case "java":
                icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/java-file-icon.png"))));
                break;
            default:
                if (file.isDirectory()) {
                    icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/directory-icon.png"))));
                } else {
                    icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/default-file-icon.png"))));
                }
                break;
        }

        icon.setFitHeight(16);
        icon.setFitWidth(16);

        TreeItem<String> node = new TreeItem<>(file.getName(), icon);
        return new TreeItem<String>(file.getName()) {
            private boolean isLeaf;
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public ObservableList<TreeItem<String>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;
                    super.getChildren().setAll(buildChildren(this));
                }
                return super.getChildren();
            }

            @Override
            public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    isLeaf = file.isFile();
                }
                return isLeaf;
            }

            private ObservableList<TreeItem<String>> buildChildren(TreeItem<String> treeItem) {
                File f = new File(file.getPath());
                if (f.isDirectory()) {
                    File[] files = f.listFiles();
                    if (files != null) {
                        ObservableList<TreeItem<String>> children = FXCollections.observableArrayList();
                        for (File childFile : files) {
                            children.add(createNode(childFile));
                        }
                        return children;
                    }
                }
                return FXCollections.emptyObservableList();
            }
        };
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    @FXML
    protected void onExecuteButtonClick() {
        String command = codeEditorArea.getText();
        TerminalEmulator terminalEmulator = new TerminalEmulator(terminalArea);
        terminalEmulator.executeCommand(command);
    }

    @FXML
    protected void onDirectoryTreeClick() {
        TreeItem<String> selectedItem = directoryTree.getSelectionModel().getSelectedItem();
        String selectedDirectory = selectedItem.getValue();
        terminalArea.appendText("\n" + selectedDirectory);
    }
    @FXML
    protected void onDirectoryTreeDoubleClick() {
        TreeItem<String> selectedItem = directoryTree.getSelectionModel().getSelectedItem();
        String selectedDirectory = selectedItem.getValue();
        terminalArea.appendText("\n" + selectedDirectory);
    }
    @FXML
    protected void onDirectoryTreeEnter() {
        TreeItem<String> selectedItem = directoryTree.getSelectionModel().getSelectedItem();
        String selectedDirectory = selectedItem.getValue();
        terminalArea.appendText("\n" + selectedDirectory);
    }
    @FXML
    protected void openFolderOnAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            TreeItem<String> rootNode = createNode(selectedDirectory);
            directoryTree.setRoot(rootNode);
        }
    }
    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}