import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import model.Bicycle;
import model.BicycleStatus;
import model.Customer;
import model.Rental;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
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
    private Logger serviceLogger;
    private ListAppender<ILoggingEvent> logEvents;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRentalRepository();
        service = new RentalService(repository);
        serviceLogger = (Logger) LoggerFactory.getLogger(RentalService.class);
        logEvents = new ListAppender<>();
        logEvents.start();
        serviceLogger.addAppender(logEvents);
    }

    @AfterEach
    void detachLogAppender() {
        serviceLogger.detachAppender(logEvents);
        logEvents.stop();
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
        assertTrue(logEvents.list.stream().anyMatch(event -> event.getLevel() == Level.INFO
                && event.getFormattedMessage().contains("Аренда 1 создана")));
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
        assertTrue(logEvents.list.stream().anyMatch(event -> event.getLevel() == Level.WARN
                && event.getFormattedMessage().contains("уже арендован")));
    }

    @Test
    void returnsRentedBicycle() {
        Customer customer = new Customer(1, "Анна");
        Bicycle bicycle = new Bicycle(10, "Городской", BicycleStatus.AVAILABLE);
        service.rent(customer, bicycle);

        service.returnBicycle(bicycle);

        assertEquals(BicycleStatus.AVAILABLE, bicycle.getStatus());
        assertFalse(repository.hasActiveRental(10));
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

    @Test
    void doesNotChangeBicycleStatusWhenRentalCannotBeSaved() {
        RentalService failingService = new RentalService(new FailingRentalRepository());
        Customer customer = new Customer(1, "Анна");
        Bicycle bicycle = new Bicycle(12, "Шоссейный", BicycleStatus.AVAILABLE);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> failingService.rent(customer, bicycle));

        assertTrue(exception.getMessage().contains("Хранилище недоступно"));
        assertEquals(BicycleStatus.AVAILABLE, bicycle.getStatus());
        assertTrue(logEvents.list.stream().anyMatch(event -> event.getLevel() == Level.ERROR
                && event.getFormattedMessage().contains("не удалось сохранить аренду")
                && event.getThrowableProxy() != null
                && event.getThrowableProxy().getMessage().equals(exception.getMessage())));
    }

    /** Хранилище, которое имитирует сбой при сохранении аренды. */
    private static final class FailingRentalRepository extends InMemoryRentalRepository {
        @Override
        public void save(Rental rental) {
            throw new IllegalStateException("Хранилище недоступно");
        }
    }
}
