package com.editor.texteditor.controllers;

import com.editor.texteditor.models.CodeEditor;
import com.editor.texteditor.models.DirectoryTree;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.stage.DirectoryChooser;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

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
        DirectoryTree directoryTree = new DirectoryTree();
        TreeItem<File> rootNode = directoryTree.createNode(rootDirectory);
        this.directoryTree.setRoot(rootNode);

        this.directoryTree.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY){
                // contentmenu pop up here
            }
            if (event.getClickCount() == 2) { // double-click
                System.out.println("Double-clicked on the directory tree");
                TreeItem<File> selectedItem = this.directoryTree.getSelectionModel().getSelectedItem();
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

        MenuItem menuItem1 = new MenuItem("New File");
        MenuItem menuItem2 = new MenuItem("Delete");

        ContextMenu contextMenu = new ContextMenu(menuItem1, menuItem2);

        menuItem1.setOnAction(event -> {
            TreeItem<File> selectedItem = this.directoryTree.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getValue().isDirectory()) {
                try {
                    Files.createFile(Paths.get(selectedItem.getValue().getAbsolutePath(), "newFile.txt"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        menuItem2.setOnAction(event -> {
            TreeItem<File> selectedItem = this.directoryTree.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getValue().isFile()) {
                try {
                    Files.delete(selectedItem.getValue().toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        this.directoryTree.setCellFactory(tv -> {
            TreeCell<File> cell = new TreeCell<>() {
                private final ImageView cIcon = loadSvgIcon("assets/file-icons/c.svg");
                private final ImageView cSharpIcon = loadSvgIcon("assets/file-icons/csharp.svg");
                private final ImageView imageIcon = loadSvgIcon("assets/file-icons/image.svg");
                private final ImageView javaIcon = loadSvgIcon("assets/file-icons/java.svg");
                private final ImageView cppIcon = loadSvgIcon("assets/file-icons/cpp.svg");
                private final ImageView htmlIcon = loadSvgIcon("assets/file-icons/html.svg");
                private final ImageView cssIcon = loadSvgIcon("assets/file-icons/css.svg");
                private final ImageView folderIcon = loadSvgIcon("assets/file-icons/folder.svg");
                private final ImageView defaultIcon = loadSvgIcon("assets/file-icons/file.svg");

                @Override
                protected void updateItem(File item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item.getName());
                        if (item.isDirectory()) {
                            setGraphic(folderIcon);
                        } else {
                            String fileName = item.getName().toLowerCase();
                            if (fileName.endsWith(".c")) {
                                setGraphic(cIcon);
                            } else if (fileName.endsWith(".cs")) {
                                setGraphic(cSharpIcon);
                            } else if (fileName.endsWith(".cpp")) {
                                setGraphic(cppIcon);
                            } else if (fileName.endsWith(".html")) {
                                setGraphic(htmlIcon);
                            } else if (fileName.endsWith(".css")) {
                                setGraphic(cssIcon);
                            } else if (fileName.endsWith(".java")) {
                                setGraphic(javaIcon);
                            } else if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                                setGraphic(imageIcon);
                            } else {
                                setGraphic(defaultIcon);
                            }
                        }
                    }
                }
            };

            cell.setOnContextMenuRequested(event -> {
                contextMenu.show(cell, event.getScreenX(), event.getScreenY());
            });

            return cell;
        });

//        directoryTree.setOnContextMenuRequested(event -> {
//            TreeItem<File> selectedItem = this.directoryTree.getSelectionModel().getSelectedItem();
//            if (selectedItem != null) {
//                contextMenu.show(directoryTree, event.getScreenX(), event.getScreenY());
//            }
//        });

//        TerminalConfig darkConfig = new TerminalConfig();
//        darkConfig.setBackgroundColor(Color.rgb(30, 32, 48));
//        darkConfig.setForegroundColor(Color.rgb(230, 248, 255));
//        darkConfig.setCursorColor(Color.rgb(230, 45, 80));
//
//        TerminalBuilder terminalBuilder = new TerminalBuilder(darkConfig);
//        TerminalTab terminal = terminalBuilder.newTerminal();
//
//        terminalTabPane.getTabs().add(terminal);
//        terminal.onTerminalFxReady(() -> {
//            terminal.getTerminal().command("ls -l");
//        });
    }

    @FXML
    public void openFolderOnAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource File");
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            File rootDirectory = new File(selectedDirectory.getAbsolutePath());
            DirectoryTree directoryTree = new DirectoryTree();
            TreeItem<File> rootNode = directoryTree.createNode(rootDirectory);
            this.directoryTree.setRoot(rootNode);
        }
    }

    private ImageView loadSvgIcon(String filePath) {
        try {
            InputStream svgFileStream = getClass().getClassLoader().getResourceAsStream(filePath);
            if (svgFileStream == null) {
                System.out.println("SVG file not found: " + filePath);
                return null;
            }
            TranscoderInput input = new TranscoderInput(svgFileStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outputStream);
            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.transcode(input, output);
            byte[] pngBytes = outputStream.toByteArray();
            Image image = new Image(new ByteArrayInputStream(pngBytes));
            return new ImageView(image);
        } catch (TranscoderException e) {
            System.out.println("Failed to load SVG icon: " + filePath);
            e.printStackTrace();
            return null;
        }
    }
}