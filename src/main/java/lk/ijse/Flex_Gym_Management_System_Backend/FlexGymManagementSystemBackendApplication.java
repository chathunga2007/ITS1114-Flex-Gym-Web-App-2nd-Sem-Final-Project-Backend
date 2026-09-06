package lk.ijse.Flex_Gym_Management_System_Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlexGymManagementSystemBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(FlexGymManagementSystemBackendApplication.class, args);
	}
}