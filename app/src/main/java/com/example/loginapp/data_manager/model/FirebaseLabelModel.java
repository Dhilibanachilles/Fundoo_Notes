package com.example.loginapp.data_manager.model;

public class FirebaseLabelModel {
    private  String label;
    private  String labelId;

    public FirebaseLabelModel(String label,String labelId) {
        this.label = label;
        this.labelId = labelId;
    }

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}