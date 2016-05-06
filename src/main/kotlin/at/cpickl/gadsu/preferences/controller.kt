package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.service.Logged
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject


@Logged
open class PreferencesController @Inject constructor(
        private val window: PreferencesWindow,
        private val prefs: Prefs
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe open fun onShowPreferencesEvent(@Suppress("UNUSED_PARAMETER") event: ShowPreferencesEvent) {
        window.initData(prefs.preferencesData)
        window.start()
    }

    @Subscribe open fun onPreferencesWindowClosedEvent(@Suppress("UNUSED_PARAMETER") event: PreferencesWindowClosedEvent) {
        prefs.preferencesData = window.readData()
    }

    @Subscribe open fun onQuitEvent(@Suppress("UNUSED_PARAMETER") event: QuitEvent) {
        prefs.preferencesData = window.readData() // store data back (again?! seems so...)
        window.close()
    }
}
