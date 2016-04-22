
# T-O-D-O

## High

* integrate report generation (save PDF, view, and print report)
* treatment CRUDs
* protocol generation UI wizard

## Med

* logo for windows icon
* enable auto formatting in intellij
* on press enter in input field: save
* mit doppelklick auf eine JTextArea oeffnet sich ein modaler dialog, wo nur ein fettes textarea drinnen ist, das man speichern/dismissen kann (mehr platz zum schreiben)
* in UI auf max length (durch DB limits) setzen (eigene textfeld/textarea komponente mit konstruktor)
* reset prefs feature
* add DB constraint that firstName+lastName combination is unique!
* JTextField().putClientProperty("JTextField.variant", "search")
        
## Low

* check there are no snapshots during release.sh
* create native EXE for windows
* tooltips
* mnemonic and shortcut for menubar
* export (wichtig), aber auch import (nicht sooo wichtig)
* release files online stellen
  * requirements:
    * direct link to file
    * ~100MB transfer, 500MB total is enough
    * programmatic upload (during release build)
  * https://www.box.com ?
* google kalender integration (erst spaeter, wenns termine gibt)
  * das evtl schon als plugin zur verfuegung stellen (??)
  * https://developers.google.com/google-apps/calendar/quickstart/java
* konkurrenz analyse
  * http://www.clinicsense.com/tour/
  * http://www.bodyworkbuddy.com/
* @ClientSpringJdbcRepositoryTest hamcrest-bean would be nice ;) assertThat(actualSaved, theSameAs(newClient.withId(generatedId)).excludePath("Client.Created"))
* haha, one can see the prefs window float down to that position ;)
* investigate: http://jetbrains.github.io/spek/
* good UI test code sample: https://github.com/UISpec4J/UISpec4J/blob/master/uispec4j/src/test/java/org/uispec4j/PanelTest.java
* instead of polluting the code with thousands of log statements for EventBus on-listener-methods, use a more centralized (AOP-like) approach


## Luxury

* automatic version check
  * during release, create some file with current version in it
  * at app startup, regularly check (GET http to github repo) remote file for version match
