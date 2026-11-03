package service;

import model.Bicycle;
import model.BicycleStatus;
import model.Customer;
import model.Rental;
import repository.RentalRepository;

public class RentalService {
    private final RentalRepository repository;

    public RentalService(RentalRepository repository) {
        this.repository = repository;
    }

    public Rental rent(Customer customer, Bicycle bicycle) {
        if (bicycle.getStatus() == BicycleStatus.MAINTENANCE) {
            throw new IllegalStateException("Велосипед находится на обслуживании и не может быть выдан");
        }
        if (bicycle.getStatus() == BicycleStatus.RENTED || repository.hasActiveRental(bicycle.getId())) {
            throw new IllegalStateException("Велосипед уже арендован и не может быть выдан");
        }

        Rental rental = new Rental(repository.nextId(), customer.getId(), bicycle.getId());
        repository.save(rental);
        bicycle.setStatus(BicycleStatus.RENTED);
        return rental;
    }

    public void returnBicycle(Bicycle bicycle) {
        if (!repository.hasActiveRental(bicycle.getId())) {
            throw new IllegalStateException("Велосипед не находится в аренде и не может быть возвращен");
        }
        repository.closeActiveRental(bicycle.getId());
        bicycle.setStatus(BicycleStatus.AVAILABLE);
    }
}
