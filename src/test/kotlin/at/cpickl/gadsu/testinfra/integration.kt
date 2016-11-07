package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.Args
import at.cpickl.gadsu.GadsuModule
import at.cpickl.gadsu.appointment.AppointmentRepository
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.client.xprops.XPropsSqlRepository
import at.cpickl.gadsu.persistence.SpringJdbcx
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolJdbcRepository
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolRepository
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.treatment.DynTreatmentService
import at.cpickl.gadsu.treatment.DynTreatmentServiceImpl
import at.cpickl.gadsu.treatment.HaraDiagnosisJdbcRepository
import at.cpickl.gadsu.treatment.HaraDiagnosisRepository
import at.cpickl.gadsu.treatment.TreatmentJdbcRepository
import at.cpickl.gadsu.treatment.TreatmentRepository
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.treatment.TreatmentServiceImpl
import com.google.common.eventbus.EventBus
import com.google.inject.Guice
import com.google.inject.testing.fieldbinder.Bind
import com.google.inject.testing.fieldbinder.BoundFieldModule
import com.google.inject.util.Modules
import org.joda.time.DateTime
import org.mockito.Mockito.mock
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
    @Bind protected lateinit var mockClientRepository: ClientRepository
    @Bind protected lateinit var mockTreatmentRepository: TreatmentRepository
    @Bind protected lateinit var mockAppointmentRepository: AppointmentRepository
    @Bind protected lateinit var mockXPropsRepository: XPropsSqlRepository

    @Bind private var _clock: Clock = SimpleTestableClock()
    protected var clock = _clock as SimpleTestableClock
    @Bind private var _idGenerator: IdGenerator = SimpleTestableIdGenerator()
    protected var idGenerator = _idGenerator as SimpleTestableIdGenerator

    @Inject protected var bus: EventBus = EventBus()

    protected var busListener: TestBusListener = TestBusListener()

    @BeforeMethod
    fun init() {
        val prefs = Preferences.userNodeForPackage(preferencesNode)
        prefs.clear()
        prefs.flush()

        busListener = TestBusListener()
        mockClientRepository = mock(ClientRepository::class.java)
        mockTreatmentRepository = mock(TreatmentRepository::class.java)
        mockAppointmentRepository = mock(AppointmentRepository::class.java)
        mockXPropsRepository = mock(XPropsSqlRepository::class.java)

        Guice.createInjector(
                Modules.override(
                        GadsuModule(Args(null, "jdbc:hsqldb:mem:notUsed", false, preferencesNodeName, null))
                ).with(BoundFieldModule.of(this))
        ).injectMembers(this)

        bus.register(busListener)
    }

}

object IntegrationServiceLookuper {

    fun lookupTreatmentService(jdbcx: SpringJdbcx,
                               now: DateTime = TEST_DATETIME1,
                               clock: Clock = SimpleTestableClock(now),
                               bus: EventBus = EventBus(),
                               defaultGeneratedId: String = TEST_UUID1,
                               idGenerator: IdGenerator = SimpleTestableIdGenerator(defaultGeneratedId),
                               treatmentRepository: TreatmentRepository = TreatmentJdbcRepository(jdbcx, idGenerator),
                               haraDiagnosisRepository: HaraDiagnosisRepository = HaraDiagnosisJdbcRepository(jdbcx),
                               dynTreatmentService: DynTreatmentService = DynTreatmentServiceImpl(haraDiagnosisRepository),
                               multiProtocolRepository: MultiProtocolRepository = MultiProtocolJdbcRepository(jdbcx, idGenerator)
                               ): TreatmentService {
        return TreatmentServiceImpl(treatmentRepository, dynTreatmentService, multiProtocolRepository, jdbcx, bus, clock)
    }

}
