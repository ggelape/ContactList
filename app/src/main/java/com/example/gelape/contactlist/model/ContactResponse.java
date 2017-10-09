
package com.example.gelape.contactlist.model;


public class ContactResponse
{
    private int id;
    private String name;
    private String email;
    private String born;
    private String bio;
    private String photo;

    public ContactResponse(int id, String name, String bio, String born, String email, String photo)
    {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.born = born;
        this.email = email;
        this.photo = photo;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getBorn()
    {
        return born;
    }

    public void setBorn(String born)
    {
        this.born = born;
    }

    public String getBio()
    {
        return bio;
    }

    public void setBio(String bio)
    {
        this.bio = bio;
    }

    public String getPhoto()
    {
        return photo;
    }

    public void setPhoto(String photo)
    {
        this.photo = photo;
    }

}
