package com.example.cmp354_covidtracer;

public class Users {
    private String name;
    private String emailId;
    private boolean isCovidPositive;

    public Users() {
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
