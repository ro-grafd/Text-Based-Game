package edu.uob;

import java.io.Serial;

// Custom exception handlers extending the Exception class to get better error message
public class GameException extends Exception {
    @Serial
    private static final long serialVersionUID = 42L;
    public GameException() {
        super("An unexpected game error occurred");
    }

    public GameException(String message) {
        super(message);
    }


    public static class CommandComplexity extends GameException {
        @Serial
        private static final long serialVersionUID = 43L;
        public CommandComplexity() {
            super("The command is too intricate to process");
        }
    }

    public static class ContextualConstraint extends GameException {
        @Serial
        private static final long serialVersionUID = 44L;
        public ContextualConstraint() {
            super("Game state violation ");
        }
    }

    public static class EntitiesResourceLimitation extends GameException {
        @Serial
        private static final long serialVersionUID = 45L;
        public EntitiesResourceLimitation() {
            super("Insufficient or (invalid) restricted access ");
        }
    }

    public static class TriggerException extends GameException {
        @Serial
        private static final long serialVersionUID = 50L;
        public TriggerException() {
            super("Trigger word is either nothing or more than one");
        }
    }

    public static class InvalidName extends GameException {
        @Serial
        private static final long serialVersionUID = 46L;
        public InvalidName() {
            super("Character name violates naming protocol");
        }

    }

    // This sneaky one is not getting used as we are not having the option to interact with other players
    // Agh, imagine the game would be like if were allowed to harm/heal other player ????
    // But quite difficult init
    public static class InteractionError extends GameException {
        @Serial
        private static final long serialVersionUID = 47L;
        public InteractionError() {
            super("Cannot complete interaction");
        }
    }

    public static class NavigationError extends GameException {
        @Serial
        private static final long serialVersionUID = 48L;
        public NavigationError() {
            super("Navigation is not possible");
        }
    }

    public static class InventoryManagementError extends GameException {
        @Serial
        private static final long serialVersionUID = 49L;
        public InventoryManagementError() {
            super("Inventory get command failed");
        }
    }
}