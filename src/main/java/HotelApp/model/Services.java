package HotelApp.model;

import javafx.beans.property.*;

public class Services {

    private final StringProperty serviceId = new SimpleStringProperty();
    private final StringProperty serviceName = new SimpleStringProperty();
    private final IntegerProperty servicePrice = new SimpleIntegerProperty();
    private final StringProperty serviceDescription = new SimpleStringProperty();

    public Services() {
    }

    public Services(String serviceId, String serviceName, int servicePrice, String serviceDescription) {
        this.serviceId.set(serviceId);
        this.serviceName.set(serviceName);
        this.servicePrice.set(servicePrice);
        this.serviceDescription.set(serviceDescription);
    }

    // --- Properties ---
    public StringProperty serviceIdProperty() {
        return serviceId;
    }

    public StringProperty serviceNameProperty() {
        return serviceName;
    }

    public IntegerProperty servicePriceProperty() {
        return servicePrice;
    }

    public StringProperty serviceDescriptionProperty() {
        return serviceDescription;
    }

    // --- Getters and Setters ---
    public String getServiceId() {
        return serviceId.get();
    }

    public void setServiceId(String serviceId) {
        this.serviceId.set(serviceId);
    }

    public String getServiceName() {
        return serviceName.get();
    }

    public void setServiceName(String serviceName) {
        this.serviceName.set(serviceName);
    }

    public int getServicePrice() {
        return servicePrice.get();
    }

    public void setServicePrice(int servicePrice) {
        this.servicePrice.set(servicePrice);
    }

    public String getServiceDescription() {
        return serviceDescription.get();
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription.set(serviceDescription);
    }
}
