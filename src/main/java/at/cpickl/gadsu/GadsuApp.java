package at.cpickl.gadsu;

import at.cpickl.gadsu.service.LogConfigurator;

/**
 * Not easy (impossible?) to define a static void main entry point in Kotlin, so do it in Java ;)
 */
public class GadsuApp {

    static {
        LogConfigurator.INSTANCE.configureLog();
    }

    public static void main(String[] args) {
        new GadsuStarter().start(args);
    }

}
