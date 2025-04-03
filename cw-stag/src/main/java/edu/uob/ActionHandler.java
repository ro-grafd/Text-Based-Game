package edu.uob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ActionHandler {
    private String triggerWord;
    private HashSet<Action> canDoActions;
    private HashSet<Action> doActions;
    private List<String> tokens;
    private HashSet<Action> feasibleActions;

    public ActionHandler(HashSet<Action> canDoActions,List<String> tokens,String triggerWord) {
        this.canDoActions = canDoActions;
        this.tokens = tokens;
        this.triggerWord = triggerWord;
        this.doActions = new HashSet<>();
        this.feasibleActions = new HashSet<>();
    }

    public void findFeasibleActions() throws GameException.ContextualConstraint {
        for(Action action: canDoActions) {
            for(String token : tokens) {
                if(action.getEntitiesInvolved().contains(token)) {
                    this.feasibleActions.add(action);
                }
            }
        }
        if(this.feasibleActions.isEmpty()) {
            throw new GameException.ContextualConstraint();
        }
    }

    public Action findToDoAction(Location location, HashMap<String, Artefact> personalInventory) throws GameException.ContextualConstraint {
        this.findFeasibleActions();
        for(Action action: feasibleActions) {
            HashSet<String> subjects = action.getEntitiesInvolved();
            boolean allSubjectsAvailable = true;

            for(String subject : subjects) {
                boolean subjectAvailable = location.getAvailableEntities().contains(subject) ||
                        personalInventory.containsKey(subject);

                if(!subjectAvailable) {
                    allSubjectsAvailable = false;
                    break;
                }
            }

            if(allSubjectsAvailable) {
                this.doActions.add(action);
            }
        }
        if(this.doActions.isEmpty()) {
            throw new GameException.ContextualConstraint();
        }
        if(this.doActions.size() > 1) {
            throw new GameException.ContextualConstraint();
        }
        return this.doActions.iterator().next();
    }
}

