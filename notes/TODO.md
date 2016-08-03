* BUG: wenn klient deaktiverieren, respect view option (show/hide inactives) and hide client
- niemals JFrame verwenden (fuer non-mainframes) sondern immer nur window&co
- zungendiagnose und puls sind keine TCM stammdaten => sondern als dynamic treatment abbilden
- breitere textfelder: mail, strasse
* @ list hoehe: die haben ja defaultmaessig immer 8 rows visible :> runterstellen, dann sollte das layout auch weiter runter resizable gehn
- warum ist im database.log ganz viel disconnects?! logged der tatsaechlich jeden app startup mit? kann man das sauberer/besser machen?!

T-O-D-O
============================================================

High
------------------------------------------------------------



Med
------------------------------------------------------------
* @UI: tcm note (and others) use split view (with grab handle to resize) instead!
* [1] add system property GADSU_DIR which changes the default ~/.gadsu/ setting
* [1] add DB constraint that firstName+lastName combination is unique!
* AOP logging for all service + repository classes
* client master list soll nicht horizontal scrollable sein => "Mail: max@mu ..." mit dots hinten dran abschneiden wenn zu lang
   - auch so beim namen?
* ad backup: store backups in greater interval (weekly, last 10, ... lets see how big such a file can get) => @TEST create load test infra!

Low
------------------------------------------------------------
* [2] check for database lock file
* konkurrenz analyse
  * http://www.clinicsense.com/tour/
  * http://www.bodyworkbuddy.com/
* alle textfields/areas immer TRIM (???)
* backup zielordner auswaehlen (sollte nicht dort liegen, wo ~/.gadsu liegt!
* switch to HSQLDB's new UUID type for ID columns
* @TECH: use custom AOP annotation to ensure transaction safety
* [2] REFACT: split ClientViewController for master and detail
* check there are no snapshots during release.sh
* @UI: https://tips4java.wordpress.com/2010/11/28/combo-box-popup/
* @Languages check default system property for language (luxury: configurable via preferences)
* @UI: JDatePicker seems to support JodaTime (see their website) 
* read jasper: http://www.tutorialspoint.com/jasper_reports/jasper_report_sections.htm
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
* WYSIWYG text editor
- user profile pic croppen wenn nicht gleiche seitenverhaeltnisse dialog
