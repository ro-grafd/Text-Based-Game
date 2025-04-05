package edu.uob;

// Super Parent class in which all others are children of this guy
// But not player, I did it because Player is the only Entity which would be dependent on the user
// So better to keep it as separate class
public abstract class GameEntity
{
    private final String name;
    private final String description;

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
