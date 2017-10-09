package com.example.gelape.contactlist;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        collapsingToolbarLayout.setTitle(bundle.getString("contactName"));
        contactMail.setText(bundle.getString("contactEmail"));
        contactBirth.setText(bundle.getString("contactBirth"));
        contactBio.setText(bundle.getString("contactBio"));
        Picasso.with(this).cancelRequest(contactImage);
        Picasso.with(this).load(bundle.getString("contactPhoto")).into(contactImage);

    }

    @OnClick(R.id.fab)
    public void onClick(View view)
    {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
