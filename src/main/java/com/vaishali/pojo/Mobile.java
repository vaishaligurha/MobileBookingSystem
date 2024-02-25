package com.vaishali.pojo;

import java.util.Date;

public class Mobile {
    private final String modelName;
    private boolean isAvailable;
    private String bookedBy;

    private Date bookingDate;

    public Mobile(String modelName) {
        this.modelName = modelName;
        this.isAvailable = true;
    }

    public String getModelName() {
        return modelName;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setBooked(boolean booked) {
        isAvailable = !booked;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}
