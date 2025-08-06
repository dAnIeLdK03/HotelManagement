package com.hotelsystemmanegment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HotelSystemManegmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelSystemManegmentApplication.class, args);
    }

}
