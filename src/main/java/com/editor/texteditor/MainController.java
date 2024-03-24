package com.editor.texteditor;

import com.editor.texteditor.utils.DirectoryTreeBuilder;
import com.kodedu.terminalfx.TerminalBuilder;
import com.kodedu.terminalfx.TerminalTab;
import com.kodedu.terminalfx.config.TerminalConfig;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
public class MainController {
    @FXML
    private TreeView<File> directoryTree;
    @FXML
    private TabPane codeEditorTabPane;
    @FXML
    private TabPane terminalTabPane;

    @FXML

    public void initialize() {
        CodeEditor codeEditor = new CodeEditor();

        File rootDirectory = new File("/home/thuanc177/Documents");
        DirectoryTreeBuilder directoryTreeBuilder = new DirectoryTreeBuilder();
        TreeItem<File> rootNode = directoryTreeBuilder.createNode(rootDirectory);
        directoryTree.setRoot(rootNode);

        directoryTree.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "" : item.getName());
            }
        });

        directoryTree.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // double-click
                System.out.println("Double-clicked on the directory tree");
                TreeItem<File> selectedItem = directoryTree.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    File selectedFile = new File(String.valueOf(selectedItem.getValue()));
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                    if (selectedFile.isFile()) {
                        Path filePath = Paths.get(selectedFile.getAbsolutePath());
                        System.out.println("File path: " + filePath); // print the file path
                        codeEditor.openFileInNewTab(filePath, codeEditorTabPane);
                    }
                }
            }
        });

        TerminalConfig darkConfig = new TerminalConfig();
        darkConfig.setBackgroundColor(Color.rgb(30, 32, 48));
        darkConfig.setForegroundColor(Color.rgb(230, 248, 255));
        darkConfig.setCursorColor(Color.rgb(230, 45, 80));

        TerminalBuilder terminalBuilder = new TerminalBuilder(darkConfig);
        TerminalTab terminal = terminalBuilder.newTerminal();

        terminalTabPane.getTabs().add(terminal);
        terminal.onTerminalFxReady(() -> {
            terminal.getTerminal().command("ls -l");
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
            TreeItem<File> rootNode = directoryTreeBuilder.createNode(rootDirectory);
            directoryTree.setRoot(rootNode);
        }
    }
}