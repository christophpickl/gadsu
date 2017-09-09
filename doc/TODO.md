- remove language support again

GADSU REVIEW MIT RENE

!!! schreibt keine logs raus (auf win)
! wenn starten, dann kann man klient deaktivieren?!?
?! 5e window kann man nicht zu machen?!
! acupunct window (und alle anderen auch) mit CMD+W schliessbar
! ctrl+4 change auf tab assistent
! wenn ungueltige mail eingegeben, und focus lost => rot umranden
	-> ansonsten erst beim speichern warnen...
! treatment list nur single selection (kein multi, geht eh net, zb loeschen bei mehrfachselektion loescht nur einen)
!! beep() bei add dyntreat wenn alle da sind ist annoying => just disable button
!! wenn in multiprop edit mode => mit enter finishen
! prefs window groesser machen (ist scrollbar sichtbar)
	- threshold ist nicht selbsterklaerend was das ist
	- confirmation mail auch besser erklaeren
! die buttons "neu klient anlegen" VS "neu anlegen" ist verwirrend => rename!
! sternzeichen rechts von berechnete jahre anzeigen

--
!! BUG: wenn falscher gcal kalender name, doofe fehlermeldung
! splash screen anderes hintergrund bild, andere schrift; serioeser machen
! wenn "bl1" eingeben => autokorrektur auf "Bl1" und erkennen
! devcontroller + fuer screenshot: erstellt klient der viele (5+) behandlungen hat
--
! [2] BACK button in treatment view hervorheben => links unterhalb meridian selector grosses pfeilchen nach links rendern
! [3] bei treatment prev/next button => "2 von 9" rendern
! acupoints db bissi higher ranken (minimal set finishen)
! groesseres bild vom client schon auf hauptseite (evtl mit klick drauf in simple popup; ebenso auf treatment view bzw UEBERALL wo man das foto sieht)
! hat versucht bei blut puls den "0" zu selektieren, damit hats zu nix gfuehrt! weil onFocus schon alles vorselektiert :(
! UJE: wenn in treatment view, hat versucht zu schliessen, und hat app beendet => wollte nicht!! => sollte evtl nur zurueck navigieren, wie erwartet?!
- CTRL+B in menu bar
-  menu item "Ansicht" bietet immer alle globalen navigations aktionen; zb in treatment view, zurueck navigieren zu main/client view
!! BUG: gcal sync funkt nicht, window dahinter
! higher prio fuer treatment table renderer (icons von meridiane, dyntreat ergebnisse)
! wenn neuen klient anlegen, dann "assistent tab" ist disabled (erst wenn yetPersisted enablen)
! mails empfangen checkbox rechts neben mail adresse rendern, in selbe zeile

-- ad website:
	* sagen dass man JAVA braucht
	* feature liste ueberarbeiten, vereinfachen, nur eine sektion auch fuer roadmap
-- [2] eigene website machen

WIN REMARKS
* allgemein bg ist total weiss => soll grau sein
-- breite verschieben von klientliste
- window icon sind default ones (zb about)
-- dragndrop versucht bei meridian selector
// rene hat XLS mit akupunkturpunkte


* (2) @UI: client view die tabs alle mit CMD+number ansprechbar (implizit sobald tab hinzugefuegt wird, weil daweil is es haendisch deshalb funkts fuern assistent nicht!) 
* (3) treatment 30min statt 15min schritte (needs some (implicit?!) migration)
* (2) on confirmation mail: first check freemarker template and report proper error immediately
* (1) add global dummy data (different client with lots of values) -> reuse in development data reset and all of those main-classes
* (2) stack trace anzeigen; log file path clickable machen
