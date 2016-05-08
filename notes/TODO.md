
  
@rebecca
* notiz feld labeln
* behandlungslist labeln
* alle textfields/areas immer TRIM
* mehr aktionen ueber menu bar: Klient / Bild Aendern, ... 
* BUG: treatment list selection onNew
* datum text eingebbar, zb geburtsdatum ist umstaendlich jahr nach hinten zu scrollen (ueberhaupt die ganze komponenten copy'n'pasten, anstatt noch mehr reflection)

T-O-D-O
============================================================

High
------------------------------------------------------------
* @UI: bug in TCM lists (not preselected)
* changes detection for treatment
* @UI: automatically scroll to far left in textfields (on update)
* define different DB location during development (dont cross with PROD data when working on gadsu!!!)
* bug, wenn aenderung verwerfen, treatment list ist leer!

Med
------------------------------------------------------------
* [2] check for database lock file
* [1] UI: logo for windows icon
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

* google CALENDER integration
  * erst spaeter, wenns termine gibt...
  * das evtl schon als plugin zur verfuegung stellen (??)
  * https://developers.google.com/google-apps/calendar/quickstart/java
* [8] UI: mit doppelklick auf eine JTextArea oeffnet sich ein modaler dialog, wo nur ein fettes textarea drinnen ist, das man speichern/dismissen kann (mehr platz zum schreiben)
