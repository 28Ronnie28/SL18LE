package models.all;

import java.io.Serializable;

public class ClassFile implements Serializable{

    private int classID;
    private String fileName;
    private int fileLength;
    private int value;
    private Object fileDownloader;

    public ClassFile(int classID, String fileName, int fileLength) {
        this.classID = classID;
        this.fileName = fileName;
        this.fileLength = fileLength;
    }

    public int getClassID() {
        return classID;
    }

    public String getFileName() {
        return fileName;
    }

    public int getFileLength() {
        return fileLength;
    }

    public int getValue() {
        return value;
    }

    public Object getFileDownloader() {
        return fileDownloader;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setFileDownloader(Object fileDownloader) {
        this.fileDownloader = fileDownloader;
    }
}
