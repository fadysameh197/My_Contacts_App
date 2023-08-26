package com.fady.mycontactsapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.fady.mycontactsapp.adapter.ContactsAdapter;
import com.fady.mycontactsapp.db.ContactsDatabase;
import com.fady.mycontactsapp.db.entity.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    final private ArrayList<Contact> contactArrayList = new ArrayList<>();
    ImageView img_no_contacts;
    // Variables
    private ContactsAdapter contactsAdapter;
    private ContactsDatabase contactsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img_no_contacts = findViewById(R.id.img_no_contacts);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Favorite Contacts");


        // RecyclerVIew
        RecyclerView recyclerView = findViewById(R.id.recycler_view_contacts);
        //Database
        contactsDatabase = Room.databaseBuilder(getApplicationContext(), ContactsDatabase.class, "contactDB").allowMainThreadQueries().build();
        // Contacts List
        displayAllContactsInBackground();
        contactsAdapter = new ContactsAdapter(this, contactArrayList, MainActivity.this);
        if (contactArrayList.size() == 0) {
            img_no_contacts.setImageResource(R.drawable.no_contacts);
        } else {
            img_no_contacts.setImageResource(0);
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactsAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> addAndEditContacts(false, null, -1));
    }

    private void displayAllContactsInBackground() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //background work
            contactArrayList.addAll(contactsDatabase.getContactDao().getContacts());
            //after background work finish
            handler.post(() -> {

                contactsAdapter.notifyDataSetChanged();
                if (contactArrayList.size() == 0) {
                    img_no_contacts.setImageResource(R.drawable.no_contacts);
                } else {
                    img_no_contacts.setImageResource(0);
                }
            });
        });

    }

    public void addAndEditContacts(final boolean isUpdated, final Contact contact, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.layout_add_contact, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(view);


        TextView contactTitle = view.findViewById(R.id.new_contact_title);
        final EditText newContact = view.findViewById(R.id.name);
        final EditText contactEmail = view.findViewById(R.id.email);

        contactTitle.setText(!isUpdated ? "Add New Contact" : "Edit Contact");


        if (isUpdated && contact != null) {
            newContact.setText(contact.getName());
            contactEmail.setText(contact.getEmail());
        }

        alertDialogBuilder.setCancelable(false).setPositiveButton(isUpdated ? "Update" : "Save", (dialogInterface, i) -> {

        }).setNegativeButton("Delete", (dialogInterface, i) -> {
            if (isUpdated) {
                DeleteContact(contact, position);
            } else {
                dialogInterface.cancel();
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(newContact.getText().toString())) {
                Toast.makeText(MainActivity.this, "Please Enter a Name", Toast.LENGTH_SHORT).show();

                return;
            } else {
                alertDialog.dismiss();
            }

            if (isUpdated && contact != null) {
                UpdateContact(newContact.getText().toString(), contactEmail.getText().toString(), position);

            } else {
                CreateContact(newContact.getText().toString(), contactEmail.getText().toString());

            }

        });
        img_no_contacts.setImageResource(0);
    }

    private void DeleteContact(Contact contact, int position) {

        contactArrayList.remove(position);
        contactsDatabase.getContactDao().deleteContact(contact);

        contactsAdapter.notifyDataSetChanged();
        if (contactArrayList.size() == 0) {
            img_no_contacts.setImageResource(R.drawable.no_contacts);
        } else {
            img_no_contacts.setImageResource(0);
        }

    }


    private void UpdateContact(String name, String email, int position) {
        Contact contact = contactArrayList.get(position);

        contact.setName(name);
        contact.setEmail(email);

        contactsDatabase.getContactDao().updateContact(contact);

        contactArrayList.set(position, contact);
        contactsAdapter.notifyDataSetChanged();


    }


    private void CreateContact(String name, String email) {

        long id = contactsDatabase.getContactDao().addContact(new Contact(name, email, 0));
        Contact contact = contactsDatabase.getContactDao().getContact(id);

        if (contact != null) {
            contactArrayList.add(0, contact);
            contactsAdapter.notifyDataSetChanged();
        }

    }


    // Menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}