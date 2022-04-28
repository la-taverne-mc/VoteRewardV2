package fr.lataverne.votereward.utils.commands;

class Parameter {

    private final boolean isOptional;

    private final String label;

    Parameter(String label, boolean isOptional) {
        this.label = label;
        this.isOptional = isOptional;
    }

    public String getLabel() {
        return this.label;
    }

    public boolean isOptional() {
        return this.isOptional;
    }
}
