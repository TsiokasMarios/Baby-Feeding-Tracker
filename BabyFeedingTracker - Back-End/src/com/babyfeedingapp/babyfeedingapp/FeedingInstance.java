package com.babyfeedingapp.babyfeedingapp;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Objects;


public class FeedingInstance {
    private int id;
    private double amountMl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    
    //Default constructor
    public FeedingInstance() {
    	
    }
    
    //Constructor used for creating new instances and returning them
    public FeedingInstance(int id,double amountMl, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.amountMl = amountMl;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmountMl() {
        return amountMl;
    }

    public void setAmountMl(double amountMl) {
        this.amountMl = amountMl;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "FeedingInstance{" +
                "id=" + id +
                ", amountMl=" + amountMl +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedingInstance that = (FeedingInstance) o;
        return id == that.id && Double.compare(that.amountMl, amountMl) == 0 && startTime.equals(that.startTime) && endTime.equals(that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amountMl, startTime, endTime);
    }

}