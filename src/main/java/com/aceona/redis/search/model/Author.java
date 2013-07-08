package com.aceona.redis.search.model;

import java.io.Serializable;

public class Author implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = -5068453720190693122L;

    private String userName;
    
    private String email;

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
