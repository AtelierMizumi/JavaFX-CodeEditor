package com.editor.texteditor.models;

import java.io.File;

public class Source {

    private File sourceFile;

    public Source(File source) {
        sourceFile = source;
    }

    public String getName() {
        return sourceFile.getName();
    }

    public String getPath() {
        return sourceFile.getPath();
    }

    public File getFile(){
        return sourceFile;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Source source){
            return source.getPath().equals(getPath());
        }
        return false;
    }
}