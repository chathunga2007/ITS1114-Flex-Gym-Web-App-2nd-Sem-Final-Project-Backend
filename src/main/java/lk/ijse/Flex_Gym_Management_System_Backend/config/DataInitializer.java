package lk.ijse.Flex_Gym_Management_System_Backend.config;

import lk.ijse.Flex_Gym_Management_System_Backend.entity.*;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Package;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.*;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PackageRepository packageRepository;
    private final LockerRepository lockerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Checking and upgrading database schema and seeding records...");
        upgradeOrderTableColumns();
        seedUsersAndMembers();
        seedCategoriesAndProducts();
        seedPackages();
        seedLockers();
        log.info("Database initialization check complete!");
    }

    private void upgradeOrderTableColumns() {
        try {
            log.info("Checking and modifying orders table columns for real-world order lifecycle...");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN order_status VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN payment_status VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN payment_method VARCHAR(50)");
            log.info("orders table column types upgraded successfully to VARCHAR(50).");
        } catch (Exception e) {
            log.warn("Could not alter table orders columns (they may already be VARCHAR or table does not exist yet): {}", e.getMessage());
        }
    }

    private void seedUsersAndMembers() {
        if (!userRepository.existsByEmail("admin@flexgym.com")) {
            User admin = new User();
            admin.setEmail("admin@flexgym.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setUserRole(UserRole.ROLE_ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);
            log.info("Default Admin account seeded: admin@flexgym.com / Admin@123");
        }

        if (!userRepository.existsByEmail("receptionist@flexgym.com")) {
            User recep = new User();
            recep.setEmail("receptionist@flexgym.com");
            recep.setPassword(passwordEncoder.encode("Recep@123"));
            recep.setUserRole(UserRole.ROLE_RECEPTIONIST);
            recep.setStatus(UserStatus.ACTIVE);
            recep.setCreatedAt(LocalDateTime.now());
            userRepository.save(recep);
            log.info("Default Receptionist account seeded: receptionist@flexgym.com / Recep@123");
        }

        if (!userRepository.existsByEmail("trainer@flexgym.com")) {
            User trainer = new User();
            trainer.setEmail("trainer@flexgym.com");
            trainer.setPassword(passwordEncoder.encode("Trainer@123"));
            trainer.setUserRole(UserRole.ROLE_TRAINER);
            trainer.setStatus(UserStatus.ACTIVE);
            trainer.setCreatedAt(LocalDateTime.now());
            userRepository.save(trainer);
            log.info("Default Trainer account seeded: trainer@flexgym.com / Trainer@123");
        }

        if (!userRepository.existsByEmail("member@flexgym.com")) {
            User memberUser = new User();
            memberUser.setEmail("member@flexgym.com");
            memberUser.setPassword(passwordEncoder.encode("Member@123"));
            memberUser.setUserRole(UserRole.ROLE_MEMBER);
            memberUser.setStatus(UserStatus.ACTIVE);
            memberUser.setCreatedAt(LocalDateTime.now());
            User savedUser = userRepository.save(memberUser);

            Member member = new Member();
            member.setMemberFullName("Kasun Perera");
            member.setMemberPhoneNumber("+94 77 123 4567");
            member.setAge("26");
            member.setGender("Male");
            member.setHeightCm(new BigDecimal("178"));
            member.setWeightKg(new BigDecimal("74.5"));
            member.setMemberStatus(MemberStatus.ACTIVE);
            member.setUser(savedUser);
            memberRepository.save(member);
            log.info("Default Member account seeded: member@flexgym.com / Member@123 (Kasun Perera)");
        }
    }

    private void seedCategoriesAndProducts() {
        if (categoryRepository.count() == 0) {
            Category supplements = new Category(null, "Supplements", "High quality workout supplements & vitamins", CategoryStatus.ACTIVE);
            Category gear = new Category(null, "Fitness Gear", "Gym straps, belts, wraps, and lifting essentials", CategoryStatus.ACTIVE);
            Category apparel = new Category(null, "Gym Apparel", "Performance gym wear, stringers, and track pants", CategoryStatus.ACTIVE);
            Category accessories = new Category(null, "Accessories", "Shakers, gym bottles, and training accessories", CategoryStatus.ACTIVE);

            categoryRepository.saveAll(Arrays.asList(supplements, gear, apparel, accessories));
            log.info("Seeded 4 default product categories.");

            Product p1 = new Product(
                    null,
                    "HydroPure Isolate Whey (2kg)",
                    "Ultra-pure hydrolyzed whey protein with 27g protein per scoop for rapid muscle recovery.",
                    new BigDecimal("18500.00"),
                    35,
                    "https://images.unsplash.com/photo-1579722821273-0f6c7d44362f?auto=format&fit=crop&w=600&q=80",
                    ProductStatus.ACTIVE,
                    supplements
            );

            Product p2 = new Product(
                    null,
                    "Pro Heavy-Duty Lifting Straps & Wraps",
                    "Industrial-grade cotton and silicone padded straps for heavy deadlifts and pulls.",
                    new BigDecimal("3200.00"),
                    50,
                    "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?auto=format&fit=crop&w=600&q=80",
                    ProductStatus.ACTIVE,
                    gear
            );

            Product p3 = new Product(
                    null,
                    "Micronized Creatine Monohydrate (500g)",
                    "100% pure micronized creatine for maximum strength, power, and cellular hydration.",
                    new BigDecimal("8900.00"),
                    40,
                    "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?auto=format&fit=crop&w=600&q=80",
                    ProductStatus.ACTIVE,
                    supplements
            );

            Product p4 = new Product(
                    null,
                    "Flex Gym Athletic Performance Hoodie",
                    "Breathable lightweight moisture-wicking fleece hoodie built for cold gym sessions.",
                    new BigDecimal("4800.00"),
                    25,
                    "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?auto=format&fit=crop&w=600&q=80",
                    ProductStatus.ACTIVE,
                    apparel
            );

            Product p5 = new Product(
                    null,
                    "Flex Stainless Steel Shaker Bottle (750ml)",
                    "Vacuum-insulated, double-wall stainless steel shaker that keeps drinks ice cold for 24h.",
                    new BigDecimal("2600.00"),
                    60,
                    "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=600&q=80",
                    ProductStatus.ACTIVE,
                    accessories
            );

            productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));
            log.info("Seeded 5 initial official Flex Gym products.");
        }
    }

    private void seedPackages() {
        if (packageRepository.count() == 0) {
            Package pkg1 = new Package(
                    null,
                    "Basic Fitness Pass",
                    "Full gym floor access, cardio equipment & locker room access during regular hours.",
                    new BigDecimal("5000.00"),
                    1,
                    PackageStatus.ACTIVE
            );

            Package pkg2 = new Package(
                    null,
                    "Silver 3-Month Transformation",
                    "Quarterly full access, 2 free personal training intro sessions, and locker usage.",
                    new BigDecimal("13500.00"),
                    3,
                    PackageStatus.ACTIVE
            );

            Package pkg3 = new Package(
                    null,
                    "Gold 6-Month Elite Pass",
                    "Bi-annual complete gym and wellness pass with customized trainer workout splits.",
                    new BigDecimal("24000.00"),
                    6,
                    PackageStatus.ACTIVE
            );

            Package pkg4 = new Package(
                    null,
                    "Platinum VIP Annual Membership",
                    "365-day unrestricted 24/7 access, VIP personal locker, free sauna, and 15% store discount.",
                    new BigDecimal("42000.00"),
                    12,
                    PackageStatus.ACTIVE
            );

            packageRepository.saveAll(Arrays.asList(pkg1, pkg2, pkg3, pkg4));
            log.info("Seeded 4 gym membership packages.");
        }
    }

    private void seedLockers() {
        if (lockerRepository.count() == 0) {
            for (int i = 1; i <= 12; i++) {
                String num = String.format("L-%02d", i);
                Locker locker = new Locker();
                locker.setLockerNumber(num);
                locker.setIsOccupied(false);
                locker.setStatus(LockerStatus.AVAILABLE);
                lockerRepository.save(locker);
            }
            log.info("Seeded 12 gym lockers (L-01 to L-12).");
        }
    }
}