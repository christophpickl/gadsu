
! bug: print report speichern geht nicht (drucken aber schon)
- geburtsdatum eingebbar (durchklicken is ur laestig)
- notizfeld fuer TCM
- nickname (zwischen first- und lastname)
- breitere textfelder: mail, strasse
- wenn in textareas, mit tab in felder springen!
- in textareas wrap only at whitespace
- sternzeichen kalkulator :)
- geburtstags reminder!!!
@client:
  - neues feld familie: geschwister, eltern verheiratet, bezug zu geschwister/eltern
!!! textfelder mehr als 1024 zeichen! vor allem notiz braucht mehr!!!
- BUG: wenn neue behandlung anlegen, speichern -> dann muss navigationsbutton updated werden => write test first!

* ad report:
 - extra lib fuer pdf handling: mehrere zusammenmergen
 - binary/clientPic in jasper rein
 - dynamische texthoehe fuer jasper
 - schon vorhandenes deckblatt programmatisch was drauf schreiben
 - seitenzahl muesste auch komplett neu vergeben werden, ToC haendisch??
 
 @ list hoehe: die haben ja defaultmaessig immer 8 rows visible :> runterstellen, dann sollte das layout auch weiter runter resizable gehn

T-O-D-O
============================================================

High
------------------------------------------------------------
* @UI: bug in TCM lists (not preselected)
* changes detection for treatment
* @UI: automatically scroll to far left in textfields (on update)
* BUG: wenn aenderung verwerfen, treatment list ist leer!
* BUG: treatment list selection onNew
* git tagging in release.sh does not work!
* automate the release more
  * download link on github main page to current version
  * nice would be to create a release in github, containing list of issues, and attached release files, etc.

Med
------------------------------------------------------------
* datum text eingebbar, zb geburtsdatum ist umstaendlich jahr nach hinten zu scrollen (ueberhaupt die ganze komponenten copy'n'pasten, anstatt noch mehr reflection)
* [2] check for database lock file
* [1] add system property GADSU_DIR which changes the default ~/.gadsu/ setting
* [1] UI: logo for windows icon
* ad backup: store backups in greater interval (weekly, last 10, ... lets see how big such a file can get) => @TEST create load test infra!
* [1] add DB constraint that firstName+lastName combination is unique!
* AOP logging for all service + repository classes
* konkurrenz analyse
  * http://www.clinicsense.com/tour/
  * http://www.bodyworkbuddy.com/
* calc star sign (west and east) based on birthday
* client master list soll nicht horizontal scrollable sein => "Mail: max@mu ..." mit dots hinten dran abschneiden wenn zu lang
   - auch so beim namen?

Low
------------------------------------------------------------
* alle textfields/areas immer TRIM (???)
* mehr aktionen ueber menu bar: Klient / Bild Aendern, ...
* switch to HSQLDB's new UUID type for ID columns
* @TECH: use custom AOP annotation to ensure transaction safety
* [2] REFACT: split ClientViewController for master and detail
* check there are no snapshots during release.sh
* @UI: https://tips4java.wordpress.com/2010/11/28/combo-box-popup/
* @Languages check default system property for language (luxury: configurable via preferences)
* @UI: JDatePicker seems to support JodaTime (see their website)
* read jasper: http://www.tutorialspoint.com/jasper_reports/jasper_report_sections.htm
* enhance window descriptor:
  * make reusable for others
  * remember by dispaly
* when tracing in SqlJdbcX, then avoid linefeeds in log output

Test
------------------------------------------------------------
* use powermock (as much is final in kotlin)
* @ClientServiceImplIntegrationTest use guice support for integration tests instead
* investigate: http://jetbrains.github.io/spek/
* good UI test code sample: https://github.com/UISpec4J/UISpec4J/blob/master/uispec4j/src/test/java/org/uispec4j/PanelTest.java
* code cov schauen ob man bereiche excluden kann
* @ClientSpringJdbcRepositoryTest hamcrest-bean would be nice ;) assertThat(actualSaved, theSameAs(newClient.withId(generatedId)).excludePath("Client.Created"))
* investigate: custom hamcrest matcher for kotlin

Luxury Ideas
------------------------------------------------------------
* doodle integration (a la steffi)
* [8] UI: mit doppelklick auf eine JTextArea oeffnet sich ein modaler dialog, wo nur ein fettes textarea drinnen ist, das man speichern/dismissen kann (mehr platz zum schreiben)
* WYSIWYG text editor
