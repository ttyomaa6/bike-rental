package service;

import model.Bicycle;
import model.BicycleStatus;
import model.Customer;
import model.Rental;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.RentalRepository;

public class RentalService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RentalService.class);

    private final RentalRepository repository;

    public RentalService(RentalRepository repository) {
        this.repository = repository;
    }

    public Rental rent(Customer customer, Bicycle bicycle) {
        LOGGER.info("Получен запрос на аренду: клиент {} ({}), велосипед {} ({})",
                customer.getId(), customer.getName(), bicycle.getId(), bicycle.getModel());

        if (bicycle.getStatus() == BicycleStatus.MAINTENANCE) {
            LOGGER.warn("Отказ в аренде: велосипед {} находится на обслуживании", bicycle.getId());
            throw new IllegalStateException("Велосипед находится на обслуживании и не может быть выдан");
        }
        if (bicycle.getStatus() == BicycleStatus.RENTED || repository.hasActiveRental(bicycle.getId())) {
            LOGGER.warn("Отказ в аренде: велосипед {} уже арендован, клиент {} не может его получить",
                    bicycle.getId(), customer.getId());
            throw new IllegalStateException("Велосипед уже арендован и не может быть выдан");
        }

        Rental rental = new Rental(repository.nextId(), customer.getId(), bicycle.getId());
        try {
            repository.save(rental);
        } catch (RuntimeException exception) {
            LOGGER.error("Ошибка создания аренды {}: не удалось сохранить аренду велосипеда {} для клиента {}",
                    rental.getId(), bicycle.getId(), customer.getId(), exception);
            throw exception;
        }
        bicycle.setStatus(BicycleStatus.RENTED);
        LOGGER.info("Аренда {} создана: велосипед {} выдан клиенту {}",
                rental.getId(), bicycle.getId(), customer.getId());
        return rental;
    }

    public void returnBicycle(Bicycle bicycle) {
        LOGGER.info("Получен запрос на возврат велосипеда {}", bicycle.getId());
        if (!repository.hasActiveRental(bicycle.getId())) {
            LOGGER.warn("Отказ в возврате: велосипед {} не находится в аренде", bicycle.getId());
            throw new IllegalStateException("Велосипед не находится в аренде и не может быть возвращен");
        }
        repository.closeActiveRental(bicycle.getId());
        bicycle.setStatus(BicycleStatus.AVAILABLE);
        LOGGER.info("Велосипед {} возвращён, аренда закрыта", bicycle.getId());
    }
}
