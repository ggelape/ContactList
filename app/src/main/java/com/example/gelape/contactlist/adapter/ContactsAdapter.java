package com.example.gelape.contactlist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gelape.contactlist.R;
import com.example.gelape.contactlist.model.ContactResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends ArrayAdapter<ContactResponse>
{
    @BindView(R.id.contactName)
    TextView contactName;
    @BindView(R.id.contactPicture)
    CircleImageView contactPicture;
    ArrayList<ContactResponse> contacts;
    private int rowLayout;
    private Context context;

    public ContactsAdapter(Context context, int rowLayout, ArrayList<ContactResponse> contacts)
    {
        super(context,rowLayout,contacts);
        this.contacts = contacts;
        this.rowLayout = rowLayout;
        this.context = context;
    }
    @Override
    public int getCount()
    {
        return super.getCount();
    }

    @Override
    public long getItemId(int position)
    {
        return super.getItemId(position);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent)
    {
        View v;
        if (view == null)
        {
            v = LayoutInflater.from(context).inflate(R.layout.contact_cell, parent, false);
        }
        else
        {
            v = view;
        }
        ButterKnife.bind(this, v);

        Picasso.with(context).cancelRequest(contactPicture);
        Picasso.with(context).load(contacts.get(position).getPhoto()).into(contactPicture);
        contactName.setText(contacts.get(position).getName());
        return v;
    }

}
