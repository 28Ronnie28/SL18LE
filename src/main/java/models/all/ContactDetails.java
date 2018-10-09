package models.all;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.Serializable;

public class ContactDetails implements Serializable {

    private int id;
    private String name;
    private String position;
    private String department;
    private String contactNumber;
    private String email;
    private byte[] imageBytes;

    public ContactDetails(int id, String name, String position, String department, String contactNumber, String email, byte[] imageBytes) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.department = department;
        this.contactNumber = contactNumber;
        this.email = email;
        this.imageBytes = imageBytes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getDepartment() {
        return department;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public Image getImage() {
        try {
            return SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(imageBytes)), null);
        } catch (Exception ex) {
        }
        return null;
    }

    public String getContactDetails() {
        return "Name: " + name + "\nPosition: " + position + "\nDepartment: " + department + "\nEmail: " + contactNumber + "\nContact Number: " + contactNumber;
    }
}
