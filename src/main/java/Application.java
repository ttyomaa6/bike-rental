import model.Bicycle;
import model.BicycleStatus;
import model.Customer;
import model.Rental;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.InMemoryRentalRepository;
import repository.RentalRepository;
import service.RentalService;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        RentalRepository repository = new InMemoryRentalRepository();
        RentalService service = new RentalService(repository);

        Customer anna = new Customer(1, "Анна");
        Customer ivan = new Customer(2, "Иван");
        Bicycle cityBike = new Bicycle(10, "Городской", BicycleStatus.AVAILABLE);
        Bicycle mountainBike = new Bicycle(11, "Горный", BicycleStatus.MAINTENANCE);

        LOGGER.info("Сценарий 1: аренда свободного велосипеда");
        Rental rental = service.rent(anna, cityBike);
        LOGGER.info("Результат: велосипед {} выдан клиенту {}", rental.getBicycleId(), anna.getName());

        LOGGER.info("Сценарий 2: повторная аренда уже арендованного велосипеда");
        try {
            service.rent(ivan, cityBike);
        } catch (IllegalStateException exception) {
            LOGGER.info("Результат: {}", exception.getMessage());
        }

        LOGGER.info("Сценарий 3: аренда велосипеда на обслуживании");
        try {
            service.rent(anna, mountainBike);
        } catch (IllegalStateException exception) {
            LOGGER.info("Результат: {}", exception.getMessage());
        }
    }
}
