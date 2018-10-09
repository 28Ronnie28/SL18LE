package models.lecturer;

import java.io.Serializable;

public class UploadFile implements Serializable {

    private String fileName;
    private int classID;
    private byte[] fileData;

    public UploadFile(String fileName, int classID, byte[] fileData) {
        this.fileName = fileName;
        this.classID = classID;
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }

    public int getClassID() {
        return classID;
    }

    public byte[] getFileData() {
        return fileData;
    }
}
