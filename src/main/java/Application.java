import model.Bicycle;
import model.BicycleStatus;
import model.Customer;
import model.Rental;
import repository.InMemoryRentalRepository;
import repository.RentalRepository;
import service.RentalService;

public class Application {
    public static void main(String[] args) {
        RentalRepository repository = new InMemoryRentalRepository();
        RentalService service = new RentalService(repository);

        Customer anna = new Customer(1, "Анна");
        Customer ivan = new Customer(2, "Иван");
        Bicycle cityBike = new Bicycle(10, "Городской", BicycleStatus.AVAILABLE);
        Bicycle mountainBike = new Bicycle(11, "Горный", BicycleStatus.MAINTENANCE);

        Rental rental = service.rent(anna, cityBike);
        System.out.println("Выдан велосипед " + rental.getBicycleId()
                + " клиенту " + anna.getName());

        try {
            service.rent(ivan, cityBike);
        } catch (IllegalStateException exception) {
            System.out.println(exception.getMessage());
        }

        try {
            service.rent(anna, mountainBike);
        } catch (IllegalStateException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
