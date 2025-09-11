package HotelApp.model;

import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {

    private final StringProperty customerId = new SimpleStringProperty();
    private final StringProperty customerName = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dob = new SimpleObjectProperty<>();
    private final StringProperty citizenIdentityNumber = new SimpleStringProperty();
    private final BooleanProperty isForeigner = new SimpleBooleanProperty();
    private final BooleanProperty gender = new SimpleBooleanProperty();
    private final BooleanProperty isChild = new SimpleBooleanProperty();
    private final StringProperty phoneNum = new SimpleStringProperty();

    public Customer() {
    }

    public Customer(String customerId, String customerName, LocalDate dob, String citizenIdentityNumber,
            boolean isForeigner, boolean gender, boolean isChild, String phoneNum) {
        this.customerId.set(customerId);
        this.customerName.set(customerName);
        this.dob.set(dob);
        this.citizenIdentityNumber.set(citizenIdentityNumber);
        this.isForeigner.set(isForeigner);
        this.gender.set(gender);
        this.isChild.set(isChild);
        this.phoneNum.set(phoneNum);
    }

    // Properties
    public StringProperty customerIdProperty() {
        return customerId;
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public ObjectProperty<LocalDate> dobProperty() {
        return dob;
    }

    public StringProperty citizenIdentityNumberProperty() {
        return citizenIdentityNumber;
    }

    public BooleanProperty isForeignerProperty() {
        return isForeigner;
    }

    public BooleanProperty genderProperty() {
        return gender;
    }

    public BooleanProperty isChildProperty() {
        return isChild;
    }

    public StringProperty phoneNumProperty() {
        return phoneNum;
    }

    // Getters and Setters
    public String getCustomerId() {
        return customerId.get();
    }

    public void setCustomerId(String customerId) {
        this.customerId.set(customerId);
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public LocalDate getDob() {
        return dob.get();
    }

    public void setDob(LocalDate dob) {
        this.dob.set(dob);
    }

    public String getCitizenIdentityNumber() {
        return citizenIdentityNumber.get();
    }

    public void setCitizenIdentityNumber(String citizenIdentityNumber) {
        this.citizenIdentityNumber.set(citizenIdentityNumber);
    }

    public boolean isIsForeigner() {
        return isForeigner.get();
    }

    public void setIsForeigner(boolean isForeigner) {
        this.isForeigner.set(isForeigner);
    }

    public boolean isGender() {
        return gender.get();
    }

    public void setGender(boolean gender) {
        this.gender.set(gender);
    }

    public boolean isIsChild() {
        return isChild.get();
    }

    public void setIsChild(boolean isChild) {
        this.isChild.set(isChild);
    }

    public String getPhoneNum() {
        return phoneNum.get();
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum.set(phoneNum);
    }
}
