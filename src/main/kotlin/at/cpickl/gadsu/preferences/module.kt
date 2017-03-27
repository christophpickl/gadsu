package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.preferences.view.PreferencesSwingWindow
import at.cpickl.gadsu.preferences.view.PreferencesWindow
import com.google.inject.AbstractModule
import com.google.inject.Provider
import com.google.inject.Scopes
import javax.inject.Inject


class PreferencesModule() : AbstractModule() {
    override fun configure() {

        bind(PreferencesController::class.java).asEagerSingleton()
        bind(PreferencesWindow::class.java).to(PreferencesSwingWindow::class.java).`in`(Scopes.SINGLETON)

        bind(Prefs::class.java).to(JdbcPrefs::class.java).asEagerSingleton()

        bind(PreferencesData::class.java).toProvider(PreferencesDataProvider::class.java)
    }
}

class PreferencesDataProvider @Inject constructor(
        private val prefs: Prefs
) : Provider<PreferencesData> {

    override fun get() = prefs.preferencesData

}
