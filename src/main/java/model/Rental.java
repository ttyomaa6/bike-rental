package model;

public class Rental {
    private final int id;
    private final int customerId;
    private final int bicycleId;
    private boolean active = true;

    public Rental(int id, int customerId, int bicycleId) {
        this.id = id;
        this.customerId = customerId;
        this.bicycleId = bicycleId;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getBicycleId() {
        return bicycleId;
    }

    public boolean isActive() {
        return active;
    }

    public void close() {
        active = false;
    }
}
