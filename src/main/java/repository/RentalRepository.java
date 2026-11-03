package repository;

import model.Rental;

public interface RentalRepository {
    void save(Rental rental);

    boolean hasActiveRental(int bicycleId);

    void closeActiveRental(int bicycleId);

    int nextId();
}
