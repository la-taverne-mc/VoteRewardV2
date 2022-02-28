package fr.lataverne.votereward.commands;

@FunctionalInterface
public interface DynamicCommand {

    boolean isDynamicCommand(String label);
}
