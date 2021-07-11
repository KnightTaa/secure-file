package com.example.securefils;

public class FileModel {

    private String fileUrl;

    public FileModel(){

    }

    public FileModel(String fileUrl){
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
