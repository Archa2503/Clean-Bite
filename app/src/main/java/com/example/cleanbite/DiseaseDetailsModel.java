package com.example.cleanbite;

public class DiseaseDetailsModel {

    private String diseaseName;
    private String duration;
    private String sideEffects;
    private String userId; // New field to store the UID

    // Empty constructor needed for Firestore
    public DiseaseDetailsModel() {}

    public DiseaseDetailsModel(String diseaseName, String duration, String sideEffects, String userId) {
        this.diseaseName = diseaseName;
        this.duration = duration;
        this.sideEffects = sideEffects;
        this.userId = userId; // Initialize userId
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
