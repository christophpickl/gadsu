!!! wenn in textareas, mit tab in felder springen!
!!! notizfeld fuer TCM

!!! textfelder mehr als 1024 zeichen! vor allem notiz braucht mehr!!!
!! vorherige/next treatment buttons nebeneinander

!! BUG vorherige button soll aktiv sein, wenn neue behandlung anlegen
! @date component: geburtsdatum eingebbar (durchklicken is ur laestig)
- nickname (zwischen first- und lastname)
- breitere textfelder: mail, strasse
- in textareas wrap only at whitespace
-- geburtstags reminder
- @client neues feld familie: geschwister, eltern verheiratet, bezug zu geschwister/eltern

* ad report:
 - extra lib fuer pdf handling: mehrere zusammenmergen
 - binary/clientPic in jasper rein
 - dynamische texthoehe fuer jasper
 - schon vorhandenes deckblatt programmatisch was drauf schreiben
 - seitenzahl muesste auch komplett neu vergeben werden, ToC haendisch??
 
 @ list hoehe: die haben ja defaultmaessig immer 8 rows visible :> runterstellen, dann sollte das layout auch weiter runter resizable gehn

------------------------------------------------------------

@GADSU RENE

! BUG: wenn klient TCM note veraendern, dann save, dann wird er nicht disabled!

- initiale fenstergroesse groesser
	* checken wie gross das display ist, damit nicht ueber maximal
- das "erstellt am" feld dezenter machen
	* er hat probiert reinzuklicken, weils so im "flow der eingabe" im weg steht
- LUXURY: bild croppen wenn nicht gleiche seitenverhaeltnisse dialog
- client list andere sachen reinrendern als die mailadresse
	* zb anzahl der behandlungen
	* datum der naechst anstehenden behandlung (anhand der termine!)
	* geburtstags icon: wenn geburtstag bevorsteht
	* 
- BUG: termin dialog oeffnen, dann gleich "neue behandlung" klicken => termin ist nicht gespeichert
- mehr auswahlmoeglichkeiten fuer pulsdiagnose
- klienten sortierreihenfolge auswaehlbar
	* vorname
	* meisten behandlung
	* most recent behandlung
	* most recent updated (store last update field)
- in/aktive flag fuern client
- MINOR: @check modifications: wenn klient bearbeiten, dann anderen klient gehen, dann dialog "speichern" klick, dann sollte der andere klient ausgewaehlt sein, nicht der alte (sonst ist ein zweiter unnoetiger klick notwendig)
! datumskomponente ueberarbeiten
	=> forken!
	- selber text einggebbar!
	- BUG: wenn datepicker UI offen ist, dann zu TCM wechseln (oder anderen klient), bleibt popup offen; so machen wie wenn zu treatment wechseln
	- nicht grau sondern weiss hinterlegen
- Client.insert prototype die herkunftsland defaultmaessig auf Oesterreich
	* eigentlich ist eh nur wichtig wo man aufgewachsen ist (zusaetzliches feld?!)
! wenn dateien speichern (generell!) immer einen default namen vorschlagen
	! plus file type indicator auf PDF
	- plus fenster title auf was gscheites setzen
	- wenn sammelprotokoll drucken: soll automatisch .pdf anfuegen
* @protokoll: die TCM properties nicht generisch den text erzeugen, sondern pro feld ein text-template hinterlegen. (oder gar plump als auflistung ausgeben in einem freitextfeld)
- @TCM property list: das verhalten soll defaultmaessig so sein, als wuerde man die ganze zeit ctrl gedrueck halten
	- evtl echt schon mit rendering hints arbeiten fuer checkbox renderer
- wenn exception geworfen, dann im error dialog nicht nur URL anklickbar zu github issue, sondern auch log file direkt aufmachen (zb unter macos "open xxx"

!! anzahl an behandlungen als "ziel" einggebar; drunter erscheint dann ein balken als progressbar (brauch ja 20, das soll aber konfigurierbar sein)

- warum ist im database.log ganz viel disconnects?! logged der tatsaechlich jeden app startup mit? kann man das sauberer/besser machen?!
! die EXE version ist im windows nirgends registriert
	* man kanns nicht mal pinnen an die taskbar!
	=> windows installer anbieten!
* die versionsnummer aus Gadsu-1.2.3.exe rausnehmen => nur "Gadsu.exe"
* backup zielordner auswaehlen (sollte nicht dort liegen, wo ~/.gadsu liegt!
- dass tab content transparent ist, ist unter win schirch (wird weiss!)

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
* @UI: tcm note (and others) use split view (with grab handle to resize) instead!
* datum text eingebbar, zb geburtsdatum ist umstaendlich jahr nach hinten zu scrollen (ueberhaupt die ganze komponenten copy'n'pasten, anstatt noch mehr reflection)
* [2] check for database lock file
* [1] add system property GADSU_DIR which changes the default ~/.gadsu/ setting
* ad backup: store backups in greater interval (weekly, last 10, ... lets see how big such a file can get) => @TEST create load test infra!
* [1] add DB constraint that firstName+lastName combination is unique!
* AOP logging for all service + repository classes
* konkurrenz analyse
  * http://www.clinicsense.com/tour/
  * http://www.bodyworkbuddy.com/
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
* @UI: JDatePicker seems to support JodaTime (see their website) => 
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
