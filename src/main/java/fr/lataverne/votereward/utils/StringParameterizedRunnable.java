package fr.lataverne.votereward.utils;

public abstract class StringParameterizedRunnable implements Runnable {

    protected String parameter = "";

    public void setParameter(String param) {
        this.parameter = param;
    }
}
