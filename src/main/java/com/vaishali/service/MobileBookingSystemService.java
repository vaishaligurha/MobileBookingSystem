package com.vaishali.service;

import com.vaishali.pojo.Mobile;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class MobileBookingSystemService {
    private static volatile MobileBookingSystemService instance;
    private final Map<String, Mobile> mobiles;
    private final ReadWriteLock lock;
    private final ExecutorService executorService;
    private static final Logger LOGGER = Logger.getLogger(MobileBookingSystemService.class.getName());
    private MobileBookingSystemService() {
        mobiles = new HashMap<>();
        lock = new ReentrantReadWriteLock();
        initializeMobilePhones();

        // Create a thread pool with a fixed number of threads
        int numThreads = Runtime.getRuntime().availableProcessors() * 2;
        executorService = Executors.newFixedThreadPool(numThreads);
    }

    public static MobileBookingSystemService getInstance() {
        if (instance == null) {
            synchronized (MobileBookingSystemService.class) {
                if (instance == null) {
                    instance = new MobileBookingSystemService();
                }
            }
        }
        return instance;
    }

    private void initializeMobilePhones() {
        //This can be read from properties file as well. Suitable if list of mobile can change
        mobiles.put("Samsung Galaxy S9", new Mobile("Samsung Galaxy S9"));
        mobiles.put("Samsung Galaxy S8", new Mobile("Samsung Galaxy S8"));
        mobiles.put("Motorola Nexus 6", new Mobile("Motorola Nexus 6"));
        mobiles.put("Oneplus 9", new Mobile("Oneplus 9"));
        mobiles.put("Apple iPhone 13", new Mobile("Apple iPhone 13"));
        mobiles.put("Apple iPhone 12", new Mobile("Apple iPhone 12"));
        mobiles.put("Apple iPhone 11", new Mobile("Apple iPhone 11"));
        mobiles.put("iPhone X", new Mobile("iPhone X"));
        mobiles.put("Nokia 3310", new Mobile("Nokia 3310"));
    }

    public void bookMobile(String modelName, String bookedBy) throws ExecutionException, InterruptedException {
        Future<?> future = executorService.submit(() -> {
            lock.writeLock().lock();
            try {
                Mobile mobile = mobiles.get(modelName);
                if (mobile != null) {
                    if (mobile.isAvailable()) {
                        mobile.setBookedBy(bookedBy);
                        mobile.setBooked(true);
                        mobile.setBookingDate(new Date());
                        LOGGER.info("Phone "+ modelName +" booked by " + bookedBy);
                    } else {
                        throw new IllegalStateException("Phone " + modelName + " is already booked.");
                    }
                } else {
                    throw new IllegalArgumentException("Phone " + modelName + " not found.");
                }
            } finally {
                lock.writeLock().unlock();
            }
        });
        try {
           future.get();
        }catch(ExecutionException e) {
            Throwable cause = e.getCause();
            if(cause instanceof IllegalStateException) {
                throw (IllegalStateException) cause;
            }else if(cause instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) cause;
            }else{
                throw e;
            }
        }
    }

    public void returnMobile(String modelName) throws ExecutionException, InterruptedException {
        Future<?> future =  executorService.submit(() -> {
            lock.writeLock().lock();
            try {
                Mobile mobile = mobiles.get(modelName);
                if (mobile != null) {
                    if (!mobile.isAvailable()) {
                        mobile.setBookedBy(null);
                        mobile.setBooked(false);
                        mobile.setBookingDate(null); // Reset booking date
                        LOGGER.info("Phone " + modelName + " is returned.");
                    } else {
                        throw new IllegalStateException("Phone " + modelName + " is already returned.");
                    }
                } else {
                    throw new IllegalArgumentException("Phone " + modelName + " not found.");
                }
            } finally {
                lock.writeLock().unlock();
            }
        });
        try {
            future.get();
        }catch(ExecutionException e) {
            Throwable cause = e.getCause();
            if(cause instanceof IllegalStateException) {
                throw (IllegalStateException) cause;
            }else if(cause instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) cause;
            }else{
                throw e;
            }
        }
    }

    public Mobile getMobile(String modelName) {
        lock.readLock().lock();
        try {
            return mobiles.get(modelName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
