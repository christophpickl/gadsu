
# T-O-D-O

## High

* proper exception handling (needs some global concept)
* migrate DB version

## Med

* logo for windows icon
* enable auto formatting in intellij
* on press enter in input field: save
* mit doppelklick auf eine JTextArea oeffnet sich ein modaler dialog, wo nur ein fettes textarea drinnen ist, das man speichern/dismissen kann (mehr platz zum schreiben)

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