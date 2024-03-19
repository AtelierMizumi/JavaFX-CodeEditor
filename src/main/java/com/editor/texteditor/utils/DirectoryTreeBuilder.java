package com.editor.texteditor.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DirectoryTreeBuilder {
    public TreeItem<String> createNode(final File file) {
        return new TreeItem<>(file.getAbsolutePath()) {
            private boolean isLeaf;
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public ObservableList<TreeItem<String>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;
                    try {
                        super.getChildren().setAll(buildChildren(this).get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
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

            private CompletableFuture<ObservableList<TreeItem<String>>> buildChildren(TreeItem<String> treeItem) {
                return CompletableFuture.supplyAsync(() -> {
                    File f = new File(file.getPath());
                    if (f.isDirectory()) {
                        File[] files = f.listFiles();
                        if (files != null) {
                            // Sort the files array alphabetically
                            Arrays.sort(files, Comparator.comparing(File::getName));
                            ObservableList<TreeItem<String>> children = FXCollections.observableArrayList();
                            for (File childFile : files) {
                                children.add(createNode(childFile));
                            }
                            return children;
                        }
                    }
                    return FXCollections.emptyObservableList();
                });
            }
        };
    }
}