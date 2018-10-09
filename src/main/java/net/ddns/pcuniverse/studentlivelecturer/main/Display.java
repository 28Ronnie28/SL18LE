package net.ddns.pcuniverse.studentlivelecturer.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.all.*;
import models.lecturer.*;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class Display extends Application {

    public static final File APPLICATION_FOLDER = new File(System.getProperty("user.home") + "/AppData/Local/PCUniverse/StudentLiveLecturer");
    public static final File LOCAL_CACHE = new File(APPLICATION_FOLDER.getAbsolutePath() + "/Local Cache");
    public static BooleanProperty enableAnimations = new SimpleBooleanProperty(true);
    public IntegerProperty currentTimeslot = new SimpleIntegerProperty();
    private ConnectionHandler connectionHandler = new ConnectionHandler();
    private ObservableList<LecturerClass> classAndResults = FXCollections.observableArrayList();
    private Stage stage;
    private ImageView loginLogoImageView;
    private TextField lecturerNumberTextField;
    private PasswordField passwordField;
    private Hyperlink forgotPasswordHyperlink;
    private ProgressIndicator loginWaitIndicator;
    private JFXButton loginButton;
    private VBox loginPane;
    private Text classText;
    private ComboBox<LecturerClass> classSelectComboBox;
    private HBox classHeadingPane;
    private Button doAttendanceButton;
    private ListView<LecturerFileObservable> classFilesListView;
    private GridPane timetableGridPane;
    private VBox resultsInnerPane;
    private ScrollPane noticeboardScrollPane;
    private JFXMasonryPane noticeboardInnerPane;
    private VBox contactDetailsCardPane;
    private ListView<ImportantDate> importantDateListView;
    private VBox attendanceInnerPane;
    private Label lecturerInfoLabel;
    private SideTabPane sideTabPane;
    private VBox lecturerPane;
    private StackPane backgroundPane;
    private StackPane contentPane;
    private volatile BooleanProperty uploading = new SimpleBooleanProperty(false);

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Setup Stage
        //<editor-fold desc="Stage">
        stage = primaryStage;
        stage.setTitle("StudentLive Lecturer " + getBuild());
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("SLLogo.png")));
        stage.setMaxWidth(1920);
        stage.setMaxHeight(1080);
        stage.setMinWidth(1280);
        stage.setMinHeight(800);
        stage.setMaximized(true);
        //</editor-fold>

        //Setup tooltip delays
        //<editor-fold desc="Tooltip Delays">
        try {
            Class<?> clazz = Tooltip.class.getDeclaredClasses()[0];
            Constructor<?> constructor = clazz.getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);
            constructor.setAccessible(true);
            Object tooltipBehavior = constructor.newInstance(new Duration(300), new Duration(5000), new Duration(300), false);
            Field fieldBehavior = Tooltip.class.getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            fieldBehavior.set(Tooltip.class, tooltipBehavior);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //</editor-fold>

        //Setup login pane
        //<editor-fold desc="Login Pane">
        loginLogoImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("SLLogo.png")));
        loginLogoImageView.setFitHeight(200);
        loginLogoImageView.setFitWidth(200);
        lecturerNumberTextField = new TextField("PH009"); //TODO
        lecturerNumberTextField.setPromptText("Lecturer Number");
        lecturerNumberTextField.getStyleClass().add("login-fields");
        passwordField = new PasswordField();
        passwordField.setText("password"); //TODO
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("login-fields");
        passwordField.getStyleClass().add("login-password");
        loginButton = new JFXButton("Login");
        loginButton.getStyleClass().add("login-button");
        loginButton.setOnAction((ActionEvent e) -> {
            lecturerNumberTextField.setBorder(null);
            passwordField.setBorder(null);
            if (lecturerNumberTextField.getText().length() < 5) {
                lecturerNumberTextField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));
                Tooltip lecturerNumberShortTooltip = new Tooltip("Lecturer number too short");
                lecturerNumberShortTooltip.getStyleClass().add("login-tooltip");
                lecturerNumberTextField.setTooltip(lecturerNumberShortTooltip);
            } else if (passwordField.getText().length() < 5) {
                passwordField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));
                Tooltip passwordShortTooltip = new Tooltip("Password too short");
                passwordShortTooltip.getStyleClass().add("login-tooltip");
                passwordField.setTooltip(passwordShortTooltip);
            } else {
                loginPane.getChildren().clear();
                loginPane.getChildren().addAll(loginLogoImageView, loginWaitIndicator);
                BooleanProperty waitingForAuthorisation = new SimpleBooleanProperty(true);
                BooleanProperty authoriseResult = new SimpleBooleanProperty(false);
                Thread loginThread = new Thread(() -> {
                    if (connectionHandler.authorise(lecturerNumberTextField.getText(), passwordField.getText())) {
                        while (!connectionHandler.lecturerInitialized()) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                        authoriseResult.setValue(true);
                    } else {
                        authoriseResult.setValue(false);
                    }
                    waitingForAuthorisation.set(false);
                });
                loginThread.start();
                waitingForAuthorisation.addListener(al -> {
                    if (authoriseResult.getValue()) {
                        if (connectionHandler.isDefaultPassword()) {
                            Platform.runLater(() -> new ChangePasswordDialog(stage, true, connectionHandler).showDialog());
                        }
                        Platform.runLater(() -> {
                            contentPane.getChildren().clear();
                            contentPane.getChildren().addAll(backgroundPane, lecturerPane);
                        });
                    } else {
                        Platform.runLater(() -> {
                            passwordField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));
                            Tooltip incorrectLoginTooltip = new Tooltip("Incorrect login details");
                            incorrectLoginTooltip.getStyleClass().add("login-tooltip");
                            passwordField.setTooltip(incorrectLoginTooltip);
                            loginPane.getChildren().clear();
                            loginPane.getChildren().addAll(loginLogoImageView, lecturerNumberTextField, passwordField, loginButton, forgotPasswordHyperlink);
                            passwordField.clear();
                            passwordField.requestFocus();
                        });
                    }
                });
            }
        });
        loginButton.setDefaultButton(true);
        forgotPasswordHyperlink = new Hyperlink("Forgot Password?");
        forgotPasswordHyperlink.setStyle("-fx-text-fill: #8B0000");
        forgotPasswordHyperlink.setOnAction(e -> {
            new ForgotPasswordDialog(stage, connectionHandler);
        });
        loginWaitIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        loginWaitIndicator.setPrefSize(206, 206);
        loginWaitIndicator.setStyle(" -fx-progress-color: #990000;");
        loginPane = new VBox(loginLogoImageView, lecturerNumberTextField, passwordField, loginButton, forgotPasswordHyperlink);
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setSpacing(20);
        loginPane.setPadding(new Insets(10));
        loginPane.setMaxSize(500, 500);
        //</editor-fold>

        //Setup home pane
        //<editor-fold desc="Home Pane">
        Text homeText = new Text("Welcome to StudentLive");
        homeText.getStyleClass().add("heading-text");
        VBox homeTextBox = new VBox(homeText);
        VBox.setVgrow(homeTextBox, Priority.ALWAYS);
        homeTextBox.setAlignment(Pos.TOP_CENTER);

        Label classLbl = new Label("Class");
        classLbl.getStyleClass().add("home-text");
        classLbl.setTextFill(Color.web("#990000"));;
        classLbl.setPadding(new Insets(10));
        VBox classLblBox = new VBox(classLbl);
        classLblBox.setAlignment(Pos.BOTTOM_CENTER);
        Button classBtn = new Button();
        classBtn.getStyleClass().add("home-icon-image");
        classBtn.getStyleClass().add("home-icon-image-class");
        classBtn.setOnMouseClicked(e -> {
            Platform.runLater(() -> { ;
                sideTabPane.select(1);
            });
        });
        VBox classBtnBox = new VBox(classBtn);
        classBtnBox.setAlignment(Pos.TOP_CENTER);
        VBox classIconPane = new VBox(classBtnBox, classLblBox);

        Label timetableLbl = new Label("Timetable");
        timetableLbl.getStyleClass().add("home-text");
        timetableLbl.setTextFill(Color.web("#990000"));;
        timetableLbl.setPadding(new Insets(10));
        VBox timetableLblBox = new VBox(timetableLbl);
        timetableLblBox.setAlignment(Pos.BOTTOM_CENTER);
        Button timetableBtn = new Button();
        timetableBtn.getStyleClass().add("home-icon-image");
        timetableBtn.getStyleClass().add("home-icon-image-timetable");
        timetableBtn.setOnMouseClicked(e -> {
            Platform.runLater(() -> { ;
                sideTabPane.select(2);
            });
        });
        VBox timetableBtnBox = new VBox(timetableBtn);
        timetableBtnBox.setAlignment(Pos.TOP_CENTER);
        VBox timetableIconPane = new VBox(timetableBtnBox, timetableLblBox);

        Label resultsLbl = new Label("Results");
        resultsLbl.getStyleClass().add("home-text");
        resultsLbl.setTextFill(Color.web("#990000"));;
        resultsLbl.setPadding(new Insets(10));
        VBox resultsLblBox = new VBox(resultsLbl);
        resultsLblBox.setAlignment(Pos.BOTTOM_CENTER);
        Button resultsBtn = new Button();
        resultsBtn.getStyleClass().add("home-icon-image");
        resultsBtn.getStyleClass().add("home-icon-image-results");
        resultsBtn.setOnMouseClicked(e -> {
            Platform.runLater(() -> { ;
                sideTabPane.select(3);
            });
        });
        VBox resultsBtnBox = new VBox(resultsBtn);
        resultsBtnBox.setAlignment(Pos.TOP_CENTER);
        VBox resultsIconPane = new VBox(resultsBtnBox, resultsLblBox);

        Label noticeboardLbl = new Label("Noticeboard");
        noticeboardLbl.getStyleClass().add("home-text");
        noticeboardLbl.setTextFill(Color.web("#990000"));;
        noticeboardLbl.setPadding(new Insets(10));
        VBox noticeboardLblBox = new VBox(noticeboardLbl);
        noticeboardLblBox.setAlignment(Pos.BOTTOM_CENTER);
        Button noticeboardBtn = new Button();
        noticeboardBtn.getStyleClass().add("home-icon-image");
        noticeboardBtn.getStyleClass().add("home-icon-image-noticeboard");
        noticeboardBtn.setOnMouseClicked(e -> {
            Platform.runLater(() -> { ;
                sideTabPane.select(4);
            });
        });
        VBox noticeboardBtnBox = new VBox(noticeboardBtn);
        noticeboardBtnBox.setAlignment(Pos.TOP_CENTER);
        VBox noticebaordIconPane = new VBox(noticeboardBtnBox, noticeboardLblBox);

        Label contactInfoLbl = new Label("Contact Info");
        contactInfoLbl.getStyleClass().add("home-text");
        contactInfoLbl.setTextFill(Color.web("#990000"));;
        contactInfoLbl.setPadding(new Insets(10));
        VBox contactInfoLblBox = new VBox(contactInfoLbl);
        contactInfoLblBox.setAlignment(Pos.BOTTOM_CENTER);
        Button contactinfoBtn = new Button();
        contactinfoBtn.getStyleClass().add("home-icon-image");
        contactinfoBtn.getStyleClass().add("home-icon-image-contactinfo");
        contactinfoBtn.setOnMouseClicked(e -> {
            Platform.runLater(() -> { ;
                sideTabPane.select(5);
            });
        });
        VBox contactinfoBtnBox = new VBox(contactinfoBtn);
        contactinfoBtnBox.setAlignment(Pos.TOP_CENTER);
        VBox contactinfoIconPane = new VBox(contactinfoBtnBox, contactInfoLblBox);

        Label importantdatesLbl = new Label("Important Dates");
        importantdatesLbl.getStyleClass().add("home-text");
        importantdatesLbl.setTextFill(Color.web("#990000"));;
        importantdatesLbl.setPadding(new Insets(10));
        VBox importantdatesLblBox = new VBox(importantdatesLbl);
        importantdatesLblBox.setAlignment(Pos.BOTTOM_CENTER);
        Button importantdatesBtn = new Button();
        importantdatesBtn.getStyleClass().add("home-icon-image");
        importantdatesBtn.getStyleClass().add("home-icon-image-importantdates");
        importantdatesBtn.setOnMouseClicked(e -> {
            Platform.runLater(() -> { ;
                sideTabPane.select(6);
            });
        });
        VBox importantdatesBtnBox = new VBox(importantdatesBtn);
        importantdatesBtnBox.setAlignment(Pos.TOP_CENTER);
        VBox importantdatesIconPane = new VBox(importantdatesBtnBox, importantdatesLblBox);

        Label attendanceLbl = new Label("Attendance");
        attendanceLbl.getStyleClass().add("home-text");
        attendanceLbl.setTextFill(Color.web("#990000"));;
        attendanceLbl.setPadding(new Insets(10));
        VBox attendanceLblBox = new VBox(attendanceLbl);
        attendanceLblBox.setAlignment(Pos.BOTTOM_CENTER);
        Button attendanceBtn = new Button();
        attendanceBtn.getStyleClass().add("home-icon-image");
        attendanceBtn.getStyleClass().add("home-icon-image-attendance");
        attendanceBtn.setOnMouseClicked(e -> {
            Platform.runLater(() -> { ;
                sideTabPane.select(7);
            });
        });
        VBox attendanceBtnBox = new VBox(attendanceBtn);
        attendanceBtnBox.setAlignment(Pos.TOP_CENTER);
        VBox attendanceIconPane = new VBox(attendanceBtnBox, attendanceLblBox);

        Label assignmentsLbl = new Label("Assignments");
        assignmentsLbl.getStyleClass().add("home-text");
        assignmentsLbl.setTextFill(Color.web("#990000"));;
        assignmentsLbl.setPadding(new Insets(10));
        VBox assignmentsLblBox = new VBox(assignmentsLbl);
        assignmentsLblBox.setAlignment(Pos.BOTTOM_CENTER);
        Button assignmentsBtn = new Button();
        assignmentsBtn.getStyleClass().add("home-icon-image");
        assignmentsBtn.getStyleClass().add("home-icon-image-assignments");
        assignmentsBtn.setOnMouseClicked(e -> {
            Platform.runLater(() -> { ;
                sideTabPane.select(8);
            });
        });
        VBox assignmentsBtnBox = new VBox(assignmentsBtn);
        assignmentsBtnBox.setAlignment(Pos.TOP_CENTER);
        VBox assignmentsIconPane = new VBox(assignmentsBtnBox, assignmentsLblBox);

        VBox firstgroupIcons = new VBox(classIconPane, contactinfoIconPane);
        HBox.setHgrow(firstgroupIcons, Priority.ALWAYS);
        firstgroupIcons.setSpacing(20);
        VBox secondgroupIcons = new VBox(timetableIconPane, importantdatesIconPane);
        HBox.setHgrow(secondgroupIcons, Priority.ALWAYS);
        secondgroupIcons.setSpacing(20);
        VBox thirdgroupIcons = new VBox(resultsIconPane, attendanceIconPane);
        HBox.setHgrow(thirdgroupIcons, Priority.ALWAYS);
        thirdgroupIcons.setSpacing(20);
        VBox fourthgroupIcons = new VBox(noticebaordIconPane, assignmentsIconPane);
        HBox.setHgrow(fourthgroupIcons, Priority.ALWAYS);
        fourthgroupIcons.setSpacing(20);
        HBox classicons = new HBox(firstgroupIcons, secondgroupIcons, thirdgroupIcons, fourthgroupIcons);

        Label cafeLbl = new Label("Cafe");
        cafeLbl.getStyleClass().add("home-text");
        cafeLbl.setTextFill(Color.web("#990000"));;
        cafeLbl.setPadding(new Insets(10));
        VBox cafeLblBox = new VBox(cafeLbl);
        cafeLblBox.setAlignment(Pos.BOTTOM_CENTER);
        Button cafeBtn = new Button();
        cafeBtn.getStyleClass().add("home-icon-image");
        cafeBtn.getStyleClass().add("home-icon-image-cafe");
        cafeBtn.setOnMouseClicked(e -> {
            Platform.runLater(() -> { ;
                sideTabPane.select(9);
            });
        });
        VBox cafeBtnBox = new VBox(cafeBtn);
        cafeBtnBox.setAlignment(Pos.TOP_CENTER);
        VBox cafeIconPane = new VBox(cafeBtnBox, cafeLblBox);
        HBox.setHgrow(cafeIconPane, Priority.ALWAYS);

        Label chatRoomLbl = new Label("ChatRoom");
        chatRoomLbl.getStyleClass().add("home-text");
        chatRoomLbl.setTextFill(Color.web("#990000"));;
        chatRoomLbl.setPadding(new Insets(10));
        VBox chatRoomLblBox = new VBox(chatRoomLbl);
        chatRoomLblBox.setAlignment(Pos.BOTTOM_CENTER);
        Button chatRoomBtn = new Button();
        chatRoomBtn.getStyleClass().add("home-icon-image");
        chatRoomBtn.getStyleClass().add("home-icon-image-chatroom");
        chatRoomBtn.setOnMouseClicked(e -> {
            Platform.runLater(() -> { ;
                sideTabPane.select(10);
            });
        });
        VBox chatRoomBtnBox = new VBox(chatRoomBtn);
        chatRoomBtnBox.setAlignment(Pos.TOP_CENTER);
        VBox chatRoomIconPane = new VBox(chatRoomBtnBox, chatRoomLblBox);
        HBox.setHgrow(chatRoomIconPane, Priority.ALWAYS);

        Label settingsLbl = new Label("Settings");
        settingsLbl.getStyleClass().add("home-text");
        settingsLbl.setTextFill(Color.web("#990000"));;
        settingsLbl.setPadding(new Insets(10));
        VBox settingsLblBox = new VBox(settingsLbl);
        settingsLblBox.setAlignment(Pos.BOTTOM_CENTER);
        Button settingsBtn = new Button();
        settingsBtn.getStyleClass().add("home-icon-image");
        settingsBtn.getStyleClass().add("home-icon-image-settings");
        settingsBtn.setOnMouseClicked(e -> {
            Platform.runLater(() -> { ;
                sideTabPane.select(11);
            });
        });
        VBox settingsBtnBox = new VBox(settingsBtn);
        settingsBtnBox.setAlignment(Pos.TOP_CENTER);
        VBox settingsIconPane = new VBox(settingsBtnBox, settingsLblBox);
        HBox.setHgrow(settingsIconPane, Priority.ALWAYS);

        HBox bottomIcons = new HBox(cafeIconPane, chatRoomIconPane, settingsIconPane);
        bottomIcons.setPadding(new Insets(25));

        VBox homePane = new VBox(homeTextBox, classicons, bottomIcons);
        homePane.setPadding(new Insets(15));
        homePane.setAlignment(Pos.CENTER);
        homePane.setSpacing(50);
        VBox.setVgrow(homePane, Priority.ALWAYS);
        //</editor-fold>

        //Setup class pane
        //<editor-fold desc="Class Pane">
        classText = new Text();
        classText.setOnMouseClicked(e -> classSelectComboBox.show());
        classText.getStyleClass().add("class-heading-text");
        classSelectComboBox = new ComboBox<>(classAndResults);
        classSelectComboBox.getStyleClass().add("class-select-combo-box");
        classSelectComboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
            if (!classSelectComboBox.getSelectionModel().isEmpty()) {
                classText.setText(classSelectComboBox.getSelectionModel().getSelectedItem().toString());
                if (classFilesListView != null) {
                    classFilesListView.getItems().clear();
                    ObservableList<LecturerFileObservable> studentFiles = FXCollections.observableArrayList();
                    for (ClassFile classFile : classSelectComboBox.getSelectionModel().getSelectedItem().getFiles()) {
                        studentFiles.add(new LecturerFileObservable(classFile));
                    }
                    classFilesListView.setItems(studentFiles);
                }
            }
            checkAttendance();
        });
        doAttendanceButton = new Button("Attendance");
        doAttendanceButton.setOnAction(e -> {
            List<String[]> students = new ArrayList<>();
            for (LecturerStudentAttendance lsa : connectionHandler.attendance) {
                for (LecturerStudentAttendanceClass lsac : lsa.getClasses()) {
                    if (!classSelectComboBox.getSelectionModel().isEmpty() && lsac.getClassID() == classSelectComboBox.getSelectionModel().getSelectedItem().getId()) {
                        students.add(new String[]{lsa.getStudentFirstName() + " " + lsa.getStudentLastName(), lsa.getStudentNumber()});
                        break;
                    }
                }
            }
            new AttendanceDialog(stage, connectionHandler, students, classSelectComboBox.getSelectionModel().getSelectedItem().getId()).showDialog();
            try {
                Thread.sleep(100);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            checkAttendance();
        });
        classHeadingPane = new HBox(classSelectComboBox, classText);
        classHeadingPane.setSpacing(5);
        classHeadingPane.setAlignment(Pos.CENTER);
        classFilesListView = new ListView<>();
        classFilesListView.getStyleClass().add("files-list-view");
        classFilesListView.setPlaceholder(new Label("No files available for this class"));
        classFilesListView.setCellFactory(param -> new ListCell<LecturerFileObservable>() {
            @Override
            protected void updateItem(LecturerFileObservable file, boolean empty) {

                super.updateItem(file, empty);

                SVGGlyph savedImage = new SVGGlyph(0, "Saved", "M219.429 73.143h438.857v219.429h-438.857v-219.429zM731.429 73.143h73.143v512q0 8-5.714 22t-11.429 19.714l-160.571 160.571q-5.714 5.714-19.429 11.429t-22.286 5.714v-237.714q0-22.857-16-38.857t-38.857-16h-329.143q-22.857 0-38.857 16t-16 38.857v237.714h-73.143v-731.429h73.143v237.714q0 22.857 16 38.857t38.857 16h475.429q22.857 0 38.857-16t16-38.857v-237.714zM512 603.428v182.857q0 7.429-5.429 12.857t-12.857 5.429h-109.714q-7.429 0-12.857-5.429t-5.429-12.857v-182.857q0-7.429 5.429-12.857t12.857-5.429h109.714q7.429 0 12.857 5.429t5.429 12.857zM877.714 585.143v-530.286q0-22.857-16-38.857t-38.857-16h-768q-22.857 0-38.857 16t-16 38.857v768q0 22.857 16 38.857t38.857 16h530.286q22.857 0 50.286-11.429t43.429-27.429l160-160q16-16 27.429-43.429t11.429-50.286z", Color.WHITE);
                savedImage.setSize(32, 32);
                savedImage.setScaleY(-1);
                SVGGlyph downloadImage = new SVGGlyph(0, "Download", "M731.429 420.571q0 8-5.143 13.143t-13.143 5.143h-128v201.143q0 7.429-5.429 12.857t-12.857 5.429h-109.714q-7.429 0-12.857-5.429t-5.429-12.857v-201.143h-128q-7.429 0-12.857-5.429t-5.429-12.857q0-8 5.143-13.143l201.143-201.143q5.143-5.143 13.143-5.143t13.143 5.143l200.571 200.571q5.714 6.857 5.714 13.714zM1097.143 292.571q0-90.857-64.286-155.143t-155.143-64.286h-621.714q-105.714 0-180.857 75.143t-75.143 180.857q0 74.286 40 137.143t107.429 94.286q-1.143 17.143-1.143 24.571 0 121.143 85.714 206.857t206.857 85.714q89.143 0 163.143-49.714t107.714-132q40.571 35.429 94.857 35.429 60.571 0 103.429-42.857t42.857-103.429q0-43.429-23.429-78.857 74.286-17.714 122-77.429t47.714-136.286z", Color.WHITE);
                downloadImage.setSize(32, 26);
                downloadImage.setScaleY(-1);

                MenuItem openFileMenuItem = new MenuItem("Open File");
                openFileMenuItem.setOnAction(event -> {
                    File openFile;
                    if ((openFile = new File(LOCAL_CACHE.getAbsolutePath() + "/" + file.getClassFile().getClassID() + "/" + file.getClassFile().getFileName())).exists() && openFile.length() == file.getClassFile().getFileLength()) {
                        try {
                            java.awt.Desktop.getDesktop().open(openFile);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                MenuItem exportFileMenuItem = new MenuItem("Export File");
                exportFileMenuItem.setOnAction(event -> {
                    try {
                        DirectoryChooser dc = new DirectoryChooser();
                        dc.setTitle("Choose Directory to export to..");
                        File f = dc.showDialog(stage);
                        if (f != null) {
                            File target = new File(f.getAbsolutePath() + "/" + file.getClassFile().getFileName());
                            File toCopy = new File(LOCAL_CACHE.getAbsolutePath() + "/" + file.getClassFile().getClassID() + "/" + file.getClassFile().getFileName());
                            Files.copy(toCopy.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                MenuItem deleteFileLocalMenuItem = new MenuItem("Delete File (Local)");
                deleteFileLocalMenuItem.setOnAction(e -> connectionHandler.deleteFile(file.getClassFile().getClassID(), file.getClassFile().getFileName()));
                MenuItem deleteFileRemoteMenuItem = new MenuItem("Delete File (Remote)");
                deleteFileRemoteMenuItem.setOnAction(e -> connectionHandler.deleteFileRemote(file.getClassFile().getClassID(), file.getClassFile().getFileName()));
                MenuItem detailsMenuItem = new MenuItem("Details");
                detailsMenuItem.setOnAction(e -> UserNotification.showFileDetails(stage, file.getClassFile()));
                ContextMenu savedContextMenu = new ContextMenu(openFileMenuItem, exportFileMenuItem, deleteFileLocalMenuItem, deleteFileRemoteMenuItem, detailsMenuItem);
                savedContextMenu.getStyleClass().add("file-context-menu");
                MenuItem downloadFileMenuItem = new MenuItem("Download File");
                downloadFileMenuItem.setOnAction(e -> {
                    file.getClassFile().setValue(2);
                    ConnectionHandler.FileDownloader fileDownloader = connectionHandler.new FileDownloader(file.getClassFile());
                    fileDownloader.start();
                    file.getClassFile().setFileDownloader(fileDownloader);
                    connectionHandler.lecturer.update();
                });
                ContextMenu downloadContextMenu = new ContextMenu(downloadFileMenuItem);
                downloadContextMenu.getStyleClass().add("file-context-menu");

                setOnMouseClicked(evt -> {
                    if (evt.getClickCount() == 2) {
                        if (getGraphic().equals(savedImage)) {
                            openFileMenuItem.fire();
                        } else if (getGraphic().equals(downloadImage)) {
                            downloadFileMenuItem.fire();
                        }
                    }
                });

                if (empty || file == null || file.getClassFile().getFileName() == null) {
                    setText(null);
                    setContextMenu(null);
                    setGraphic(null);
                } else {
                    setText(getFileNameWithoutExtension(file.getClassFile().getFileName()));
                    setGraphicTextGap(35);
                    if (file.getClassFile().getValue() == 0) {
                        setGraphic(downloadImage);
                        setContextMenu(downloadContextMenu);
                    } else if (file.getClassFile().getValue() == 1) {
                        setGraphic(savedImage);
                        setContextMenu(savedContextMenu);
                    } else {
                        ProgressBar downloadProgressBar = new ProgressBar(0);
                        downloadProgressBar.getStyleClass().add("download-progress-bar");
                        downloadProgressBar.progressProperty().bind(file.progressProperty());
                        Text downloadPercentageText = new Text();
                        downloadPercentageText.textProperty().bind(file.progressProperty().multiply(100).asString("%.0f").concat("%"));
                        downloadPercentageText.getStyleClass().add("percentage-text");
                        StackPane downloadPane = new StackPane(downloadProgressBar, downloadPercentageText);
                        setGraphic(downloadPane);
                        setGraphicTextGap(16);
                    }
                }
            }
        });
        classFilesListView.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });
        classFilesListView.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            Boolean uploaded = false;
            if (dragboard.hasFiles()) {
                uploaded = true;
                UploadDialog uploadDialog = new UploadDialog(stage);
                uploading.addListener(al -> {
                    if (!uploading.get()) {
                        uploadDialog.closeAnimation();
                    }
                });
                uploading.set(true);
                ObservableList<File> uploadFiles = FXCollections.observableArrayList();
                uploadFiles.addAll(dragboard.getFiles());
                new Thread(() -> {
                    try {
                        System.out.println(uploadFiles.size());
                        int classID = classSelectComboBox.getSelectionModel().getSelectedItem().getId();
                        for (File newFile : uploadFiles) {
                            UploadFile uploadFile = new UploadFile(newFile.getName(), classID, Files.readAllBytes(newFile.toPath()));
                            connectionHandler.sendData(uploadFile);
                        }
                        for (File newFile : uploadFiles) {
                            File localNewFile = new File(LOCAL_CACHE.getAbsolutePath() + "/" + classID + "/" + newFile.getName());
                            localNewFile.getParentFile().mkdirs();
                            Files.copy(newFile.toPath(), localNewFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Copied file: " + newFile.getName());
                        }
                        connectionHandler.updateSavedFiles();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    uploading.set(false);
                }).start();
                if (uploading.get()) {
                    uploadDialog.showDialog();
                }
            }
            event.setDropCompleted(uploaded);
            event.consume();
        });
        classFilesListView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) classFilesListView.getSelectionModel().clearSelection();
        });
        Button uploadButton = new Button("+");
        uploadButton.setMinSize(50, 50);
        uploadButton.setMaxSize(50, 50);
        uploadButton.setStyle("-fx-background-color: #ffb81c;" +
                "-fx-background-radius: 25;" +
                "-fx-border-radius: 25;" +
                "-fx-font-size: 22;");
        uploadButton.setOnAction(e -> {
            if (!classSelectComboBox.getSelectionModel().isEmpty()) {
                try {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Select file to upload..");
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null) {
                        File localNewFile = new File(LOCAL_CACHE.getAbsolutePath() + "/" + classSelectComboBox.getSelectionModel().getSelectedItem().getId() + "/" + file.getName());
                        localNewFile.getParentFile().mkdirs();
                        Files.copy(file.toPath(), localNewFile.toPath());
                        UploadFile uploadFile = new UploadFile(file.getName(), classSelectComboBox.getSelectionModel().getSelectedItem().getId(), Files.readAllBytes(file.toPath()));
                        System.out.println(file.getName());
                        UploadDialog uploadDialog = new UploadDialog(stage);
                        uploading.addListener(al -> {
                            if (!uploading.get()) {
                                uploadDialog.closeAnimation();
                            }
                        });
                        uploading.set(true);
                        new Thread(() -> {
                            connectionHandler.sendData(uploadFile);
                            uploading.set(false);
                        }).start();
                        uploadDialog.showDialog();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        VBox uploadPane = new VBox(uploadButton);
        uploadPane.setAlignment(Pos.BOTTOM_LEFT);
        uploadPane.setPadding(new Insets(0, 0, -32, -16));
        HBox classFilesPane = new HBox(classFilesListView, uploadPane);
        HBox.setHgrow(classFilesListView, Priority.ALWAYS);
        classFilesPane.setAlignment(Pos.CENTER);
        classFilesPane.setPadding(new Insets(15, 150, 30, 150));
        VBox classPane = new VBox(classHeadingPane, classFilesPane);
        VBox.setVgrow(classFilesPane, Priority.ALWAYS);
        classPane.setSpacing(20);
        classPane.setPadding(new Insets(15));
        classPane.setAlignment(Pos.CENTER);
        //</editor-fold>

        //Setup timetable pane
        //<editor-fold desc="Timetable Pane">
        timetableGridPane = new GridPane();
        ObservableList<ColumnConstraints> columnConstraints = FXCollections.observableArrayList();
        for (int i = 0; i < 14; i++) {
            ColumnConstraints col = new ColumnConstraints();
            if (i == 0) {
                col.setPercentWidth(4);
            } else {
                col.setPercentWidth(8);
            }
            columnConstraints.add(col);
        }
        timetableGridPane.getColumnConstraints().addAll(columnConstraints);
        ObservableList<RowConstraints> rowConstraints = FXCollections.observableArrayList();
        for (int i = 0; i < 6; i++) {
            RowConstraints row = new RowConstraints();
            if (i == 0) {
                row.setPercentHeight(10);
            } else {
                row.setPercentHeight(18);
            }
            rowConstraints.add(row);
        }
        timetableGridPane.getRowConstraints().addAll(rowConstraints);
        timetableGridPane.getStyleClass().add("timetable-pane");
        timetableGridPane.setMaxSize(2000, 2000);
        VBox timetablePane = new VBox(timetableGridPane);
        VBox.setVgrow(timetableGridPane, Priority.ALWAYS);
        //</editor-fold>

        //Setup results pane
        //<editor-fold desc="Results Pane">
        Text resultText = new Text("Results");
        resultText.getStyleClass().add("heading-text");
        resultsInnerPane = new VBox();
        resultsInnerPane.setAlignment(Pos.CENTER);
        resultsInnerPane.setSpacing(25);
        resultsInnerPane.setPadding(new Insets(20));
        ScrollPane resultsScrollPane = new ScrollPane(resultsInnerPane);
        resultsInnerPane.setOnScroll(event -> {
            double deltaY = event.getDeltaY() * 10;
            double width = resultsScrollPane.getContent().getBoundsInLocal().getWidth();
            double vValue = resultsScrollPane.getVvalue();
            resultsScrollPane.setVvalue(vValue + -deltaY / width);
        });
        VBox.setVgrow(resultsScrollPane, Priority.ALWAYS);
        resultsInnerPane.prefHeightProperty().bind(resultsScrollPane.heightProperty().subtract(46D));
        resultsScrollPane.setFitToWidth(true);
        VBox resultsPane = new VBox(resultText, resultsScrollPane);
        //resultsPane.getChildren().addAll(resultsScrollPane);
        resultsPane.setPadding(new Insets(15));
        resultsPane.setAlignment(Pos.TOP_CENTER);
        resultsPane.setSpacing(25);
        //</editor-fold>

        //Setup noticeboard pane
        //<editor-fold desc="Noticeboard Pane">
        noticeboardScrollPane = new ScrollPane();
        noticeboardScrollPane.setFitToWidth(true);
        noticeboardScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox noticeboardPane = new VBox(noticeboardScrollPane);
        VBox.setVgrow(noticeboardScrollPane, Priority.ALWAYS);
        noticeboardScrollPane.widthProperty().addListener(e -> {
            noticeboardInnerPane.setMaxWidth(noticeboardScrollPane.getWidth());
        });
        populateNoticeBoard();
        //</editor-fold>

        //Setup contact details pane
        //<editor-fold desc="Contact Details Pane">
        Text contactText = new Text("Contact Details");
        contactText.getStyleClass().add("heading-text");
        contactDetailsCardPane = new VBox();
        contactDetailsCardPane.setAlignment(Pos.TOP_CENTER);
        contactDetailsCardPane.setSpacing(15);
        ScrollPane contactScrollPane = new ScrollPane(contactDetailsCardPane);
        contactDetailsCardPane.setOnScroll(event -> {
            double deltaY = event.getDeltaY() * 10;
            double width = contactScrollPane.getContent().getBoundsInLocal().getWidth();
            double vValue = contactScrollPane.getVvalue();
            contactScrollPane.setVvalue(vValue + -deltaY / width);
        });
        VBox.setVgrow(contactScrollPane, Priority.ALWAYS);
        contactDetailsCardPane.prefHeightProperty().bind(contactScrollPane.heightProperty().subtract(46D));
        contactScrollPane.setFitToWidth(true);
        VBox contactPane = new VBox(contactText, contactScrollPane);
        contactPane.setSpacing(25);
        contactPane.setPadding(new Insets(15));
        contactPane.setAlignment(Pos.CENTER);
        VBox.setVgrow(contactDetailsCardPane, Priority.ALWAYS);
        populateContactDetails();
        //</editor-fold>

        //Setup important dates pane
        //<editor-fold desc="Important Dates">
        Text importantDatesText = new Text("Important Dates");
        importantDatesText.getStyleClass().add("heading-text");

        importantDateListView = new ListView<>();
        importantDateListView.getStyleClass().add("files-list-view");
        importantDateListView.setCellFactory(param -> new ListCell<ImportantDate>() {
            @Override
            protected void updateItem(ImportantDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText("     " + item.getDate() + "   -   " + item.getDescription());
                }
            }
        });

        VBox importantDatesPane = new VBox(importantDatesText, importantDateListView);
        VBox.setVgrow(importantDateListView, Priority.ALWAYS);
        importantDatesPane.setPadding(new Insets(15, 50, 50, 50));
        importantDatesPane.setSpacing(15);
        importantDatesPane.setAlignment(Pos.CENTER);
        //</editor-fold>

        //Setup attendance pane
        //<editor-fold desc="Attendance Pane">
        Text attendanceText = new Text("Attendance");
        attendanceText.getStyleClass().add("heading-text");
        attendanceInnerPane = new VBox();
        attendanceInnerPane.setAlignment(Pos.CENTER);
        attendanceInnerPane.setSpacing(15);
        attendanceInnerPane.setPadding(new Insets(20));
        ScrollPane attendanceScrollPane = new ScrollPane(attendanceInnerPane);
        attendanceInnerPane.setOnScroll(event -> {
            double deltaY = event.getDeltaY() * 10;
            double width = attendanceScrollPane.getContent().getBoundsInLocal().getWidth();
            double vValue = attendanceScrollPane.getVvalue();
            attendanceScrollPane.setVvalue(vValue + -deltaY / width);
        });
        VBox.setVgrow(attendanceScrollPane, Priority.ALWAYS);
        attendanceInnerPane.prefHeightProperty().bind(attendanceScrollPane.heightProperty().subtract(46D));
        attendanceScrollPane.setFitToWidth(true);
        VBox attendancePane = new VBox(attendanceText, attendanceScrollPane);
        attendancePane.setPadding(new Insets(15));
        attendancePane.setSpacing(25);
        attendancePane.setAlignment(Pos.TOP_CENTER);
        //</editor-fold>

        //Setup assignments pane
        //<editor-fold desc="Assignments Pane">
        VBox assignmentsPane = new VBox();
        assignmentsPane.setSpacing(20);
        assignmentsPane.setPadding(new Insets(15));
        assignmentsPane.setAlignment(Pos.CENTER);
        //</editor-fold>

        //Setup cafe pane
        //<editor-fold desc="Cafe Pane">
        VBox cafePane = new VBox();
        cafePane.setSpacing(20);
        cafePane.setPadding(new Insets(15));
        cafePane.setAlignment(Pos.CENTER);
        //</editor-fold>

        //Setup chatRoom pane
        //<editor-fold desc="ChatRoom Pane">
        VBox chatRoomPane = new VBox();
        cafePane.setSpacing(20);
        cafePane.setPadding(new Insets(15));
        cafePane.setAlignment(Pos.CENTER);
        //</editor-fold>

        //Setup side tab pane
        //<editor-fold desc="Side Tab Pane">
        SideTab homeSideTab = new SideTab("M302.93,149.794c5.561-6.116,5.024-15.49-1.199-20.932L164.63,8.898   c-6.223-5.442-16.2-5.328-22.292,0.257L4.771,135.258c-6.092,5.585-6.391,14.947-0.662,20.902l3.449,3.592   c5.722,5.955,14.971,6.665,20.645,1.581l10.281-9.207v134.792c0,8.27,6.701,14.965,14.965,14.965h53.624   c8.264,0,14.965-6.695,14.965-14.965v-94.3h68.398v94.3c-0.119,8.264,5.794,14.959,14.058,14.959h56.828   c8.264,0,14.965-6.695,14.965-14.965V154.024c0,0,2.84,2.488,6.343,5.567c3.497,3.073,10.842,0.609,16.403-5.513L302.93,149.794z", 26, 32, "Home", homePane);
        SideTab classSideTab = new SideTab("M585.143 658.286v269.714q12.571-8 20.571-16l233.143-233.143q8-8 16-20.571h-269.714zM512 640q0-22.857 16-38.857t38.857-16h310.857v-603.429q0-22.857-16-38.857t-38.857-16h-768q-22.857 0-38.857 16t-16 38.857v914.286q0 22.857 16 38.857t38.857 16h457.143v-310.857z", 26, 32, "Class", classPane);
        SideTab rosterSideTab = new SideTab("M292.571 164.571v109.714q0 8-5.143 13.143t-13.143 5.143h-182.857q-8 0-13.143-5.143t-5.143-13.143v-109.714q0-8 5.143-13.143t13.143-5.143h182.857q8 0 13.143 5.143t5.143 13.143zM292.571 384v109.714q0 8-5.143 13.143t-13.143 5.143h-182.857q-8 0-13.143-5.143t-5.143-13.143v-109.714q0-8 5.143-13.143t13.143-5.143h182.857q8 0 13.143 5.143t5.143 13.143zM585.143 164.571v109.714q0 8-5.143 13.143t-13.143 5.143h-182.857q-8 0-13.143-5.143t-5.143-13.143v-109.714q0-8 5.143-13.143t13.143-5.143h182.857q8 0 13.143 5.143t5.143 13.143zM292.571 603.428v109.714q0 8-5.143 13.143t-13.143 5.143h-182.857q-8 0-13.143-5.143t-5.143-13.143v-109.714q0-8 5.143-13.143t13.143-5.143h182.857q8 0 13.143 5.143t5.143 13.143zM585.143 384v109.714q0 8-5.143 13.143t-13.143 5.143h-182.857q-8 0-13.143-5.143t-5.143-13.143v-109.714q0-8 5.143-13.143t13.143-5.143h182.857q8 0 13.143 5.143t5.143 13.143zM877.714 164.571v109.714q0 8-5.143 13.143t-13.143 5.143h-182.857q-8 0-13.143-5.143t-5.143-13.143v-109.714q0-8 5.143-13.143t13.143-5.143h182.857q8 0 13.143 5.143t5.143 13.143zM585.143 603.428v109.714q0 8-5.143 13.143t-13.143 5.143h-182.857q-8 0-13.143-5.143t-5.143-13.143v-109.714q0-8 5.143-13.143t13.143-5.143h182.857q8 0 13.143 5.143t5.143 13.143zM877.714 384v109.714q0 8-5.143 13.143t-13.143 5.143h-182.857q-8 0-13.143-5.143t-5.143-13.143v-109.714q0-8 5.143-13.143t13.143-5.143h182.857q8 0 13.143 5.143t5.143 13.143zM877.714 603.428v109.714q0 8-5.143 13.143t-13.143 5.143h-182.857q-8 0-13.143-5.143t-5.143-13.143v-109.714q0-8 5.143-13.143t13.143-5.143h182.857q8 0 13.143 5.143t5.143 13.143zM950.857 786.286v-621.714q0-37.714-26.857-64.571t-64.571-26.857h-768q-37.714 0-64.571 26.857t-26.857 64.571v621.714q0 37.714 26.857 64.571t64.571 26.857h768q37.714 0 64.571-26.857t26.857-64.571z", 32, 32, "Timetable", timetablePane);
        SideTab resultsSideTab = new SideTab("M207.429 73.143l52 52-134.286 134.286-52-52v-61.143h73.143v-73.143h61.143zM506.286 603.428q0 12.571-12.571 12.571-5.714 0-9.714-4l-309.714-309.714q-4-4-4-9.714 0-12.571 12.571-12.571 5.714 0 9.714 4l309.714 309.714q4 4 4 9.714zM475.429 713.143l237.714-237.714-475.429-475.429h-237.714v237.714zM865.714 658.286q0-30.286-21.143-51.429l-94.857-94.857-237.714 237.714 94.857 94.286q20.571 21.714 51.429 21.714 30.286 0 52-21.714l134.286-133.714q21.143-22.286 21.143-52z", 32, 32, "Results", resultsPane);
        SideTab noticeboardSideTab = new SideTab("M544 960l-96-96 96-96-224-256h-224l176-176-272-360.616v-39.384h39.384l360.616 272 176-176v224l256 224 96-96 96 96-480 480zM448 416l-64 64 224 224 64-64-224-224z", 32, 32, "Noticeboard", noticeboardPane);
        SideTab contactSideTab = new SideTab("M192 960v-1024h768v1024h-768zM576 703.67c70.51 0 127.67-57.16 127.67-127.67s-57.16-127.67-127.67-127.67-127.67 57.16-127.67 127.67 57.16 127.67 127.67 127.67v0zM768 192h-384v64c0 70.696 57.306 128 128 128v0h128c70.696 0 128-57.304 128-128v-64zM64 896h96v-192h-96v192zM64 640h96v-192h-96v192zM64 384h96v-192h-96v192zM64 128h96v-192h-96v192z", 32, 32, "Contact", contactPane);
        SideTab importantDatesSideTab = new SideTab("M73.143 0h164.571v164.571h-164.571v-164.571zM274.286 0h182.857v164.571h-182.857v-164.571zM73.143 201.143h164.571v182.857h-164.571v-182.857zM274.286 201.143h182.857v182.857h-182.857v-182.857zM73.143 420.571h164.571v164.571h-164.571v-164.571zM493.714 0h182.857v164.571h-182.857v-164.571zM274.286 420.571h182.857v164.571h-182.857v-164.571zM713.143 0h164.571v164.571h-164.571v-164.571zM493.714 201.143h182.857v182.857h-182.857v-182.857zM292.571 694.857v164.571q0 7.429-5.429 12.857t-12.857 5.429h-36.571q-7.429 0-12.857-5.429t-5.429-12.857v-164.571q0-7.429 5.429-12.857t12.857-5.429h36.571q7.429 0 12.857 5.429t5.429 12.857zM713.143 201.143h164.571v182.857h-164.571v-182.857zM493.714 420.571h182.857v164.571h-182.857v-164.571zM713.143 420.571h164.571v164.571h-164.571v-164.571zM731.429 694.857v164.571q0 7.429-5.429 12.857t-12.857 5.429h-36.571q-7.429 0-12.857-5.429t-5.429-12.857v-164.571q0-7.429 5.429-12.857t12.857-5.429h36.571q7.429 0 12.857 5.429t5.429 12.857zM950.857 731.428v-731.429q0-29.714-21.714-51.429t-51.429-21.714h-804.571q-29.714 0-51.429 21.714t-21.714 51.429v731.429q0 29.714 21.714 51.429t51.429 21.714h73.143v54.857q0 37.714 26.857 64.571t64.571 26.857h36.571q37.714 0 64.571-26.857t26.857-64.571v-54.857h219.429v54.857q0 37.714 26.857 64.571t64.571 26.857h36.571q37.714 0 64.571-26.857t26.857-64.571v-54.857h73.143q29.714 0 51.429-21.714t21.714-51.429z", 32, 32, "Important Dates", importantDatesPane);
        SideTab attendanceTab = new SideTab("M960 352l-288-288-96 96-64-64 160-160 352 352zM448 192h320v115.128c-67.22 39.2-156.308 66.11-256 74.26v52.78c70.498 39.728 128 138.772 128 237.832 0 159.058 0 288-192 288s-192-128.942-192-288c0-99.060 57.502-198.104 128-237.832v-52.78c-217.102-17.748-384-124.42-384-253.388h448v64z", 32, 32, "Attendance", attendancePane);
        SideTab assignmentsTab = new SideTab("M45,25H20c-0.552,0-1,0.447-1,1s0.448,1,1,1h25c0.552,0,1-0.447,1-1S45.552,25,45,25z", 32, 32, "Assignments", assignmentsPane);
        SideTab cafeTab = new SideTab("M423.892,229.581h-21.406v-43.154H49.378v82.953c0,97.352,79.202,176.554,176.554,176.554    c62.029,0,116.683-32.159,148.182-80.675h49.777c37.406,0,67.839-30.433,67.839-67.839    C491.731,260.015,461.298,229.581,423.892,229.581z M423.892,331.867h-32.839c7.379-19.436,11.433-40.496,11.433-62.487v-6.408    h21.406c18.994,0,34.448,15.453,34.448,34.448S442.886,331.867,423.892,331.867z", 32, 32, "Cafe", cafePane);
        SideTab chatRoomTab = new SideTab("M403.581,69.3c-44.7-44.7-104-69.3-167.2-69.3s-122.5,24.6-167.2,69.3c-86.4,86.4-92.4,224.7-14.9,318    c-7.6,15.3-19.8,33.1-37.9,42c-8.7,4.3-13.6,13.6-12.1,23.2s8.9,17.1,18.5,18.6c4.5,0.7,10.9,1.4,18.7,1.4    c20.9,0,51.7-4.9,83.2-27.6c35.1,18.9,73.5,28.1,111.6,28.1c61.2,0,121.8-23.7,167.4-69.3c44.7-44.7,69.3-104,69.3-167.2    S448.281,114,403.581,69.3z M384.481,384.6c-67.5,67.5-172,80.9-254.2,32.6c-5.4-3.2-12.1-2.2-16.4,2.1c-0.4,0.2-0.8,0.5-1.1,0.8    c-27.1,21-53.7,25.4-71.3,25.4h-0.1c20.3-14.8,33.1-36.8,40.6-53.9c1.2-2.9,1.4-5.9,0.7-8.7c-0.3-2.7-1.4-5.4-3.3-7.6    c-73.2-82.7-69.4-208.7,8.8-286.9c81.7-81.7,214.6-81.7,296.2,0C466.181,170.1,466.181,302.9,384.481,384.6z", 32, 32, "ChatRoom", chatRoomPane);
        SideTab settingsSideTab = new SideTab("M585.143 438.857q0 60.571-42.857 103.429t-103.429 42.857-103.429-42.857-42.857-103.429 42.857-103.429 103.429-42.857 103.429 42.857 42.857 103.429zM877.714 501.143v-126.857q0-6.857-4.571-13.143t-11.429-7.429l-105.714-16q-10.857-30.857-22.286-52 20-28.571 61.143-78.857 5.714-6.857 5.714-14.286t-5.143-13.143q-15.429-21.143-56.571-61.714t-53.714-40.571q-6.857 0-14.857 5.143l-78.857 61.714q-25.143-13.143-52-21.714-9.143-77.714-16.571-106.286-4-16-20.571-16h-126.857q-8 0-14 4.857t-6.571 12.286l-16 105.143q-28 9.143-51.429 21.143l-80.571-61.143q-5.714-5.143-14.286-5.143-8 0-14.286 6.286-72 65.143-94.286 96-4 5.714-4 13.143 0 6.857 4.571 13.143 8.571 12 29.143 38t30.857 40.286q-15.429 28.571-23.429 56.571l-104.571 15.429q-7.429 1.143-12 7.143t-4.571 13.429v126.857q0 6.857 4.571 13.143t10.857 7.429l106.286 16q8 26.286 22.286 52.571-22.857 32.571-61.143 78.857-5.714 6.857-5.714 13.714 0 5.714 5.143 13.143 14.857 20.571 56.286 61.429t54 40.857q7.429 0 14.857-5.714l78.857-61.143q25.143 13.143 52 21.714 9.143 77.714 16.571 106.286 4 16 20.571 16h126.857q8 0 14-4.857t6.571-12.286l16-105.143q28-9.143 51.429-21.143l81.143 61.143q5.143 5.143 13.714 5.143 7.429 0 14.286-5.714 73.714-68 94.286-97.143 4-4.571 4-12.571 0-6.857-4.571-13.143-8.571-12-29.143-38t-30.857-40.286q14.857-28.571 23.429-56l104.571-16q7.429-1.143 12-7.143t4.571-13.429z", 32, 32, "Settings", null);
        sideTabPane = new SideTabPane(connectionHandler, homeSideTab, classSideTab, rosterSideTab, resultsSideTab, noticeboardSideTab, contactSideTab, importantDatesSideTab, attendanceTab, assignmentsTab, cafeTab, chatRoomTab, settingsSideTab);
        sideTabPane.setParent(stage);
        //</editor-fold>

        //Setup top pane
        //<editor-fold desc="Top Pane">
        JFXHamburger jfxHamburger = new JFXHamburger();
        jfxHamburger.setStyle("-fx-color: white");
        HamburgerBackArrowBasicTransition backArrowBasicTransition = new HamburgerBackArrowBasicTransition(jfxHamburger);
        backArrowBasicTransition.setRate(-1);
        jfxHamburger.setOnMouseClicked(e -> {
            backArrowBasicTransition.setRate(backArrowBasicTransition.getRate() * -1);
            backArrowBasicTransition.play();
            sideTabPane.setExtended(!sideTabPane.getExtended());
        });
        ImageView logoImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("SLLogo.png")));
        logoImageView.setFitHeight(32);
        logoImageView.setFitWidth(32);
        Label campusLabel = new Label(connectionHandler.connectionType);
        campusLabel.getStyleClass().add("top-pane-text");
        HBox campusPane = new HBox(campusLabel);
        Label connectionTypeLabel = new Label();
        connectionTypeLabel.getStyleClass().add("top-pane-text");
        HBox connectionTypePane = new HBox(connectionTypeLabel);
        connectionTypePane.setAlignment(Pos.CENTER);
        lecturerInfoLabel = new Label();
        lecturerInfoLabel.getStyleClass().add("top-pane-text");
        HBox studentInfoPane = new HBox(lecturerInfoLabel);
        studentInfoPane.setAlignment(Pos.CENTER_RIGHT);
        HBox topPane = new HBox(jfxHamburger, logoImageView, campusLabel, connectionTypePane, studentInfoPane);
        campusPane.minWidthProperty().bind(lecturerInfoLabel.widthProperty().subtract(107D));
        campusPane.maxWidthProperty().bind(lecturerInfoLabel.widthProperty().subtract(107D));
        HBox.setHgrow(connectionTypePane, Priority.ALWAYS);
        topPane.getStyleClass().add("top-pane");
        //</editor-fold>

        //Setup background pane
        //<editor-fold desc="Background Pane">
        backgroundPane = new StackPane();
        backgroundPane.getStyleClass().add("background-pane");
        backgroundPane.setEffect(new GaussianBlur(50));
        //</editor-fold>

        //Setup lecturer pane
        //<editor-fold desc="Lecturer Pane">
        lecturerPane = new VBox(topPane, sideTabPane);
        VBox.setVgrow(sideTabPane, Priority.ALWAYS);
        //</editor-fold>

        //Setup content pane
        //<editor-fold desc="Content Pane">
        contentPane = new StackPane(backgroundPane, loginPane);
        //</editor-fold>

        //Setup lecturer update listener
        //<editor-fold desc="Lecturer Update Listener">
        connectionHandler.lecturer.update.addListener((observable, oldV, newV) -> {
            if (newV) {
                connectionHandler.lecturer.update.set(false);
                if (connectionHandler.lecturer.getLecturer() != null && connectionHandler.lecturer.getLecturer().getClasses() != null) {
                    Platform.runLater(() -> {
                        String prevSelected = null;
                        if (!classSelectComboBox.getSelectionModel().isEmpty()) {
                            prevSelected = classSelectComboBox.getSelectionModel().getSelectedItem().getModuleNumber();
                        }
                        classAndResults.clear();
                        classAndResults.addAll(connectionHandler.lecturer.getLecturer().getClasses());
                        if (prevSelected == null) {
                            SetTimeSlot:
                            if (!classSelectComboBox.getItems().isEmpty()) {
                                int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
                                for (LecturerClass car : classSelectComboBox.getItems()) {
                                    for (ClassTime ct : car.getClassTimes()) {
                                        if (ct.getDayOfWeek() == dayOfWeek && currentTimeslot.get() >= ct.getStartSlot() && currentTimeslot.get() <= ct.getEndSlot()) {
                                            classSelectComboBox.getSelectionModel().select(car);
                                            break SetTimeSlot;
                                        }
                                    }
                                }
                                classSelectComboBox.getSelectionModel().select(0);
                            }
                        } else {
                            for (LecturerClass car : classAndResults) {
                                if (car.getModuleNumber().equals(prevSelected)) {
                                    classSelectComboBox.getSelectionModel().select(car);
                                }
                            }
                        }
                        Lecturer lecturer = connectionHandler.lecturer.getLecturer();
                        lecturerInfoLabel.setText(lecturer.getFirstName() + " " + lecturer.getLastName() + " " + lecturer.getLecturerNumber());
                        populateTimetable();
                        populateContactDetails();
                        populateImportantDates();
                    });
                }
            }
        });
        //</editor-fold>

        //Setup noticeboard update listener
        //<editor-fold desc="Noticeboard Update Listener">
        connectionHandler.notices.addListener((InvalidationListener) e -> {
            populateNoticeBoard();
        });
        //</editor-fold>

        connectionHandler.results.addListener((InvalidationListener) e -> {
            //TODO
            resultsInnerPane.getChildren().clear();
            if (!connectionHandler.results.isEmpty()) {
                for (LecturerStudentResult lsr : connectionHandler.results) {
                    Label heading = new Label(lsr.getStudentFirstName() + " - " + lsr.getStudentNumber());
                    heading.setStyle("-fx-font-size: 24;" +
                                    "-fx-text-fill: white;");
                    VBox resultStudentPane = new VBox(heading);
                    for (LecturerStudentResultClass lsrc : lsr.getClasses()) {
                        resultStudentPane.getChildren().add(new ResultPane(lsrc.getModuleName(), lsrc.getResults()));
                    }
                    resultStudentPane.setAlignment(Pos.CENTER);
                    resultStudentPane.setSpacing(15);
                    resultsInnerPane.getChildren().add(resultStudentPane);
                }
            }
        });

        connectionHandler.attendance.addListener((InvalidationListener) e -> {
            //TODO
            Platform.runLater(() -> {
                attendanceInnerPane.getChildren().clear();
                if (!connectionHandler.attendance.isEmpty()) {
                    for (LecturerStudentAttendance lsa : connectionHandler.attendance) {
                        Label heading = new Label(lsa.getStudentFirstName() + " - " + lsa.getStudentNumber());
                        heading.setStyle("-fx-font-size: 24;" +
                                        "-fx-text-fill: white;");
                        VBox attendanceStudentPane = new VBox(heading);
                        for (LecturerStudentAttendanceClass lsac : lsa.getClasses()) {
                            attendanceStudentPane.getChildren().add(new AttendanceCard(lsac.getModuleName(), lsac.getAttendance()));
                        }
                        attendanceStudentPane.setAlignment(Pos.CENTER);
                        attendanceStudentPane.setSpacing(15);
                        attendanceInnerPane.getChildren().add(attendanceStudentPane);
                    }
                }
            });
        });

        //Setup contact details update listener
        //<editor-fold desc="Contact Details Update Listener">
        connectionHandler.contactDetails.addListener((InvalidationListener) e -> {
            populateContactDetails();
        });
        //</editor-fold>

        //Setup important dates update listener
        //<editor-fold desc="Important Dates Update Listener">
        connectionHandler.importantDates.addListener((InvalidationListener) e -> {
            populateImportantDates();
        });
        //</editor-fold>

        //Setup stage close listener
        //<editor-fold desc="Stage Close Listener">
        stage.setOnCloseRequest(e -> System.exit(0));
        //</editor-fold>

        //Setup login pane animation
        //<editor-fold desc="Login Pane animation">
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), loginPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
        //</editor-fold>

        //Setup scene
        //<editor-fold desc="Scene">
        Scene scene = new Scene(contentPane);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("StudentLiveStyle.css").toExternalForm());
        //</editor-fold>

        //Start timeslot updater
        //<editor-fold desc="Timeslot Updater">
        new TimeslotUpdater().start();
        //</editor-fold>

        //Set and display scene
        //<editor-fold desc="Display Scene">
        stage.setScene(scene);
        stage.show();
        //</editor-fold>
    }

    void checkAttendance() {
        if (!classSelectComboBox.getSelectionModel().isEmpty() && isClassTime(classSelectComboBox.getSelectionModel().getSelectedItem()) && !connectionHandler.hasAttendance(classSelectComboBox.getSelectionModel().getSelectedItem().getId())) { //has to do attendance
            if (!classHeadingPane.getChildren().contains(doAttendanceButton)) {
                classHeadingPane.getChildren().add(doAttendanceButton);
            }
        } else if (classHeadingPane.getChildren().contains(doAttendanceButton)) {
            classHeadingPane.getChildren().remove(doAttendanceButton);
        }

    }

    Boolean isClassTime(LecturerClass lecturerClass) {
        int currentTimeSlot = getCurrentTimeSlot();
        for (ClassTime classTime : lecturerClass.getClassTimes()) {
            if (classTime.getDayOfWeek() == Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 && classTime.getStartSlot() <= currentTimeSlot && classTime.getEndSlot() >= currentTimeSlot) {
                return true;
            }
        }
        return false;
    }

    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName.contains(".")) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    public static String getFileExtension(String fileName) {
        if (fileName.contains(".") && fileName.lastIndexOf(".") < fileName.length()) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "N/A";
    }

    private int getCurrentTimeSlot() {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            Date currentDate = parser.parse(parser.format(Calendar.getInstance().getTime()));
            if (currentDate.compareTo(parser.parse("8:00")) >= 0 && currentDate.compareTo(parser.parse("9:00")) < 0) {
                return 1;
            } else if (currentDate.compareTo(parser.parse("9:00")) >= 0 && currentDate.compareTo(parser.parse("10:00")) < 0) {
                return 2;
            } else if (currentDate.compareTo(parser.parse("10:00")) >= 0 && currentDate.compareTo(parser.parse("11:00")) < 0) {
                return 3;
            } else if (currentDate.compareTo(parser.parse("11:00")) >= 0 && currentDate.compareTo(parser.parse("12:00")) < 0) {
                return 4;
            } else if (currentDate.compareTo(parser.parse("12:00")) >= 0 && currentDate.compareTo(parser.parse("13:00")) < 0) {
                return 5;
            } else if (currentDate.compareTo(parser.parse("13:00")) >= 0 && currentDate.compareTo(parser.parse("14:00")) < 0) {
                return 6;
            } else if (currentDate.compareTo(parser.parse("14:00")) >= 0 && currentDate.compareTo(parser.parse("15:00")) < 0) {
                return 7;
            } else if (currentDate.compareTo(parser.parse("15:00")) >= 0 && currentDate.compareTo(parser.parse("16:00")) < 0) {
                return 8;
            } else if (currentDate.compareTo(parser.parse("16:00")) >= 0 && currentDate.compareTo(parser.parse("17:00")) < 0) {
                return 9;
            } else if (currentDate.compareTo(parser.parse("17:00")) >= 0 && currentDate.compareTo(parser.parse("18:00")) < 0) {
                return 10;
            } else if (currentDate.compareTo(parser.parse("18:00")) >= 0 && currentDate.compareTo(parser.parse("18:45")) < 0) {
                return 11;
            } else if (currentDate.compareTo(parser.parse("18:45")) >= 0 && currentDate.compareTo(parser.parse("19:30")) < 0) {
                return 12;
            } else if (currentDate.compareTo(parser.parse("19:30")) >= 0 && currentDate.compareTo(parser.parse("20:15")) < 0) {
                return 13;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private void populateTimetable() {
        List<LecturerClass> classAndResults = classSelectComboBox.getItems();
        timetableGridPane.getChildren().clear();
        String[] weekdays = {"Mo", "Tu", "We", "Th", "Fr"};
        for (int i = 0; i < 5; i++) {
            Label label = new Label(weekdays[i]);
            label.getStyleClass().add("timetable-weekdays");
            HBox labelPane = new HBox(label);
            labelPane.setAlignment(Pos.CENTER);
            timetableGridPane.add(labelPane, 0, i + 1);
        }
        String[] timeSlots = {"08:00 - 08:45", "09:00 - 09:45", "10:00 - 10:45", "11:00 - 11:45", "12:00 - 12:45", "13:00 - 13:45", "14:00 - 14:45", "15:00 - 15:45", "16:00 - 16:45", "17:00 - 17:45", "18:00 - 18:45", "18:45 - 19:30", "19:30 - 20:15"};
        for (int i = 0; i < timeSlots.length; i++) {
            Label label = new Label(timeSlots[i]);
            label.getStyleClass().add("timetable-text");
            label.setAlignment(Pos.CENTER);
            HBox labelPane = new HBox(label);
            labelPane.setAlignment(Pos.CENTER);
            timetableGridPane.add(labelPane, i + 1, 0);
        }
        int classNumber = 0;
        for (LecturerClass cr : classAndResults) {
            String moduleName = cr.getModuleName();
            for (ClassTime ct : cr.getClassTimes()) {
                for (int i = ct.getStartSlot(); i <= ct.getEndSlot(); i++) {
                    TimetableBlock timetableBlock = new TimetableBlock(moduleName + "\n\n(" + ct.getRoomNumber() + ")", classNumber);
                    timetableBlock.setOnMouseClicked(e -> {
                        Platform.runLater(() -> {
                            classSelectComboBox.getSelectionModel().select(cr);
                            sideTabPane.select(0);
                        });
                    });
                    timetableGridPane.add(timetableBlock, i, ct.getDayOfWeek());
                }
            }
            classNumber++;
        }
        timetableGridPane.setGridLinesVisible(false);
        timetableGridPane.setGridLinesVisible(true);
    }

    private void populateNoticeBoard() {
        ObservableList<StackPane> noticePanes = FXCollections.observableArrayList();
        for (Notice nb : connectionHandler.notices) {
            noticePanes.add(new NoticeboardCard(stage, nb.getHeading(), nb.getDescription(), false));
        }
        Platform.runLater(() -> {
            noticeboardInnerPane = new JFXMasonryPane();
            noticeboardInnerPane.getStyleClass().add("noticeboard-pane");
            noticeboardScrollPane.setContent(new StackPane(noticeboardInnerPane));
            if (noticePanes.isEmpty()) {
                noticeboardInnerPane.getChildren().addAll(new NoticeboardCard(stage, "Nothing to show here!", "Watch this space for new information", false));
                noticeboardInnerPane.setHSpacing(10);
                noticeboardInnerPane.setVSpacing(10);
                noticeboardInnerPane.setLayoutMode(JFXMasonryPane.LayoutMode.MASONRY);
                noticeboardInnerPane.prefHeightProperty().bind(noticeboardScrollPane.heightProperty().subtract(2D));
            } else {
                noticeboardInnerPane.getChildren().addAll(noticePanes);
                noticeboardInnerPane.setHSpacing(10);
                noticeboardInnerPane.setVSpacing(10);
                noticeboardInnerPane.setLayoutMode(JFXMasonryPane.LayoutMode.MASONRY);
                noticeboardInnerPane.prefHeightProperty().bind(noticeboardScrollPane.heightProperty().subtract(2D));
            }
        });
    }

    private void populateContactDetails() {
        Platform.runLater(() -> {
            if (connectionHandler.lecturer.getLecturer() != null) {
                ObservableList<ContactDetailsCard> contactDetailsCards = FXCollections.observableArrayList();
                for (ContactDetails contactDetails : connectionHandler.contactDetails) {
                    contactDetailsCards.add(new ContactDetailsCard(stage, contactDetails, connectionHandler.lecturer.getLecturer().getFirstName() + " " + connectionHandler.lecturer.getLecturer().getLastName(), connectionHandler.lecturer.getLecturer().getEmail()));
                }
                contactDetailsCardPane.getChildren().clear();
                contactDetailsCardPane.getChildren().addAll(contactDetailsCards);
            }
        });
    }

    private void populateImportantDates() {
        importantDateListView.setItems(connectionHandler.importantDates);
    }

    private String getBuild() {
        try {
            Scanner scn = new Scanner(new File(APPLICATION_FOLDER.getAbsolutePath() + "/Version.txt"));
            return "(Build " + scn.nextLine() + ")";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "(Build N/A)";
    }

    public class TimeslotUpdater extends Thread {
        @Override
        public void run() {
            while (true) {
                currentTimeslot.set(getCurrentTimeSlot());
                int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
                Platform.runLater(() -> {
                    for (Node node : timetableGridPane.getChildren()) {
                        if (node != null && !(node instanceof Group) && GridPane.getColumnIndex(node) == currentTimeslot.get() && dayOfWeek > 0 && dayOfWeek < 6 && GridPane.getRowIndex(node) == dayOfWeek) {
                            if (node != null && node instanceof TimetableBlock) {
                                ((TimetableBlock) node).setSelected(true);
                            }
                        } else {
                            if (node != null && node instanceof TimetableBlock) {
                                ((TimetableBlock) node).setSelected(false);
                            }
                        }
                    }
                    checkAttendance();
                });
                try {
                    Thread.sleep(5000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        if (!LOCAL_CACHE.exists()) {
            if (!LOCAL_CACHE.mkdirs()) {
                System.exit(0);
            }
        }
        if (args.length == 1) {
            ConnectionHandler.LOCAL_ADDRESS = args[0];
        }
        launch(args);
    }

}
