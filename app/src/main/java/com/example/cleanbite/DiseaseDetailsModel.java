package com.example.cleanbite;

public class DiseaseDetailsModel {

    private String diseaseName;
    private String duration;
    private String sideEffects;

    // Empty constructor needed for Firestore
    public DiseaseDetailsModel() {}

    public DiseaseDetailsModel(String diseaseName, String duration, String sideEffects) {
        this.diseaseName = diseaseName;
        this.duration = duration;
        this.sideEffects = sideEffects;
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
}
