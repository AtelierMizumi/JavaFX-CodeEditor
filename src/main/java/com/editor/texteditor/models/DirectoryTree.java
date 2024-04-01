package com.editor.texteditor.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DirectoryTree {

    public DirectoryTree() {}

    public TreeItem<File> createNode(final File file) {
        return new TreeItem<>(file) {
            private boolean isLeaf;
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public ObservableList<TreeItem<File>> getChildren() {
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

            private CompletableFuture<ObservableList<TreeItem<File>>> buildChildren(TreeItem<File> treeItem) {
                return CompletableFuture.supplyAsync(() -> {
                    File f = treeItem.getValue();
                    if (f.isDirectory()) {
                        File[] files = f.listFiles();
                        if (files != null) {
                            Arrays.sort(files, Comparator.comparing(File::getName));
                            ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();
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