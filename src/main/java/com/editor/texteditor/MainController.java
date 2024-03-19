package com.editor.texteditor;

import com.editor.texteditor.utils.DirectoryTreeBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
public class MainController {
    @FXML
    private TextArea terminalArea;
    @FXML
    private TreeView<String> directoryTree;
    @FXML
    private TabPane codeEditorTabPane;
    @FXML
    private TabPane terminalTabPane;

    @FXML

    public void initialize() {
        CodeEditor codeEditor = new CodeEditor();

        File rootDirectory = new File("/home/thuanc177");
        DirectoryTreeBuilder directoryTreeBuilder = new DirectoryTreeBuilder();
        TreeItem<String> rootNode = directoryTreeBuilder.createNode(rootDirectory);
        directoryTree.setRoot(rootNode);

        directoryTree.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // double-click
                System.out.println("Double-clicked on the directory tree");
                TreeItem<String> selectedItem = directoryTree.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    File selectedFile = new File(selectedItem.getValue());
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                    if (selectedFile.isFile()) {
                        Path filePath = Paths.get(selectedFile.getAbsolutePath());
                        System.out.println("File path: " + filePath); // print the file path
                        codeEditor.openFileInNewTab(filePath, codeEditorTabPane);
                    }
                }
            }
        });
    }

    @FXML
    public void openFolderOnAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource File");
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            File rootDirectory = new File(selectedDirectory.getAbsolutePath());
            DirectoryTreeBuilder directoryTreeBuilder = new DirectoryTreeBuilder();
            TreeItem<String> rootNode = directoryTreeBuilder.createNode(rootDirectory);
            directoryTree.setRoot(rootNode);
        }
    }
}