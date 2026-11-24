package model;

public class Bicycle {
    private final int id;
    private final String model;
    private BicycleStatus status;

    public Bicycle(int id, String model, BicycleStatus status) {
        this.id = id;
        this.model = model;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public BicycleStatus getStatus() {
        return status;
    }

    public void setStatus(BicycleStatus status) {
        this.status = status;
    }
}
