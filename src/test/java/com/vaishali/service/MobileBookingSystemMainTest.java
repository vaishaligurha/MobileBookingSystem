package com.vaishali.service;

import com.vaishali.pojo.Mobile;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class MobileBookingSystemMainTest {
    private static MobileBookingSystemService bookingSystem;
    CountDownLatch latch = new CountDownLatch(1);

    @BeforeAll
    static void setUp() {
        bookingSystem = MobileBookingSystemService.getInstance();
    }

    @AfterAll
    static void tearDown() {
        bookingSystem.shutdown();
    }

    @Test
    void bookMobile() throws InterruptedException, ExecutionException {
        Mobile mobile = bookingSystem.getMobile("Samsung Galaxy S9");
        if(mobile.isAvailable()) {
            bookingSystem.bookMobile("Samsung Galaxy S9", "John");
            assertNotNull(mobile);
            assertFalse(mobile.isAvailable());
            assertEquals("John", mobile.getBookedBy());
            assertNotNull(mobile.getBookingDate());
            assertThrows(IllegalStateException.class,
                    () -> bookingSystem.bookMobile("Samsung Galaxy S9", "Alice"));
        }else {
            assertThrows(IllegalStateException.class,
                    () -> bookingSystem.bookMobile("Samsung Galaxy S9", "Alice"));
        }
        }

    @Test
    void returnPhone() throws InterruptedException, ExecutionException {
        Mobile mobile = bookingSystem.getMobile("Samsung Galaxy S9");
        if(!mobile.isAvailable()){
        bookingSystem.returnMobile("Samsung Galaxy S9");
        assertNotNull(mobile);
        assertTrue(mobile.isAvailable());
        assertNull(mobile.getBookedBy());
        assertNull(mobile.getBookingDate());
        assertThrows(IllegalStateException.class,
                    () -> bookingSystem.returnMobile("Samsung Galaxy S9"));
        } else {
            assertThrows(IllegalStateException.class,
                    () -> bookingSystem.returnMobile("Samsung Galaxy S9"));
        }
    }

    @Test
    void bookPhone_NonExistentPhone() {
        assertThrows(IllegalArgumentException.class,
                () -> bookingSystem.bookMobile("Non-existent Model", "John"));
    }

    @Test
    void returnPhone_NonExistentPhone() {
        assertThrows(IllegalArgumentException.class,
                () -> bookingSystem.returnMobile("Non-existent Model"));
    }
}