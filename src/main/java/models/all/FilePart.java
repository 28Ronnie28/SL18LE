package models.all;

import java.io.Serializable;

public class FilePart implements Serializable{

    private byte[] fileBytes;
    private int classID;
    private String fileName;

    public FilePart(byte[] fileBytes, int classID, String fileName) {
        this.fileBytes = fileBytes;
        this.classID = classID;
        this.fileName = fileName;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public int getClassID() {
        return classID;
    }

    public String getFileName() {
        return fileName;
    }
}
