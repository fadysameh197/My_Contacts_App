package com.fady.mycontactsapp.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class Contact {

    // 2- Variables
    @ColumnInfo(name = "contact_name")
    private String name;

    @ColumnInfo(name = "contact_email")
    private String email;

    @ColumnInfo(name = "contact_id")
    @PrimaryKey(autoGenerate = true)
    private int id;


    // 3- Constructors
    @Ignore
    public Contact() {

    }

    public Contact(String name, String email, int id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    // 4- Getters & Setters
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
