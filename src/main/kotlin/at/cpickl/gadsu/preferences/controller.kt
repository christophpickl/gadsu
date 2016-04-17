package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.QuitUserEvent
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject


class PreferencesController @Inject constructor(
        private val window: PreferencesWindow,
        private val prefs: Prefs
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onShowPreferencesEvent(@Suppress("UNUSED_PARAMETER") event: ShowPreferencesEvent) {
        log.debug("onShowPreferencesEvent(event)")
        window.initData(prefs.preferencesData ?: PreferencesData.DEFAULT)
        window.start()
    }

    @Subscribe fun onPreferencesWindowClosedEvent(@Suppress("UNUSED_PARAMETER") event: PreferencesWindowClosedEvent) {
        log.debug("onPreferencesWindowClosedEvent(event)")
        prefs.preferencesData = window.readData()
    }

    @Subscribe fun onQuitUserEvent(@Suppress("UNUSED_PARAMETER") event: QuitUserEvent) {
        log.debug("onQuitUserEvent(event)")

        prefs.preferencesData = window.readData() // store data back (again?! seems so...)
        window.close()
    }
}
