package at.cpickl.gadsu;

import javax.swing.*;

import at.cpickl.gadsu.service.LogConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Not easy (impossible?) to define a static void main entry point in Kotlin, so do it in Java ;)
 */
public class GadsuApp {

    private static final Logger LOG = LoggerFactory.getLogger(GadsuApp.class);

    static {
        LogConfigurator.INSTANCE.configureLog();

        JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // UIManager.setLookAndFeel(new SubstanceRavenLookAndFeel());
        } catch (Exception e) {
            LOG.error("Could not set native look&feel!", e);
        }
    }

    public static void main(String[] args) {
        new GadsuStarter().start(args);
    }

}
