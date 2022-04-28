package fr.lataverne.votereward.utils.commands;

import org.jetbrains.annotations.NotNull;

public abstract class DynamicCommand extends CompositeCommand {

    protected DynamicCommand(@NotNull CompositeCommand parent, String label, String... aliases) {
        super(parent, label, aliases);
    }

    public final void setPermission() {
        this.inheritPermission();
    }

    protected abstract boolean isDynamicCommand(String label);
}
