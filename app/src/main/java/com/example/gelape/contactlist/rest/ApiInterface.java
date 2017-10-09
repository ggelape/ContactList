package com.example.gelape.contactlist.rest;

import com.example.gelape.contactlist.model.ContactResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface
{
    @GET("rgasp-mobile-test/v1/content.json")
    Call<List<ContactResponse>> getContacts();
}
