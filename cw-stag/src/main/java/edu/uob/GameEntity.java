package edu.uob;

public abstract class GameEntity
{
    private String name;
    private String description;

    public GameEntity(String name, String description)
    {
        this.name = name.toLowerCase();// I want everything to lowercase
        this.description = description.toLowerCase();
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }
}
