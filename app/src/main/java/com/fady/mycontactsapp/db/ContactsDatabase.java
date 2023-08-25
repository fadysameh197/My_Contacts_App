package com.fady.mycontactsapp.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.fady.mycontactsapp.db.entity.Contact;

@Database(entities = Contact.class, version = 1)
public abstract class ContactsDatabase extends RoomDatabase {
    public abstract ContactDAO getContactDao();
}
