package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.Args
import at.cpickl.gadsu.GadsuModule
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.treatment.TreatmentRepository
import com.google.common.eventbus.EventBus
import com.google.inject.Guice
import com.google.inject.testing.fieldbinder.Bind
import com.google.inject.testing.fieldbinder.BoundFieldModule
import com.google.inject.util.Modules
import org.mockito.Mockito
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.util.prefs.Preferences
import javax.inject.Inject


@Test(groups = arrayOf("integration", "guice"))
// http://testng.org/doc/documentation-main.html#guice-dependency-injection
//@Guice(modules = arrayOf(ClientModule::class))
abstract class GuiceIntegrationTest {
    private val preferencesNode = javaClass
    private val preferencesNodeName = preferencesNode.name

    // https://github.com/google/guice/wiki/BoundFields
    @Bind protected var mockClientRepository: ClientRepository = Mockito.mock(ClientRepository::class.java)
    @Bind protected var mockTreatmentRepository: TreatmentRepository = Mockito.mock(TreatmentRepository::class.java)

    @Bind private var _clock: Clock = SimpleTestableClock()
    protected var clock = _clock as SimpleTestableClock
    @Bind private var _idGenerator: IdGenerator = SimpleTestableIdGenerator()
    protected var idGenerator = _idGenerator as SimpleTestableIdGenerator

    @Inject protected var bus: EventBus = EventBus()

    protected var busListener: AnyBusListener = AnyBusListener()

    @BeforeMethod
    fun init() {
        val prefs = Preferences.userNodeForPackage(preferencesNode)
        prefs.clear()
        prefs.flush()

        busListener = AnyBusListener()
        mockClientRepository = Mockito.mock(ClientRepository::class.java)
        mockTreatmentRepository = Mockito.mock(TreatmentRepository::class.java)

        Guice.createInjector(
                Modules.override(
                        GadsuModule(Args(null, "jdbc:hsqldb:mem:notUsed", false, preferencesNodeName))
                ).with(BoundFieldModule.of(this))
        ).injectMembers(this)

        bus.register(busListener)
    }

}
