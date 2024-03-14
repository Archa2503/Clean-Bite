package com.example.cleanbite;

public class HealthDetails {
    private String name;
    private String email;
    private String dob;
    private String age;
    private String phone;

    public HealthDetails() {
        // Default constructor required for Firestore
    }

    public HealthDetails(String name, String email, String dob, String age, String phone) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.age = age;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
