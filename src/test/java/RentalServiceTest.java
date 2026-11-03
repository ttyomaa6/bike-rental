import model.Bicycle;
import model.BicycleStatus;
import model.Customer;
import model.Rental;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InMemoryRentalRepository;
import repository.RentalRepository;
import service.RentalService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RentalServiceTest {
    private RentalRepository repository;
    private RentalService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRentalRepository();
        service = new RentalService(repository);
    }

    @Test
    void rentsAvailableBicycle() {
        Customer customer = new Customer(1, "Анна");
        Bicycle bicycle = new Bicycle(10, "Городской", BicycleStatus.AVAILABLE);

        Rental rental = service.rent(customer, bicycle);

        assertEquals(1, rental.getCustomerId());
        assertEquals(10, rental.getBicycleId());
        assertEquals(BicycleStatus.RENTED, bicycle.getStatus());
        assertTrue(repository.hasActiveRental(10));
    }

    @Test
    void doesNotRentAlreadyRentedBicycle() {
        Customer first = new Customer(1, "Анна");
        Customer second = new Customer(2, "Иван");
        Bicycle bicycle = new Bicycle(10, "Городской", BicycleStatus.AVAILABLE);
        service.rent(first, bicycle);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.rent(second, bicycle));

        assertTrue(exception.getMessage().contains("уже арендован"));
    }

    @Test
    void doesNotRentBicycleUnderMaintenance() {
        Customer customer = new Customer(1, "Анна");
        Bicycle bicycle = new Bicycle(11, "Горный", BicycleStatus.MAINTENANCE);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.rent(customer, bicycle));

        assertTrue(exception.getMessage().contains("обслуживании"));
        assertFalse(repository.hasActiveRental(11));
        assertEquals(BicycleStatus.MAINTENANCE, bicycle.getStatus());
    }
}
