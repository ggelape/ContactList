package com.example.gelape.contactlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;
import com.example.gelape.contactlist.adapter.ContactsAdapter;
import com.example.gelape.contactlist.database.DbController;
import com.example.gelape.contactlist.model.ContactResponse;
import com.example.gelape.contactlist.rest.ApiClient;
import com.example.gelape.contactlist.rest.ApiInterface;
import java.util.ArrayList;
import java.util.List;
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

    public void checkFirstAppRun()
    {
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
        else
        {
            contactsResponseFinal = controller.fetchAllContacts();
            adapter = new ContactsAdapter(getApplicationContext(), R.layout.contact_cell, contactsResponseFinal);
            contactList.setAdapter(adapter);
        }
    }

    @OnItemClick(R.id.contactList)
    public void onItemClick(int position)
    {
        Intent intent = new Intent(getApplicationContext(), ContactInfoActivity.class);
        intent.putExtra("contactName", contactsResponseFinal.get(position).getName());
        intent.putExtra("contactBio", contactsResponseFinal.get(position).getBio());
        intent.putExtra("contactBirth", contactsResponseFinal.get(position).getBorn());
        intent.putExtra("contactEmail", contactsResponseFinal.get(position).getEmail());
        intent.putExtra("contactPhoto", contactsResponseFinal.get(position).getPhoto());
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
}
