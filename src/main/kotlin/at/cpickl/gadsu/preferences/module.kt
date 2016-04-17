package at.cpickl.gadsu.preferences

import com.google.inject.AbstractModule
import com.google.inject.Provider
import com.google.inject.Scopes
import javax.inject.Inject


class PreferencesModule(private val nodePrefsFqn: String?) : AbstractModule() {
    override fun configure() {

        bind(PreferencesController::class.java).asEagerSingleton()
        bind(PreferencesWindow::class.java).to(SwingPreferencesWindow::class.java).`in`(Scopes.SINGLETON)

        val nodeClass = if (nodePrefsFqn === null) JavaPrefs::class.java else Class.forName(nodePrefsFqn)
        bind(Prefs::class.java).toInstance(JavaPrefs(nodeClass))

        bind(PreferencesData::class.java).toProvider(JavaPrefsProviderAdapter::class.java)
    }
}

class JavaPrefsProviderAdapter @Inject constructor(
        private val prefs: Prefs
) : Provider<PreferencesData> {

    override fun get() = prefs.preferencesData

}