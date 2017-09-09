GADSU REVIEW MIT RENE

!!! schreibt keine logs raus (auf win)
! wenn starten, dann kann man klient deaktivieren?!?
! acupoints db bissi higher ranken (minimal set finishen)
?! 5e window kann man nicht zu machen?!
! ctrl+4 change auf tab assistent
! wenn ungueltige mail eingegeben, und focus lost => rot umranden
	-> ansonsten erst beim speichern warnen...
! acupunct window (und alle anderen auch) mit CMD+W schliessbar
! groesseres bild vom client schon auf hauptseite (evtl mit klick drauf in simple popup; ebenso auf treatment view bzw UEBERALL wo man das foto sieht)
! BACK button in treatment view hervorheben => links unterhalb meridian selector grosses pfeilchen nach links rendern
! bei treatment prev/next button => "2 von 9" rendern
! treatment list nur single selection (kein multi, geht eh net, zb loeschen bei mehrfachselektion loescht nur einen)
! hat versucht bei puls den "0" zu selektieren, damit hats zu nix gfuehrt! weil onFocus schon alles vorselektiert :(
!! beep() bei add dyntreat wenn alle da sind ist annoying => just disable button
! menu item "Ansicht" bietet immer alle globalen navigations aktionen; zb in treatment view, zurueck navigieren zu main/client view
! wenn in treatment view, hat versucht zu schliessen, und hat app beendet => wollte nicht!! => sollte evtl nur zurueck navigieren, wie erwartet?!
!! wenn in multiprop edit mode => mit enter finishen
!!! exception dialog ist leer?!
- CTRL+B in menu bar
!! BUG: gcal sync funkt nicht, window dahinter
!!! BUG: wenn falscher gcal kalender name, doofe fehlermeldung
! higher prio fuer treatment table renderer (icons von meridiane, dyntreat ergebnisse)
! prefs window groesser machen (ist scrollbar sichtbar)
	- threshold ist nicht selbsterklaerend was das ist
	- confirmation mail auch besser erklaeren
! splash screen anderes hintergrund bild, andere schrift; serioeser machen
! wenn neuen klient anlegen, dann "assistent tab" ist disabled (erst wenn yetPersisted enablen)
! mails empfangen checkbox rechts neben mail adresse rendern, in selbe zeile
! die buttons "neu klient anlegen" VS "neu anlegen" ist verwirrend => rename!
! sternzeichen rechts von berechnete jahre anzeigen
! wenn "bl1" eingeben => autokorrektur auf "Bl1" und erkennen
! devcontroller + fuer screenshot: erstellt klient der viele (5+) behandlungen hat

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




!!! BUG: treatment list gets filled from wrong client
	* klient A mit 1 treat
	* neuen klient anlegen, was aendern
	* klient A klicken, modifier schreit -> "abbrechen"
	=> suddenly treat from A is shown!!!
21:48:03 [AWT-EventQueue-0] [ERROR] at.cpickl.gadsu.GadsuModule - Context: event=at.cpickl.gadsu.treatment.PrepareNewTreatmentEvent@800ef45, subscriber=at.cpickl.gadsu.treatment.view.TreatmentController$$EnhancerByGuice$$b5f84e23@7de0c6ae, method=public void at.cpickl.gadsu.treatment.view.TreatmentController.onPrepareNewTreatmentEvent(at.cpickl.gadsu.treatment.PrepareNewTreatmentEvent)
21:48:03 [AWT-EventQueue-0] [ERROR] a.c.g.GlobalExceptionHandler - Uncaught exception, going to die!
at.cpickl.gadsu.persistence.PersistenceException: Persistable must have set an ID! (Client(id=null, created=2017-09-08T21:47:51.670+02:00, state=ACTIVE, firstName=, lastName=, nickNameExt=, nickNameInt=, contact=Contact(mail=, phone=, street=, zipCode=, city=), knownBy=, wantReceiveMails=true, birthday=null, gender=UNKNOWN, countryOfOrigin=, origin=, relationship=UNKNOWN, job=, children=, hobbies=, note=, textImpression=, textMedical=, textComplaints=, textPersonal=, textObjective=, textMainObjective=, textSymptoms=, textFiveElements=, textSyndrom=, category=B, donation=UNKNOWN, tcmNote=, picture=DefaultImage(classpath=/gadsu/images/profile_pic-default_man.png), cprops=CProps(props(0)=)))
	at at.cpickl.gadsu.persistence.PersistenceKt.ensurePersisted(persistence.kt:185)
	at at.cpickl.gadsu.treatment.TreatmentJdbcRepository.calculateMaxNumberUsed(persistence.kt:135)
	at at.cpickl.gadsu.treatment.TreatmentServiceImpl.calculateNextNumber(service.kt:100)
	at at.cpickl.gadsu.treatment.view.TreatmentController.changeToTreatmentView(controller.kt:203)
	at at.cpickl.gadsu.treatment.view.TreatmentController.onPrepareNewTreatmentEvent(controller.kt:71)
	at at.cpickl.gadsu.service.LoggedAspect.invoke(aop.kt:40)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at com.google.common.eventbus.Subscriber.invokeSubscriberMethod(Subscriber.java:91)
	at com.google.common.eventbus.Subscriber$SynchronizedSubscriber.invokeSubscriberMethod(Subscriber.java:150)
	at com.google.common.eventbus.Subscriber$1.run(Subscriber.java:76)
	at com.google.common.util.concurrent.MoreExecutors$DirectExecutor.execute(MoreExecutors.java:399)
	at com.google.common.eventbus.Subscriber.dispatchEvent(Subscriber.java:71)
	at com.google.common.eventbus.Dispatcher$PerThreadQueuedDispatcher.dispatch(Dispatcher.java:116)
	at com.google.common.eventbus.EventBus.post(EventBus.java:217)
	at at.cpickl.gadsu.view.components.EventButton$1.actionPerformed(button.kt:20)
	at javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022)
	at javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348)
	at javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402)
	at javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259)
	at javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252)
	at java.awt.Component.processMouseEvent(Component.java:6533)
	at javax.swing.JComponent.processMouseEvent(JComponent.java:3324)
	at java.awt.Component.processEvent(Component.java:6298)
	at java.awt.Container.processEvent(Container.java:2236)
	at java.awt.Component.dispatchEventImpl(Component.java:4889)
	at java.awt.Container.dispatchEventImpl(Container.java:2294)
	at java.awt.Component.dispatchEvent(Component.java:4711)
	at java.awt.LightweightDispatcher.retargetMouseEvent(Container.java:4888)
	at java.awt.LightweightDispatcher.processMouseEvent(Container.java:4525)
	at java.awt.LightweightDispatcher.dispatchEvent(Container.java:4466)
	at java.awt.Container.dispatchEventImpl(Container.java:2280)
	at java.awt.Window.dispatchEventImpl(Window.java:2746)
	at java.awt.Component.dispatchEvent(Component.java:4711)
	at java.awt.EventQueue.dispatchEventImpl(EventQueue.java:758)
	at java.awt.EventQueue.access$500(EventQueue.java:97)
	at java.awt.EventQueue$3.run(EventQueue.java:709)
	at java.awt.EventQueue$3.run(EventQueue.java:703)
	at java.security.AccessController.doPrivileged(Native Method)
	at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:80)
	at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:90)
	at java.awt.EventQueue$4.run(EventQueue.java:731)
	at java.awt.EventQueue$4.run(EventQueue.java:729)
	at java.security.AccessController.doPrivileged(Native Method)
	at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:80)
	at java.awt.EventQueue.dispatchEvent(EventQueue.java:728)
	at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:201)
	at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:116)
	at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:105)
	at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:101)
	at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:93)
	at java.awt.EventDispatchThread.run(EventDispatchThread.java:82)
	
	
* (2) @UI: client view die tabs alle mit CMD+number ansprechbar (implizit sobald tab hinzugefuegt wird, weil daweil is es haendisch deshalb funkts fuern assistent nicht!) 
* (3) treatment 30min statt 15min schritte (needs some (implicit?!) migration)
* (2) on confirmation mail: first check freemarker template and report proper error immediately
* (1) add global dummy data (different client with lots of values) -> reuse in development data reset and all of those main-classes
* (2) stack trace anzeigen; log file path clickable machen
