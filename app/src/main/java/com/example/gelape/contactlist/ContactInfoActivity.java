package com.example.gelape.contactlist;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gelape.contactlist.database.DbController;
import com.example.gelape.contactlist.model.ContactResponse;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactInfoActivity extends AppCompatActivity
{
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.contactMail)
    TextView contactMail;
    @BindView(R.id.contactBirth)
    TextView contactBirth;
    @BindView(R.id.contactBio)
    TextView contactBio;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.contactImage)
    ImageView contactImage;
    EditText bornEdt;
    Calendar bornBox;
    Bundle bundle;
    DbController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        controller = new DbController(getApplicationContext());
        controller.open();

        bundle = getIntent().getExtras();
        updateContactInfo(bundle.getInt("contactId"));

    }

    public void updateContactInfo(int id)
    {
        ContactResponse contactsNew = controller.fetchSpecificContact(id);
        collapsingToolbarLayout.setTitle(contactsNew.getName());
        contactMail.setText(contactsNew.getEmail());
        contactBirth.setText(contactsNew.getBorn());
        contactBio.setText(contactsNew.getBio());
        Picasso.with(this).cancelRequest(contactImage);
        Picasso.with(this).load(contactsNew.getPhoto()).into(contactImage);
    }

    @OnClick(R.id.fab)
    public void onClick(View view)
    {
        createDialogUpdate(view);
    }

    private void updateLabelFrom()
    {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        bornEdt.setText(sdf.format(bornBox.getTime()));

        bornEdt.setInputType(InputType.TYPE_NULL);
    }

    public void createDialogUpdate(View view)
    {
        bundle = getIntent().getExtras();
        LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameBox = new EditText(view.getContext());
        nameBox.setHint("Nome");
        layout.addView(nameBox);

        final EditText emailBox = new EditText(view.getContext());
        emailBox.setHint("Email");
        layout.addView(emailBox);

        bornEdt = new EditText(view.getContext());
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
        bornEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ContactInfoActivity.this, date, bornBox
                        .get(Calendar.YEAR), bornBox.get(Calendar.MONTH),
                        bornBox.get(Calendar.DAY_OF_MONTH)).show();
                bornEdt.setInputType(InputType.TYPE_NULL);
            }
        });
        bornEdt.setInputType(InputType.TYPE_NULL);
        layout.addView(bornEdt);

        final EditText bioBox = new EditText(view.getContext());
        bioBox.setHint("Bio");
        layout.addView(bioBox);

        final EditText photoURL = new EditText(view.getContext());
        photoURL.setHint("URL da Foto");
        layout.addView(photoURL);

        TextView title = new TextView(this);
        // You Can Customise your Title here
        title.setText("Editar Contato");
        title.setBackgroundColor(Color.parseColor("#64b5f6"));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(22);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCustomTitle(title)
                .setView(layout).setPositiveButton("Salvar", null)
                .setNegativeButton("Cancelar", null);
        //alert.show();

        final AlertDialog alertDialog = alert.create();

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
                            ContactResponse contact = new ContactResponse(bundle.getInt("contactId"),
                                    nameBox.getText().toString(),
                                    bioBox.getText().toString(),
                                    bornEdt.getText().toString(),
                                    emailBox.getText().toString(),
                                    photoURL.getText().toString());

                            controller.updateRow(bundle.getInt("contactId"), contact);
                            updateContactInfo(bundle.getInt("contactId"));
                            dialogInterface.dismiss();
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






}
