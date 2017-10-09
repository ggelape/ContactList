package com.example.gelape.contactlist;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gelape.contactlist.adapter.ContactsAdapter;
import com.example.gelape.contactlist.database.DbController;
import com.example.gelape.contactlist.model.ContactResponse;
import com.example.gelape.contactlist.rest.ApiClient;
import com.example.gelape.contactlist.rest.ApiInterface;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
    List<ContactResponse> contactsResponse;
    ArrayList<ContactResponse> contactsResponseFinal = new ArrayList<>();
    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
    @BindView(R.id.contactList)
    ListView contactList;
    ContactsAdapter adapter;
    DbController controller;
    EditText bornEdt;
    Calendar bornBox;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        controller = new DbController(getApplicationContext());
        controller.open();
        checkFirstAppRun();
    }


    @Override
    public void onStart()
    {
        super.onStart();
        contactsResponseFinal = controller.fetchAllContacts();
        adapter = new ContactsAdapter(getApplicationContext(), R.layout.contact_cell, contactsResponseFinal);
        contactList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        createDialogAdd();
        return super.onOptionsItemSelected(item);
    }

    @OnItemClick(R.id.contactList)
    public void onItemClick(int position)
    {
        Intent intent = new Intent(getApplicationContext(), ContactInfoActivity.class);
        intent.putExtra("contactId", contactsResponseFinal.get(position).getId());
        getApplicationContext().startActivity(intent);
    }

    @OnItemLongClick(R.id.contactList)
    public boolean onItemLongClick(final int position)
    {
        new AlertDialog.Builder(MainActivity.this)
            .setTitle("Deletar")
            .setMessage("Voce deseja deletar o contato " + contactsResponseFinal.get(position).getName() + " ?")
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
            {
                @Override public void onClick(DialogInterface dialog, int which)
                {
                    controller.deleteRow(contactsResponseFinal.get(position).getId());
                    contactsResponseFinal.remove(position);
                    adapter.notifyDataSetChanged();
                }
            })
            .create()
            .show();
        return true;
    }

    public void checkFirstAppRun()
    {
        //if its the first time you run the app it gets the information from webservice
        SharedPreferences preferences = getSharedPreferences("MyPrefrence", MODE_PRIVATE);
        if (!preferences.getBoolean("isFirstTime", false))
        {
            Call<List<ContactResponse>> call = apiService.getContacts();
            call.enqueue(new Callback<List<ContactResponse>>()
            {
                @Override
                public void onResponse(Call<List<ContactResponse>>call, Response<List<ContactResponse>> response)
                {
                    contactsResponse = response.body();
                    for (int i = 0; i < contactsResponse.size(); i++)
                    {
                        DbController crud = new DbController(getBaseContext());
                        crud.insertData(i ,contactsResponse.get(i).getName() ,contactsResponse.get(i).getBorn() ,
                                contactsResponse.get(i).getBio() , contactsResponse.get(i).getEmail(), contactsResponse.get(i).getPhoto());

                    }
                    contactsResponseFinal = controller.fetchAllContacts();
                    adapter = new ContactsAdapter(getApplicationContext(), R.layout.contact_cell, contactsResponseFinal);
                    contactList.setAdapter(adapter);

                    final SharedPreferences pref = getSharedPreferences("MyPrefrence", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isFirstTime", true);
                    editor.apply();
                }
                @Override
                public void onFailure(Call<List<ContactResponse>>call, Throwable t)
                {
                    Toast.makeText(getApplicationContext(), "Failed to get Contacts " + t.toString(), Toast.LENGTH_SHORT).show();
                    final SharedPreferences pref = getSharedPreferences("MyPrefrence", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isFirstTime", false);
                    editor.apply();
                }
            });
        }
        //otherwise it gets from the database
        else
        {
            contactsResponseFinal = controller.fetchAllContacts();
            adapter = new ContactsAdapter(getApplicationContext(), R.layout.contact_cell, contactsResponseFinal);
            contactList.setAdapter(adapter);
        }
    }

    //create the dialog to add a new contact
    public void createDialogAdd()
    {
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameBox = new EditText(getApplicationContext());
        nameBox.setHint("Nome");
        layout.addView(nameBox);

        final EditText emailBox = new EditText(getApplicationContext());
        emailBox.setHint("Email");
        layout.addView(emailBox);

        bornEdt = new EditText(getApplicationContext());
        bornBox = Calendar.getInstance();
        Date currentLocalTime = bornBox.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        bornEdt.setText(dateFormat.format(currentLocalTime));
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                bornBox.set(Calendar.YEAR, year);
                bornBox.set(Calendar.MONTH, monthOfYear);
                bornBox.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelFrom();
            }
        };
        bornEdt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, date, bornBox
                        .get(Calendar.YEAR), bornBox.get(Calendar.MONTH),
                        bornBox.get(Calendar.DAY_OF_MONTH)).show();
                bornEdt.setInputType(InputType.TYPE_NULL);
            }
        });
        bornEdt.setInputType(InputType.TYPE_NULL);
        layout.addView(bornEdt);

        final EditText bioBox = new EditText(getApplicationContext());
        bioBox.setHint("Bio");
        layout.addView(bioBox);

        final EditText photoURL = new EditText(getApplicationContext());
        photoURL.setHint("URL da Foto");
        layout.addView(photoURL);

        TextView title = new TextView(this);
        // You Can Customise your Title here
        title.setText("Adicionar Contato");
        title.setBackgroundColor(Color.parseColor("#64b5f6"));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(22);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCustomTitle(title)
                .setView(layout).setPositiveButton("Salvar", null)
                .setNegativeButton("Cancelar", null);

        final AlertDialog alertDialog = alert.create();
        //check if the user left no fields blank
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(final DialogInterface dialogInterface)
            {
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        if(nameBox.getText().toString().isEmpty() ||
                                emailBox.getText().toString().isEmpty() ||
                                bioBox.getText().toString().isEmpty() ||
                                photoURL.getText().toString().isEmpty())
                        {
                            Toast.makeText(getApplicationContext(),
                                    "Nenhum dos campos pode estar em branco, favor preencher!",
                                    Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            int idProximo = contactsResponseFinal.get(contactsResponseFinal.size() - 1).getId() + 1;
                            ContactResponse contact = new ContactResponse(idProximo,
                                    nameBox.getText().toString(),
                                    bioBox.getText().toString(),
                                    bornEdt.getText().toString(),
                                    emailBox.getText().toString(),
                                    photoURL.getText().toString());

                            controller.createRow(contact);
                            dialogInterface.dismiss();
                            contactsResponseFinal = controller.fetchAllContacts();
                            adapter = new ContactsAdapter(getApplicationContext(), R.layout.contact_cell, contactsResponseFinal);
                            contactList.setAdapter(adapter);
                        }
                    }
                });

                Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialogInterface.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }

    //format the date
    private void updateLabelFrom()
    {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        bornEdt.setText(sdf.format(bornBox.getTime()));

        bornEdt.setInputType(InputType.TYPE_NULL);
    }
}
