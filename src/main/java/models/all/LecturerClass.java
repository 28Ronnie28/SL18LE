package models.all;

import java.io.Serializable;
import java.util.List;

public class LecturerClass implements Serializable{

    private int id;
    private String moduleName;
    private String moduleNumber;
    private List<ClassTime> classTimes;
    private List<ClassFile> files;

    public LecturerClass(int id, String moduleName, String moduleNumber, List<ClassTime> classTimes, List<ClassFile> files) {
        this.id = id;
        this.moduleName = moduleName;
        this.moduleNumber = moduleNumber;
        this.classTimes = classTimes;
        this.files = files;
    }

    public int getId() {
        return id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleNumber() {
        return moduleNumber;
    }

    public List<ClassTime> getClassTimes() {
        return classTimes;
    }

    public List<ClassFile> getFiles() {
        return files;
    }

    @Override
    public String toString() {
        return moduleName;
    }
}
