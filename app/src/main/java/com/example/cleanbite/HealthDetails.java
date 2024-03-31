package com.example.cleanbite;

public class HealthDetails {
    private String name;
    private String email;
    private String dob;
    private String age;
    private double height;
    private double weight;

    public HealthDetails() {
        // Default constructor required for Firestore
    }

    public HealthDetails(String name, String email, String dob, String age, double height, double weight) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    // Getters and setters for new attributes (height and weight)

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    // Other getters and setters for existing attributes

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
}