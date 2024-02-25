package com.vaishali;

import com.vaishali.service.MobileBookingSystemService;

import java.util.concurrent.ExecutionException;

public class MobileBookingSystemMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MobileBookingSystemService bookingSystem =  MobileBookingSystemService.getInstance();

        // Test booking and returning
        bookingSystem.bookMobile("Samsung Galaxy S9", "John");
        bookingSystem.bookMobile("Samsung Galaxy S9", "Alice");
        bookingSystem.returnMobile("Samsung Galaxy S9");

        // Shutdown the booking system
        bookingSystem.shutdown();
    }
}