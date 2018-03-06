
!!! do NOT change title when gcal sync
!!! search clients (name; +filter: no bhdlg, money only);
!!! sort clients (erstell datum, last treatment),
* AA klient kategorie einfuehren
* TCM selector doch (daweil) nur flat list
* fuer klienten automatisch 5E profil blatt generieren
* von zungen fotos uploadbar machen ticket!
    // - ad zungenbuecher: barbara kirschbaum gibts 2 gute buecher
* @gcal sync:
	- dont make it possible to send confirmation mail for PAST dates
	- dont change title on sync

GADSU GADSU GADSU GADSU 
	! birthday reminder popup
	- rechtsklick doesnt work!!! + mit backspace events loeschen
	- auf 30min steps stellen (bisherige runden einfach ;)
	- sort clients (bhdgl cnt.)
	- search names / filter clients
	- agenda: popup das die naechsten (+vorherigen paar) behandlungen anzeigt

* konstitution VS kondition
* syndrom(s) - depends on #115
! don't really delete clients, only mark the as deleted and persist forever (?)
* wenn client wechseln, dann reset views; v.a. TCM multiproperties nach oben scrollen
* search field mit tree auch fuer zunge/puls
* die mit gar keinen behandlungen besonders behandeln: bei sort IMMER ganz oben; eigene filter aktion (dann brauchts das 001 nimmer)
! TCM tab: unten notiz feld minimum 3 zeilen anzeigen!
! github issue schreiben: doodle integration (scan gcal, create doodle, invite clients... store everything in DB)
- ad sammelprotokoll: beschreibungsfeld weg, seitennummer hinzu (PDF post proccess), nach deckblatt eine leere seite rein
- akupunktur punkte eintragen
- zungen symptome angleichen an neuesten stand; 1:1 das wording uebernehmen

* Bei hover symptom, tooltip mit possible syndrom. 
* in richtext, wenn markierung (akupunkt oder bold), wenn enter druecken, dann format wegnehmen
* [10] recent mail liste fuehren (sowas wie templates); kann sie laden, speichern, loeschen (evtl benennen);
    - man sieht wen man was geschickt hat (achtung beim handeln von inzwischen deleted clients!)
* [10] sich umschauen nach einem alternative doodle anbieter mit gscheiter API
* (2) prefs von bulk mail nicht in prefs window, sondern als "..." button in "bulk mail window"
* (20) evtl nicht wirklich loeschen, sondern nur mark as deleted?! (oder zumindest in backup table schieben?!) 
* (2) @UI: client view die tabs alle mit CMD+number ansprechbar (implizit sobald tab hinzugefuegt wird, weil daweil is es haendisch deshalb funkts fuern assistent nicht!) 
* (3) treatment 30min statt 15min schritte (needs some (implicit?!) migration)
* (2) on confirmation mail: first check freemarker template and report proper error immediately
* (1) add global dummy data (different client with lots of values) -> reuse in development data reset and all of those main-classes
* (2) stack trace anzeigen; log file path clickable machen
- (1) remove language support again
* (3) chinese star sign calculator
