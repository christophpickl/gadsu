* @appointment: close window on hit ESC key
* icon idee
    - zb: jede behandlung hat drei "kuebeln"
    - rechts gibts eine sidebar mit drei kasterln wo "steinchen" drinnen sind,
    - zb steinchen inhalt (technik): Le, Bl, ... jedes kann man mit text versehen. oder auch steinchen bein/arm/...?
    - zb steinchen zustand: kopfweh, schlaf, ...
    - idea: wenn man auf steinchen doppelklickt, welches schon im kuebel ist, dann geht popup auf, zb um spezifische akupunkturpunkte auszuwaehlen => das verlinken mit akupunkturpunkt
* @auto updater: if on windows, suggest to download EXE not JAR (check for mac as well)
* windows improvements:
    - dont use JFrame but JWindow instead (jframe leads to ugly taskbar icon)
    - use installer so one can pin the app on the taskbar
* @appointments: als tooltip when hovered, dann die notiz anzeigen (falls vorhanden)
* minor BUG: wenn "inaktivieren von klienten", dann list rendering funkt nicht ganz
* LUXURY: wenn treatment goal setzen, dann kein neustart erforderlich (gleich rendern)
* @protocol: wenn kein username in preferences eingestellt, dann warning ausgeben (evtl gleich mit moeglichkeit den namen zu setzen direkt!)
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
* testdata creator
  - 100 clients
  - random 0-30 treatments
  - random 0-4 appointments
  - 70% hat bilder
  - 50% male/female
  - birthday sodass alter 20-60 jahre
* use LocalDateTime, LocalTime
* use DateRange
* fuer "when", "in", ... eigene extension methods machen mit synonym um backticks zu umgehen

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
- user profile pic croppen wenn nicht gleiche seitenverhaeltnisse dialog (daweil nur automatisch wo was wegschneiden)
* GADSU client ranking
	- smart way of auto-order clients
	- top clients on top (most points)
		* TC (0.2pt) - time created: count days diff to today; the more the better
		* CT (5.0pt) - count treatments: pro behandlung
		* TLT (1.0pt) - time last treatment: je aktueller, desto mehr punkte; 30 - 0 pkt
	- SAMPLES (today = 1.6.2010)
		A: "recht aktueller client"
			created = vor 2 monaten: 1.4.2010 ... 60 TC * 0.2pt => 12PT
			treatments = 4 CT * 5pt => 20PT
			last treat = gestern: 30.5.2010 ... 30 - 1 TLT = 29 * 1.0pt => 29PT
			===> 12 + 20 + 29 = 61PT
