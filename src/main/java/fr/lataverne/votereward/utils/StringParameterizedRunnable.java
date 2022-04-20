package fr.lataverne.votereward.utils;

@FunctionalInterface
public interface StringParameterizedRunnable extends Runnable {

    @Override
    public default void run() {
        this.run("");
    }

    public void run(String param);
}
