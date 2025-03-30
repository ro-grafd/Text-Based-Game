package edu.uob;

import java.io.Serial;

public class GameException extends Exception {
    @Serial
    private static final long serialVersionUID = 42L;

    // Default constructor with a generic game error message
    public GameException() {
        super("An unexpected game error occurred");
    }

    // Constructor with custom error message
    public GameException(String message) {
        super(message);
    }

    // Nested exception classes with more descriptive and unique messages
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

    public static class ResourceLimitation extends GameException {
        @Serial
        private static final long serialVersionUID = 45L;

        public ResourceLimitation(String resource) {
            super("Insufficient or restricted access to " + resource);
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

        public InvalidName(String additionalDetails) {
            super("Invalid character name: " + additionalDetails);
        }
    }

    public static class InteractionError extends GameException {
        @Serial
        private static final long serialVersionUID = 47L;

        public InteractionError(String interaction) {
            super("Cannot complete " + interaction + " interaction");
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
            super("Inventory  failed");
        }
    }
}