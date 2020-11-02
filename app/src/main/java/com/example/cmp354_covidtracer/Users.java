package com.example.cmp354_covidtracer;

import java.util.Objects;

public class Users {
    private String name;
    private String emailId;
    private boolean isCovidPositive;

    public Users() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return isCovidPositive == users.isCovidPositive &&
                Objects.equals(name, users.name) &&
                Objects.equals(emailId, users.emailId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, emailId, isCovidPositive);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Users(String name, String emailId, boolean isCovidPositive) {
        this.name = name;
        this.emailId = emailId;
        this.isCovidPositive = isCovidPositive;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public boolean isCovidPositive() {
        return isCovidPositive;
    }

    public void setCovidPositive(boolean covidPositive) {
        isCovidPositive = covidPositive;
    }
}
