package fr.utc.onzzer.server.hmi;

import fr.utc.onzzer.common.dataclass.ModelUpdateTypes;
import fr.utc.onzzer.common.dataclass.TrackLite;
import fr.utc.onzzer.common.dataclass.UserLite;
import fr.utc.onzzer.common.dataclass.communication.SocketMessagesTypes;
import fr.utc.onzzer.server.GlobalController;
import fr.utc.onzzer.server.communication.events.SocketMessageDirection;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainViewController {

    /* Connected users FX elements */
    @FXML
    private TableView tableUsers;

    @FXML
    private TableColumn colUserConnect;

    @FXML
    private TableColumn colUserId;

    @FXML
    private TableColumn colUserName;

    /* Tracks FX elements */
    @FXML
    private TableView tableTracks;

    @FXML
    private TableColumn colTrackId;

    @FXML
    private TableColumn colTrackUserId;

    @FXML
    private TableColumn colTrackTitle;

    @FXML
    private TableColumn colTrackAuthor;

    /* Incoming messages FX elements */
    @FXML
    private TableView tableInMessages;

    @FXML
    private TableColumn colInMessagesDate;

    @FXML
    private TableColumn colInMessagesType;

    @FXML
    private TableColumn colInMessagesFrom;

    @FXML
    private TableColumn colInMessagesObject;

    /* Outcoming messages FX elements */
    @FXML
    private TableView tableOutMessages;

    @FXML
    private TableColumn colOutMessagesDate;

    @FXML
    private TableColumn colOutMessagesType;

    @FXML
    private TableColumn colOutMessagesTo;

    @FXML
    private TableColumn colOutMessagesObject;

    @FXML
    private CheckBox chkInShowPing;

    @FXML
    private CheckBox chkOutShowPing;

    /* General attributes */
    private final GlobalController controller;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private ArrayList<MessageTable> inMessages = new ArrayList<>();

    private ArrayList<MessageTable> outMessages = new ArrayList<>();

    @FXML
    public void initialize() {
        colUserConnect.setCellValueFactory(new PropertyValueFactory<>("connected"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("username"));

        colTrackId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTrackUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colTrackTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTrackAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));

        colInMessagesDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colInMessagesType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colInMessagesFrom.setCellValueFactory(new PropertyValueFactory<>("from"));
        colInMessagesObject.setCellValueFactory(new PropertyValueFactory<>("object"));

        colOutMessagesDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colOutMessagesType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colOutMessagesTo.setCellValueFactory(new PropertyValueFactory<>("from"));
        colOutMessagesObject.setCellValueFactory(new PropertyValueFactory<>("object"));

        chkInShowPing.setOnMouseClicked(e -> {
            this.updateInMessages();
        });

        chkOutShowPing.setOnMouseClicked(e -> {
            this.updateOutMessages();
        });
    }

    public static class MessageTable {
        private final SimpleStringProperty date;
        private final SimpleStringProperty type;
        private final SimpleStringProperty from;
        private final SimpleStringProperty object;

        public MessageTable(String date, String type, String from, String object) {
            this.date = new SimpleStringProperty(date);
            this.type = new SimpleStringProperty(type);
            this.from = new SimpleStringProperty(from);
            this.object = new SimpleStringProperty(object);
        }

        public String getDate() {
            return date.get();
        }

        public SimpleStringProperty dateProperty() {
            return date;
        }

        public String getType() {
            return type.get();
        }

        public SimpleStringProperty typeProperty() {
            return type;
        }

        public String getFrom() {
            return from.get();
        }

        public SimpleStringProperty fromProperty() {
            return from;
        }

        public String getObject() {
            return object.get();
        }

        public SimpleStringProperty objectProperty() {
            return object;
        }
    }

    public static class TrackTable {
        private final SimpleStringProperty id;
        private final SimpleStringProperty userId;
        private final SimpleStringProperty title;
        private final SimpleStringProperty author;

        public TrackTable(String id, String userId, String title, String author) {
            this.id = new SimpleStringProperty(id);
            this.userId = new SimpleStringProperty(userId);
            this.title = new SimpleStringProperty(title);
            this.author = new SimpleStringProperty(author);
        }

        public String getId() {
            return id.get();
        }

        public SimpleStringProperty idProperty() {
            return id;
        }

        public String getUserId() {
            return userId.get();
        }

        public SimpleStringProperty userIdProperty() {
            return userId;
        }

        public String getTitle() {
            return title.get();
        }

        public SimpleStringProperty titleProperty() {
            return title;
        }

        public String getAuthor() {
            return author.get();
        }

        public SimpleStringProperty authorProperty() {
            return author;
        }
    }

    public static class UserTable {
        private final SimpleStringProperty connected;
        private final SimpleStringProperty id;
        private final SimpleStringProperty username;

        public UserTable(String connected, String id, String username) {
            this.connected = new SimpleStringProperty(connected);
            this.id = new SimpleStringProperty(id);
            this.username = new SimpleStringProperty(username);
        }

        public String getConnected() {
            return connected.get();
        }

        public SimpleStringProperty connectedProperty() {
            return connected;
        }

        public String getId() {
            return id.get();
        }

        public SimpleStringProperty idProperty() {
            return id;
        }

        public String getUsername() {
            return username.get();
        }

        public SimpleStringProperty usernameProperty() {
            return username;
        }
    }

    private void updateUsers() {
        tableUsers.getItems().clear();
        controller.getDataServicesProvider().getDataUserServices().getAllUsers().forEach(user -> {

            String connected = dtf.format(LocalDateTime.now());
            String id = user.getId().toString();
            String username = user.getUsername();

            tableUsers.getItems().add(new UserTable(connected, id, username));
        });
    }

    private void updateTracks() {
        tableTracks.getItems().clear();
        controller.getDataServicesProvider().getDataTrackServices().getAllTracks().forEach(track -> {

                String id = track.getId().toString();
                String userId = track.getUserId().toString();
                String title = track.getTitle();
                String author = track.getAuthor();

                tableTracks.getItems().add(new TrackTable(id, userId, title, author));
        });
    }

    private void updateInMessages() {
        tableInMessages.getItems().clear();
        inMessages.forEach(messageTable -> {
            if (!this.chkInShowPing.isSelected() && messageTable.getType().equals(SocketMessagesTypes.USER_PING.toString()))
                return;

            tableInMessages.getItems().add(messageTable);
        });
    }

    private void updateOutMessages() {
        tableOutMessages.getItems().clear();
        outMessages.forEach(messageTable -> {
            if (!this.chkOutShowPing.isSelected() && messageTable.getType().equals(SocketMessagesTypes.SERVER_PING.toString()))
                return;

            tableOutMessages.getItems().add(messageTable);
        });
    }

    public MainViewController(final GlobalController controller) {
        this.controller = controller;

        controller.getComServicesProvider().addNetworkListener(
                (senderSocketMessage) -> {
                    String date = this.dtf.format(LocalDateTime.now());
                    String type = senderSocketMessage.message().messageType.toString();

                    String from = "Unknow";
                    if (senderSocketMessage.sender().getUser() != null)
                        from = senderSocketMessage.sender().getUser().getUsername();

                    String object = "None";
                    if (senderSocketMessage.message().object != null)
                        object = senderSocketMessage.message().object.toString();

                    MessageTable messageTable = new MessageTable(date, type, from, object);

                    inMessages.add(messageTable);

                    if (!this.chkInShowPing.isSelected() && type.equals(SocketMessagesTypes.USER_PING.toString()))
                        return;

                    tableInMessages.getItems().add(messageTable);
                },
                SocketMessageDirection.IN
        );

        controller.getComServicesProvider().addNetworkListener(
                (senderSocketMessage) -> {
                    String date = this.dtf.format(LocalDateTime.now());
                    String type = senderSocketMessage.message().messageType.toString();

                    String to = "Unknow";
                    if (senderSocketMessage.sender().getUser() != null)
                        to = senderSocketMessage.sender().getUser().getUsername();

                    String object = "None";
                    if (senderSocketMessage.message().object != null)
                        object = senderSocketMessage.message().object.toString();

                    MessageTable messageTable = new MessageTable(date, type, to, object);

                    // Ajouter le message à la table
                    outMessages.add(messageTable);

                    if (!this.chkOutShowPing.isSelected() && type.equals(SocketMessagesTypes.SERVER_PING.toString()))
                        return;

                    tableOutMessages.getItems().add(messageTable);
                },
                SocketMessageDirection.OUT
        );

        controller.getDataServicesProvider().getDataUserServices().addListener((user) -> updateUsers(), UserLite.class, ModelUpdateTypes.NEW_USER);
        controller.getDataServicesProvider().getDataUserServices().addListener((user) -> updateUsers(), UserLite.class, ModelUpdateTypes.UPDATE_USER);
        controller.getDataServicesProvider().getDataUserServices().addListener((user) -> updateUsers(), UserLite.class, ModelUpdateTypes.DELETE_USER);

        controller.getDataServicesProvider().getDataTrackServices().addListener((track) -> updateTracks(), TrackLite.class, ModelUpdateTypes.NEW_TRACK);
        controller.getDataServicesProvider().getDataTrackServices().addListener((track) -> updateTracks(), TrackLite.class, ModelUpdateTypes.UPDATE_TRACK);
        controller.getDataServicesProvider().getDataTrackServices().addListener((track) -> updateTracks(), TrackLite.class, ModelUpdateTypes.DELETE_TRACK);
    }
}
