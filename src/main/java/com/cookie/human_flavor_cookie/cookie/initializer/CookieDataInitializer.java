package com.cookie.human_flavor_cookie.cookie.initializer;

import com.cookie.human_flavor_cookie.cookie.entity.Cookie;
import com.cookie.human_flavor_cookie.cookie.repository.CookieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CookieDataInitializer implements CommandLineRunner {

    private final CookieRepository cookieRepository;

    public CookieDataInitializer(CookieRepository cookieRepository) {
        this.cookieRepository = cookieRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (cookieRepository.count() == 0) { // 중복 데이터 방지
            cookieRepository.save(new Cookie("Brave", 5000));
            cookieRepository.save(new Cookie("Zombie", 10000));
            cookieRepository.save(new Cookie("Happy", 5000));
            cookieRepository.save(new Cookie("Angel", 15000));
            cookieRepository.save(new Cookie("ButterCream", 15000));

            System.out.println("Initial cookie data added to the database.");
        } else {
            System.out.println("Cookie data already exists in the database.");
        }
    }
}

