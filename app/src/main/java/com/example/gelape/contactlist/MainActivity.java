package com.example.gelape.contactlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gelape.contactlist.adapter.ContactsAdapter;
import com.example.gelape.contactlist.model.ContactResponse;
import com.example.gelape.contactlist.rest.ApiClient;
import com.example.gelape.contactlist.rest.ApiInterface;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
    List<ContactResponse> contactsResponse;
    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
    @BindView(R.id.contactList)
    ListView contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Call<List<ContactResponse>> call =  apiService.getContacts();
        call.enqueue(new Callback<List<ContactResponse>>()
        {
            @Override
            public void onResponse(Call<List<ContactResponse>>call, Response<List<ContactResponse>> response)
            {
                contactsResponse = response.body();
                ContactsAdapter adapter = new ContactsAdapter(getApplicationContext(), R.layout.contact_cell, contactsResponse);
                contactList.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<ContactResponse>>call, Throwable t)
            {
                Toast.makeText(getApplicationContext(), "Failed to get Contacts " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnItemClick(R.id.contactList)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent intent = new Intent(getApplicationContext(), ContactInfoActivity.class);
        intent.putExtra("contactName", contactsResponse.get(position).getName());
        intent.putExtra("contactBio", contactsResponse.get(position).getBio());
        intent.putExtra("contactBirth", contactsResponse.get(position).getBorn());
        intent.putExtra("contactEmail", contactsResponse.get(position).getEmail());
        intent.putExtra("contactPhoto", contactsResponse.get(position).getPhoto());
        getApplicationContext().startActivity(intent);
    }

}
