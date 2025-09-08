//package HotelApp;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//
//public class CheckInFormController {
//
//    // Table
//    @FXML
//    private TableView<?> tblCheckIn;
//    @FXML
//    private TableColumn<?, ?> colGuestNames;
//    @FXML
//    private TableColumn<?, ?> colRoomNumber;
//    @FXML
//    private TableColumn<?, ?> colCapacity;
//    @FXML
//    private TableColumn<?, ?> colCheckInDate;
//    @FXML
//    private TableColumn<?, ?> colCheckoutDate;
//
//    // Guest info fields
//    @FXML
//    private TextField txtCustomerName;
//    @FXML
//    private DatePicker dpDOB;
//    @FXML
//    private TextField txtCitizenId;
//    @FXML
//    private TextField txtPhone;
//    @FXML
//    private ChoiceBox<String> cbGender;
//    @FXML
//    private ChoiceBox<String> cbNationality;
//    @FXML
//    private CheckBox chkIsChild;
//
//    // Buttons
//    @FXML
//    private Button btnSave;
//    @FXML
//    private Button btnCancel;
//
//    @FXML
//    private void initialize() {
//    }
//
//    @FXML
//    private void onSave() {
//        // TODO: Save guest info to DB (Customer_Management)
//        System.out.println("Saving check-in...");
//    }
//
//    @FXML
//    private void onCancel() {
//        btnCancel.getScene().getWindow().hide();
//    }
//}

package HotelApp;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CheckInFormController {

    @FXML
    private TableView tblGuests;

    @FXML
    private TableColumn colGuestName;

    @FXML
    private TableColumn colRoomNumber;

    @FXML
    private TableColumn colCapacity;

    @FXML
    private TableColumn colType;

    @FXML
    private TableColumn colCheckIn;

    @FXML
    private TableColumn colCheckOut;

    @FXML
    private TextField txtCustomerName;

    @FXML
    private RadioButton rbChildYes;

    @FXML
    private RadioButton rbChildNo;

    @FXML
    private TextField txtDateOfBirth;

    @FXML
    private RadioButton rbForeignerYes;

    @FXML
    private RadioButton rbForeignerNo;

    @FXML
    private TextField txtCitizenId;

    @FXML
    private RadioButton rbMale;

    @FXML
    private RadioButton rbFemale;

    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCheckIn;

}