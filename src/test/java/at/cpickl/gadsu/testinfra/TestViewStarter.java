package at.cpickl.gadsu.testinfra;

import java.awt.Component;

import at.cpickl.gadsu.view.components.Framed;
import org.testng.Assert;

public class TestViewStarter {

    public static Component componentToShow;

    // invoked by ui tests
    public static void main(String[] args) {
        if (componentToShow == null) {
            Assert.fail("componentToShow must be set first!");
        }
        Framed.Companion.show(componentToShow, null);
    }

}
