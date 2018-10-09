package net.ddns.pcuniverse.studentlivelecturer.main;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.all.*;
import models.lecturer.LecturerStudentAttendance;
import models.lecturer.LecturerStudentResult;

import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;

public class ConnectionHandler {

    public static final int PORT = 25760;
    public static String LOCAL_ADDRESS = "127.0.0.1";
    public LecturerObservable lecturer = new LecturerObservable(null);
    public volatile ObservableList<Notice> notices = FXCollections.observableArrayList();
    public volatile ObservableList<ContactDetails> contactDetails = FXCollections.observableArrayList();
    public volatile ObservableList<ImportantDate> importantDates = FXCollections.observableArrayList();
    public volatile ObservableList<LecturerStudentAttendance> attendance = FXCollections.observableArrayList();
    public volatile ObservableList<LecturerStudentResult> results = FXCollections.observableArrayList();
    public volatile ObservableList<String> outputQueue = FXCollections.observableArrayList();
    public volatile ObservableList<Object> inputQueue = FXCollections.observableArrayList();
    public String connectionType = "On Campus";
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public ConnectionHandler() {
        connect();
    }

    //<editor-fold desc="Connection">
    private void connect() {
        if (!connectLocal()) {
            UserNotification.showErrorMessage("Connection Error", "Failed to connect to StudentLive Servers!\nPlease check your network connection and try again!");
            System.out.println("Exiting..");
            System.exit(0);
        }
        new InputProcessor().start();
        new OutputProcessor().start();
    }

    private Boolean connectLocal() {
        System.out.println("Trying to connect to local server... (" + LOCAL_ADDRESS + ")");
        try {
            System.setProperty("javax.net.ssl.trustStore", Display.APPLICATION_FOLDER + "/campuslive.store");
            socket = SSLSocketFactory.getDefault().createSocket(LOCAL_ADDRESS, PORT);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Socket is connected");
            return true;
        } catch (Exception ex) {
            System.out.println("Could not connect to local server");
        }
        return false;
    }
    //</editor-fold>

    public String getConnectionType() {
        return connectionType;
    }

    //<editor-fold desc="Commands">
    public Boolean authorise(String lecturerNumber, String password) {
        outputQueue.add("la:" + lecturerNumber + ":" + password);
        return getStringReply("la:");
    }

    public Boolean changePassword(String prevPassword, String newPassword) {
        outputQueue.add("cp:" + prevPassword + ":" + newPassword);
        return getStringReply("cp:");
    }

    public boolean isDefaultPassword() {
        outputQueue.add("idp:");
        return getStringReply("idp:");
    }

    public Boolean changeDefaultPassword(String newPassword) {
        outputQueue.add("cdp:" + newPassword);
        return getStringReply("cdp:");
    }

    public void forgotPassword(String email) {
        outputQueue.add("flp:" + email);
    }

    public void sendMessage(String message, String lecturerNumber) {
        outputQueue.add("sm:" + message + ":" + lecturerNumber);
    }

    public void deleteFile(int classID, String fileName) {
        new File(Display.LOCAL_CACHE + "/" + classID + "/" + fileName).delete();
        updateSavedFiles();
    }

    public void deleteFileRemote(int classID, String fileName) {
        System.out.println("df:" + classID + ":" + fileName);
        deleteFile(classID, fileName);
        outputQueue.add(0, "df:" + classID + ":" + fileName);

    }

