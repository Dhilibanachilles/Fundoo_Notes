package com.example.loginapp.data_manager.model;

public class FirebaseNoteModel {

    private String title;
    private String description;
    private String id;
    private long creationTime;
    private boolean isArchived;
    private boolean isDeleted;

    public boolean getArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FirebaseNoteModel(String title, String description, String id,
                             boolean isArchived, boolean isDeleted, long creationTime) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.isArchived = isArchived;
        this.isDeleted = isDeleted;
        this.creationTime = creationTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FirebaseNoteModel() {

    }
}