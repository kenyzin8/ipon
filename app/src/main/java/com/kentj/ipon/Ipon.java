package com.kentj.ipon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Ipon {

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public double getGoal_amount() {
        return goal_amount;
    }

    public void setGoal_amount(double goal_amount) {
        this.goal_amount = goal_amount;
    }

    public String getDeadline() {

        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    private int id;
    private String purpose;
    private double goal_amount;
    private String deadline;
    private String date_added;
    private double balance;
}
