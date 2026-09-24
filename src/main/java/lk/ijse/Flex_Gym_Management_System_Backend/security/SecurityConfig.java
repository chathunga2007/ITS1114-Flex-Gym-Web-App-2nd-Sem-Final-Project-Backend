package lk.ijse.Flex_Gym_Management_System_Backend.security;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"status\":401,\"body\":null,\"message\":\"Unauthorized: Please sign in with valid credentials.\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"status\":403,\"body\":null,\"message\":\"Access Denied: You do not have permission to access this resource.\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. CORS Pre-flight Options
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Public Auth & Onboarding endpoints
                        .requestMatchers(HttpMethod.POST,
                                "/api/users/login",
                                "/api/users/forgot-password",
                                "/api/users/verify-otp",
                                "/api/users/reset-password",
                                "/api/users/saveUser").permitAll()

                        // 3. Public AI Assistant / Chatbot
                        .requestMatchers("/api/chatbot/**").permitAll()

                        // 4. Low stock alerts (restricted before public GETs)
                        .requestMatchers(HttpMethod.GET, "/api/products/getLowStockAlerts").hasAnyRole("ADMIN", "RECEPTIONIST")

                        // 5. Public catalog & informational GET endpoints
                        .requestMatchers(HttpMethod.GET,
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/packages/**",
                                "/api/trainers/**",
                                "/api/workout-plans/**",
                                "/api/orders/track/**").permitAll()

                        // 6. Admin-Only Critical Management & Deletions
                        .requestMatchers(
                                "/api/equipments/**",
                                "/api/users/getAllUsers",
                                "/api/users/deleteUser/**",
                                "/api/members/deleteMember/**",
                                "/api/memberships/deleteMembership/**",
                                "/api/payments/deletePayment/**",
                                "/api/lockers/deleteLocker/**",
                                "/api/member-workout-plans/deletePlan/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/api/packages/**",
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/trainers/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/packages/**",
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/trainers/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/packages/**",
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/trainers/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/packages/**",
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/trainers/**",
                                "/api/workout-plans/**"
                        ).hasRole("ADMIN")

                        // 7. Receptionist & Admin Operations (Front-Desk & Desk Facility Setup)
                        .requestMatchers(
                                "/api/members/saveMember",
                                "/api/memberships/saveMembership",
                                "/api/memberships/approveMembership/**",
                                "/api/memberships/rejectMembership/**",
                                "/api/memberships/getAllPendingMemberships",
                                "/api/memberships/updateMembership/**",
                                "/api/memberships/getAllMemberships",
                                "/api/memberships/getMembership/**",
                                "/api/memberships/run-expiry-check",
                                "/api/attendance/scan",
                                "/api/lockers/saveLocker",
                                "/api/payments/savePayment",
                                "/api/payments/updatePaymentStatus/**",
                                "/api/payments/getAllPayments",
                                "/api/payments/getPayment/**",
                                "/api/orders/getAllOrders",
                                "/api/orders/updateOrderStatus/**",
                                "/api/bookings/all"
                        ).hasAnyRole("ADMIN", "RECEPTIONIST")

                        // 8. Staff / Coaching Overview (Admin, Receptionist & Trainer)
                        .requestMatchers(
                                "/api/members/getAllMembers",
                                "/api/attendance/getAllLogs"
                        ).hasAnyRole("ADMIN", "RECEPTIONIST", "TRAINER")

                        // 9. Workout Design (Admin & Trainer)
                        .requestMatchers(
                                "/api/workout-plans/saveWorkoutPlan",
                                "/api/workout-plans/updateWorkoutPlan",
                                "/api/member-workout-plans/updatePlan",
                                "/api/bookings/trainer/**"
                        ).hasAnyRole("ADMIN", "TRAINER")

                        // 10. Authenticated Operations (Members, Trainers, Receptionists, Admins)
                        .requestMatchers(
                                "/api/users/getUser/**",
                                "/api/users/updateUser",
                                "/api/members/getMember/**",
                                "/api/members/updateMember/**",
                                "/api/memberships/requestMembership",
                                "/api/memberships/getMembershipsByMember/**",
                                "/api/memberships/getMemberMembership/**",
                                "/api/attendance/getMemberAttendance/**",
                                "/api/attendance/getMonthlySummary/**",
                                "/api/lockers/getAllLockers",
                                "/api/lockers/getLocker/**",
                                "/api/lockers/updateLocker",
                                "/api/member-workout-plans/getAllPlans",
                                "/api/member-workout-plans/getPlan/**",
                                "/api/member-workout-plans/assignPlan",
                                "/api/orders/placeOrder",
                                "/api/orders/getOrder/**",
                                "/api/orders/getMemberOrders/**",
                                "/api/bookings/create",
                                "/api/bookings/member/**",
                                "/api/bookings/update-status/**",
                                "/api/progress/**",
                                "/api/payments/getPaymentsByMember/**"
                        ).hasAnyRole("ADMIN", "RECEPTIONIST", "TRAINER", "MEMBER")

                        // 11. Fallback for all other endpoints
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}