package repository;

import model.Rental;

import java.util.ArrayList;
import java.util.List;

public class InMemoryRentalRepository implements RentalRepository {
    private final List<Rental> rentals = new ArrayList<>();
    private int nextId = 1;

    @Override
    public void save(Rental rental) {
        rentals.add(rental);
    }

    @Override
    public boolean hasActiveRental(int bicycleId) {
        for (Rental rental : rentals) {
            if (rental.getBicycleId() == bicycleId && rental.isActive()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void closeActiveRental(int bicycleId) {
        for (Rental rental : rentals) {
            if (rental.getBicycleId() == bicycleId && rental.isActive()) {
                rental.close();
                return;
            }
        }
    }

    @Override
    public int nextId() {
        return nextId++;
    }
}