    public void sendData(Object data) {
        try {
            objectOutputStream.writeObject(data);
            objectOutputStream.flush();
            System.out.println("Sent data: " + data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object getReply() {
        try {
            Object input;
            while ((input = objectInputStream.readObject()) == null) ;
            return input;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    Boolean hasAttendance(int classID) {
        sendData("ha:" + classID);
        return getStringReply("ha:");
    }

    public void updateSavedFiles() {
        Boolean updated = false;
        for (LecturerClass lc : lecturer.getLecturer().getClasses()) {
            for (ClassFile cf : lc.getFiles()) {
                File f;
                if ((f = new File(Display.LOCAL_CACHE + "/" + cf.getClassID() + "/" + cf.getFileName())).exists() && f.length() == cf.getFileLength()) {
                    if (cf.getValue() != 1) {
                        cf.setValue(1);
                        updated = true;
                    }
                } else if (cf.getValue() == 1) {
                    cf.setValue(0);
                    updated = true;
                }
            }
            try {
                File classFolder = new File(Display.LOCAL_CACHE + "/" + lc.getId());
                if (classFolder.exists()) {
                    for (File file : classFolder.listFiles()) {
                        Boolean found = false;
                        for (ClassFile cf : lc.getFiles()) {
                            if (cf.getFileName().equals(file.getName()) && cf.getFileLength() == file.length()) {
                                found = true;
                            }
                        }
                        if (!found) {
                            Files.delete(file.toPath());
                            System.out.println("Deleted file: " + file.getName());
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (updated) {
            Platform.runLater(() -> lecturer.update());
            System.out.println("Files Updated");
        }
    }

    public Boolean getStringReply(String startsWith) {
        Boolean result;
        Object objectToRemove;
        ReturnResult:
        while (true) {
            for (int i = 0; i < inputQueue.size(); i++) {
                Object object = inputQueue.get(i);
                if (object instanceof String) {
                    String in = (String) object;
                    if (in.startsWith(startsWith)) {
                        objectToRemove = object;
                        result = in.charAt(startsWith.length()) == 'y';
                        break ReturnResult;
                    }
                }
            }
        }
        inputQueue.remove(objectToRemove);
        return result;
    }

    public List<ContactDetails> getContactDetails() {
        return contactDetails;
    }

    public Boolean lecturerInitialized() {
        return lecturer.getLecturer() != null;
    }

    private class InputProcessor extends Thread {
        public void run() {
            while (true) {
                Object input;
                if ((input = getReply()) != null) {
                    if (input instanceof Lecturer) {
                        lecturer.setLecturer((Lecturer) input);
                        updateSavedFiles();
                        lecturer.update();
                        System.out.println("Updated Lecturer");
                    } else if (input instanceof List<?>) {
                        List list = (List) input;
                        if (!list.isEmpty() && list.get(0) instanceof Notice) {
                            notices.clear();
                            if (!((Notice) list.get(0)).getHeading().equals("NoNotice")) {
                                notices.addAll(list);
                            }
                            System.out.println("Updated Notices");
                        } else if (!list.isEmpty() && list.get(0) instanceof ContactDetails) {
                            contactDetails.clear();
                            if (!((ContactDetails) list.get(0)).getName().equals("NoContactDetails")) {
                                contactDetails.addAll(list);
                            }
                            System.out.println("Updated Contact Details");
                        } else if (!list.isEmpty() && list.get(0) instanceof ImportantDate) {
                            importantDates.clear();
                            if (!((ImportantDate) list.get(0)).getDate().equals("NoImportantDate")) {
                                importantDates.addAll(list);
                            }
                            System.out.println("Updated Important Dates");
                        } else if (!list.isEmpty() && list.get(0) instanceof LecturerStudentAttendance) {
                            attendance.clear();
                            if (!((LecturerStudentAttendance) list.get(0)).getStudentFirstName().equals("NoAttendance")) {
                                attendance.addAll(list);
                            }
                            System.out.println("Updated Attendance");
                        } else if (!list.isEmpty() && list.get(0) instanceof LecturerStudentResult) {
                            results.clear();
                            if (!((LecturerStudentResult) list.get(0)).getStudentFirstName().equals("NoResults")) {
                                results.addAll(list);
                            }
                            System.out.println("Updated Results");
                        }
                    } else {
                        inputQueue.add(input);
                    }
                }
            }
        }
    }

    private class OutputProcessor extends Thread {
        public void run() {
            while (true) {
                if (!outputQueue.isEmpty()) {
                    sendData(outputQueue.get(0));
                    outputQueue.remove(0);
                }
            }
        }
    }

    public class FileDownloader extends Thread {

        public volatile IntegerProperty size;
        public volatile DoubleProperty progress;
        ClassFile file;
        byte[] bytes;

        public FileDownloader(ClassFile file) {
            this.file = file;
            bytes = new byte[file.getFileLength()];
            size = new SimpleIntegerProperty(0);
            progress = new SimpleDoubleProperty(0);
        }

        @Override
        public void run() {
            outputQueue.add("gf:" + file.getClassID() + ":" + file.getFileName());
            Done:
            while (true) {
                FilePart filePartToRemove = null;
                BreakSearch:
                for (int i = inputQueue.size() - 1; i > -1; i--) {
                    try {
                        Object object = inputQueue.get(i);
                        if (object instanceof FilePart) {
                            FilePart filePart = (FilePart) object;
                            if (filePart.getClassID() == file.getClassID() && filePart.getFileName().equals(file.getFileName())) {
                                filePartToRemove = filePart;
                                break BreakSearch;
                            }
                        }
                    } catch (IndexOutOfBoundsException ex) {
                    }
                }
                if (filePartToRemove != null) {
                    for (int i = 0; i < filePartToRemove.getFileBytes().length; i++) {
                        bytes[size.get() + i] = filePartToRemove.getFileBytes()[i];
                    }
                    size.set(size.get() + filePartToRemove.getFileBytes().length);
                    progress.set(1D * size.get() / bytes.length);
                    Platform.runLater(() -> lecturer.update());
                    inputQueue.remove(filePartToRemove);
                }
                if (size.get() == file.getFileLength()) {
                    System.out.println("File successfully downloaded!");
                    File f = new File(Display.LOCAL_CACHE + "/" + file.getClassID() + "/" + file.getFileName());
                    f.getParentFile().mkdirs();
                    try {
                        Files.write(f.toPath(), bytes);
                        updateSavedFiles();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break Done;
                }
            }
        }
    }

}
