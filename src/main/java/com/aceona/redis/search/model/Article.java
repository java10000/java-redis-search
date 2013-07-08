package com.aceona.redis.search.model;

import java.io.Serializable;

public class Article implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -5340000029325663567L;

    private int id;
    
    private String title;
    
    private String type;
    
    private String score;
    
    private String userName;
    
    private String email;
    

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getScore()
    {
        return score;
    }

    public void setScore(String score)
    {
        this.score = score;
    }

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
