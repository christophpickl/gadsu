package at.cpickl.gadsu;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.UIManager;

import at.cpickl.gadsu.service.LogConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This name will also show up in the native mac app, so dont rename that class.
 */
public class Gadsu {

    private static final Logger LOG = LoggerFactory.getLogger(Gadsu.class);

    public static void main(String[] cliArgs) {
        Args args = ArgsKt.parseArgsOrHelp(cliArgs);
        if (args == null) {
            return;
        }

        if (GadsuSystemProperty.INSTANCE.getDisableLog().isEnabledOrFalse()) {
            System.out.println("Gadsu log configuration disabled. (most likely because tests come with own log config)");
        } else {
            new LogConfigurator(args.getDebug()).configureLog();
        }

        JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // UIManager.setLookAndFeel(new SubstanceRavenLookAndFeel());
        } catch (Exception e) {
            LOG.error("Could not set native look&feel!", e);
        }


        try {
            new GadsuStarter().start(args);
        } catch (ArgsActionException e) {
            LOG.error("Invalid CLI arguments! " + Arrays.toString(cliArgs), e);
            System.err.println("You entered an invalid CLI argument: '" + Arrays.toString(cliArgs) + "'! Exception message: " + e.getMessage());
        }
    }

}
