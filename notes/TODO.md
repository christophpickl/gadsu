* BUG: internet connection check at startup false negatives!
* sort client list (auto-sort by some defined criteria; or sort manually by drag'n'drop)
* bigger client pic in main tab
* @appointment: close window on hit ESC key
* icon idee
    - zb: jede behandlung hat drei "kuebeln"
    - rechts gibts eine sidebar mit drei kasterln wo "steinchen" drinnen sind,
    - zb steinchen inhalt (technik): Le, Bl, ... jedes kann man mit text versehen. oder auch steinchen bein/arm/...?
    - zb steinchen zustand: kopfweh, schlaf, ...
    - idea: wenn man auf steinchen doppelklickt, welches schon im kuebel ist, dann geht popup auf, zb um spezifische akupunkturpunkte auszuwaehlen => das verlinken mit akupunkturpunkt
* @auto updater: if on windows, suggest to download EXE not JAR (check for mac as well)
* @appointments: als tooltip when hovered, dann die notiz anzeigen (falls vorhanden)
* minor BUG: wenn "inaktivieren von klienten", dann list rendering funkt nicht ganz
* LUXURY: wenn treatment goal setzen, dann kein neustart erforderlich (gleich rendern)
* @protocol: wenn kein username in preferences eingestellt, dann warning ausgeben (evtl gleich mit moeglichkeit den namen zu setzen direkt!)
* BUG: wenn klient deaktiverieren, respect view option (show/hide inactives) and hide client
- zungendiagnose und puls sind keine TCM stammdaten => sondern als dynamic treatment abbilden
- breitere textfelder: mail, strasse
* @ list hoehe: die haben ja defaultmaessig immer 8 rows visible :> runterstellen, dann sollte das layout auch weiter runter resizable gehn
- warum ist im database.log ganz viel disconnects?! logged der tatsaechlich jeden app startup mit? kann man das sauberer/besser machen?!
* @backup: in preferences (anklickbaren a la reveal folder) pfad zum backup ordner + "backup now" button


T-O-D-O
============================================================

High
------------------------------------------------------------
* BUG: wenn client bearbeiten (unsaved changes), dann bild hinzufuegen/aendern => gehn die changes verloren!
* improved GCal sync view: bigger window, proper col width; render client pic; von-bis schoenerer text (selten ueberd nacht gehen der termin); recurring flag
* neue anamnese formular ausarbeiten
* BUG: wenn mit CMD+rauf/runter klient wechseln waehrend unsaved changes sind => dialog kommt immer wieder trotz "Abbrechen" klicken

Med
---
* master client cell list: sort based on displayed label (preferred name) instead of regular name // add sorting+filtering/searching feature anyway
* on mouse over fuer hara diagnose fields
* klient deaktivieren auch per rechte mausklick (nicht nur menu)
* @RELEASE: copy artifacts from release_build to local wu path (if existing)
* LOG should append in rolling files (currently one big, exploding file with 10MB after several months only)
* add tabs to preferences window (general/google)
* ChangesChecker it would be nicer to continue after saving, but this is somehow complicated because of the EventBus which works asynchronously
* wahlfrei in treatments navigieren, nicht nur links/rechts, sondern auch zb durch nummerneingabe/dropdown direkt hin
* dyn treatment: rueckendiagnose, yu punkte, bo punkte
* upgrade to gradle 3
* maybe when splitting up into gradle modules, things will speed up?!
* when client changes detected, user confirms, then select the clicked one to save one click
* automatically scroll to far left in textfields (on update)
* mehr aktionen ueber menu bar: Klient / Bild Aendern, ...
* enhance window descriptor: make reusable for others; remember by display
* verletzungs medizinisches maxerl (an welchen stellen besonders aufpassen wegen verletzung grafik)
* @UI: tcm note (and others) use split view (with grab handle to resize) instead!
* [1] add system property GADSU_DIR which changes the default ~/.gadsu/ setting
* [1] add DB constraint that firstName+lastName combination is unique!
* AOP logging for all service + repository classes
* client master list soll nicht horizontal scrollable sein => "Mail: max@mu ..." mit dots hinten dran abschneiden wenn zu lang
   - auch so beim namen?
* ad backup: store backups in greater interval (weekly, last 10, ... lets see how big such a file can get) => @TEST create load test infra!
* sorting based on displayed value (not first/last name, as displayed name sometimes include nickname) => sorted by not displayed value!
* when sammelprotokoll print => busy indicator/progress dialog
* das datumsfeld fuer den datepicker (zb treatment view) bissi breiter machen; der inhalt steht haarscharf an
* treatment 30min statt 15min schritte
* wenn exception geworfen, dann im error dialog nicht nur URL anklickbar zu github issue, sondern auch log file direkt aufmachen (zb unter macos "open xxx")
* BUG: wenn aenderung verwerfen, treatment list ist leer!
* BUG: treatment list selection onNew
* BUG: in TCM lists (not preselected)
* das "erstellt am" feld dezenter machen; er hat probiert reinzuklicken, weils so im "flow der eingabe" im weg steht
* in appointment window, mit ESC dialog close
* cancel button for async dialog
* WebLatestVersionFetcher return withTimeout(CONNECTION_TIMEOUT) {
* TEST with guice enabled tests: build testinfra/guice.kt

Low
---
* fulltext search
* wenn TCM props CTRL+A zweimal, dann ist speichern button nicht enabled BUG!
* zungen/puls props mit kuerzel versehen, dieses in UI zusaetzlich rendern, auf schummelzettel drucken, auf protokoll nur kuerzel vermerken
* nice mac DMG window (background image)
* search for clients
* use some nice drag'n'drop feature
* ad appointment:
    * view size distribution of appointment vs treatment
    * cleanup window form: start end time in separate row; check if end > start; bigger
    * center window on show
    * be able to save at very first show
* split the app into gradle modules
* to scale images use scalrb
* [2] check for database lock file
* konkurrenz analyse
  * http://www.clinicsense.com/tour/
  * http://www.bodyworkbuddy.com/
* alle textfields/areas immer TRIM (???)
* backup zielordner auswaehlen (sollte nicht dort liegen, wo ~/.gadsu liegt!
* switch to HSQLDB's new UUID type for ID columns
* @TECH: use custom AOP annotation to ensure transaction safety
* [2] REFACT: split ClientViewController for master and detail
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
* @check modifications: wenn klient bearbeiten, dann anderen klient gehen, dann dialog "speichern" klick, dann sollte der andere klient ausgewaehlt sein, nicht der alte (sonst ist ein zweiter unnoetiger klick notwendig)

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
* WYSIWYG text editor!
* doodle integration (a la steffi)
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
* use the yet existing 5E view; select element to highlight focus to work on
* ad windows UI: dass tab content transparent ist, ist unter win schirch (wird weiss!); dont use JFrame but JWindow instead (jframe leads to ugly taskbar icon)
* ad windows install: installs the exe, registers in OS; it should be possible to pin it on the taskbar!!!; create desktop shortcut
* xml export (base64 encode image; dont need to think about import from XML, or do we?)
* encrypt DB with password

MULTI PROTOCOL
------------------------------------------------------------
* page number
! BUG: bold formatting gets lost after pdf page merging
