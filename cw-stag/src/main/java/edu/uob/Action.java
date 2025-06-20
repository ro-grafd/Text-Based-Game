package edu.uob;

import java.util.HashSet;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// Any specific Action provided by the player to perform
// So any action what it will include, stuff that it is influencing
// so entities involved, entities used, entities made, and the trigger word set
// and yes actionStatement as in what would the action would do
public class Action
{
    private final HashSet<String> entitiesInvolved;
    private final HashSet<String> entitiesUsed;
    private final HashSet<String> entitiesMade;
    private final HashSet<String> triggers;
    private final String actionStatement;
    
    public Action(Element actionElement)
    {
        this.entitiesInvolved = new HashSet<>();
        this.entitiesUsed = new HashSet<>();
        this.entitiesMade = new HashSet<>();
        this.triggers = new HashSet<>();
        this.constructSet(triggers, "triggers", "keyphrase", actionElement);
        this.constructSet(entitiesInvolved, "subjects", "entity", actionElement);
        this.constructSet(entitiesUsed, "consumed", "entity", actionElement);
        this.constructSet(entitiesMade, "produced", "entity", actionElement);
        this.actionStatement = actionElement.getElementsByTagName("narration").item(0).getTextContent();
    }

    private void constructSet(HashSet<String>setToConstruct, String outerTag, String innerTag, Element actionElement)
    {
        Element outerTags = (Element)actionElement.getElementsByTagName(outerTag).item(0);
        NodeList innerTags = outerTags.getElementsByTagName(innerTag);
        for(int i = 0; i < innerTags.getLength(); i++)
        {
            setToConstruct.add(innerTags.item(i).getTextContent().toLowerCase());
        }
    }

    public HashSet<String> getEntitiesInvolved() {
        return entitiesInvolved;
    }

    public HashSet<String> getEntitiesUsed() {
        return entitiesUsed;
    }

    public HashSet<String> getEntitiesMade() {
        return entitiesMade;
    }

    public HashSet<String> getTriggers() {
        return triggers;
    }

    public String getActionStatement() {
        return actionStatement;
    }
}
