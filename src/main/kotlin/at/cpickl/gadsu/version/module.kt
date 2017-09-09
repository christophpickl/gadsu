package at.cpickl.gadsu.version

import at.cpickl.gadsu.global.GADSU_LATEST_VERSION_URL
import at.cpickl.gadsu.global.UserEvent
import com.google.inject.AbstractModule

class CheckForUpdatesEvent : UserEvent()

class VersionModule : AbstractModule() {

    override fun configure() {
        bind(LatestVersionFetcher::class.java).toInstance(WebLatestVersionFetcher(GADSU_LATEST_VERSION_URL))

        bind(VersionChecker::class.java).to(VersionCheckerImpl::class.java)
        bind(VersionUpdater::class.java).to(VersionUpdaterImpl::class.java).asEagerSingleton()

    }
}
