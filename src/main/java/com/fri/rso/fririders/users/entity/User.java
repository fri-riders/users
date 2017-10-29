package com.fri.rso.fririders.users.entity;

import java.util.Date;
import java.util.UUID;

public class User {

    private String uuid;

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    private boolean isActive;
    private boolean isConfirmed;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    public User(String email, String password) {
        this.uuid = UUID.randomUUID().toString();
        this.email = email;
        this.password = password;
        this.isActive = true;
        this.isConfirmed = true;
        this.createdAt = new Date();
    }

    public User(String email, String password, String firstName, String lastName) {
        this.uuid = UUID.randomUUID().toString();
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = true;
        this.isConfirmed = true;
        this.createdAt = new Date();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
}
