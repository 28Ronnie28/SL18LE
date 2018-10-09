package models.all;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.List;

public class Lecturer implements Serializable{

    private String firstName;
    private String lastName;
    private String lecturerNumber;
    private String email;
    private String contactNumber;
    private byte[] imageBytes;
    private List<LecturerClass> classes;

    public Lecturer(String firstName, String lastName, String lecturerNumber, String email, String contactNumber, byte[] imageBytes, List<LecturerClass> classes) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.lecturerNumber = lecturerNumber;
        this.email = email;
        this.contactNumber = contactNumber;
        this.classes = classes;
        this.imageBytes = imageBytes;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLecturerNumber() {
        return lecturerNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public Image getImage() {
        try {
            return SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(imageBytes)), null);
        } catch (Exception ex) {
        }
        return null;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public List<LecturerClass> getClasses() {
        return classes;
    }

    public String getLecturerDetails() {
        return "First Name: " + firstName + "\nLast Name: " + lastName + "\nLecturer Number: " + lecturerNumber + "\nEmail: " + email + "\nContact Number: " + contactNumber;
    }
}
