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
import at.cpickl.gadsu.treatment.*
import at.cpickl.gadsu.treatment.dyn.DynTreatmentService
import at.cpickl.gadsu.treatment.dyn.DynTreatmentServiceImpl
import at.cpickl.gadsu.treatment.dyn.RepositoryFacade
import at.cpickl.gadsu.treatment.dyn.RepositoryFacadeImpl
import at.cpickl.gadsu.treatment.dyn.treats.*
import com.google.common.eventbus.EventBus
import com.google.inject.Guice
import com.google.inject.testing.fieldbinder.Bind
import com.google.inject.testing.fieldbinder.BoundFieldModule
import com.google.inject.util.Modules
import org.joda.time.DateTime
import org.mockito.Mockito.mock
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import javax.inject.Inject


@Test(groups = arrayOf("integration", "guice"))
// http://testng.org/doc/documentation-main.html#guice-dependency-injection
//@Guice(modules = arrayOf(ClientModule::class))
abstract class GuiceIntegrationTest {

    // https://github.com/google/guice/wiki/BoundFields
    @Bind protected lateinit var mockClientRepository: ClientRepository
    @Bind protected lateinit var mockTreatmentRepository: TreatmentRepository
    @Bind protected lateinit var mockAppointmentRepository: AppointmentRepository
    @Bind protected lateinit var mockXPropsRepository: XPropsSqlRepository

    @Bind private var _clock: Clock = SimpleTestableClock()
    protected var clock = _clock as SimpleTestableClock
    @Bind private var _idGenerator: IdGenerator = SimpleTestableIdGenerator()
    protected var idGenerator = _idGenerator as SimpleTestableIdGenerator

    @Inject protected lateinit var bus: EventBus

    protected lateinit var busListener: TestBusListener

    @BeforeMethod
    fun init() {
        busListener = TestBusListener()
        mockClientRepository = mock(ClientRepository::class.java)
        mockTreatmentRepository = mock(TreatmentRepository::class.java)
        mockAppointmentRepository = mock(AppointmentRepository::class.java)
        mockXPropsRepository = mock(XPropsSqlRepository::class.java)

        initTestGuice(this)

        bus.register(busListener)
    }

}

fun initTestGuice(testClass: Any) {
    Guice.createInjector(
            Modules.override(
                    GadsuModule(Args(null, "jdbc:hsqldb:mem:${testClass.javaClass.simpleName}", false, null))
            ).with(BoundFieldModule.of(testClass))
    ).injectMembers(testClass)
}

object IntegrationServiceLookuper {

    fun lookupTreatmentService(jdbcx: SpringJdbcx,
                               now: DateTime = TEST_DATETIME1,
                               clock: Clock = SimpleTestableClock(now),
                               bus: EventBus = EventBus(),
                               defaultGeneratedId: String = TEST_UUID1,
                               idGenerator: IdGenerator = SimpleTestableIdGenerator(defaultGeneratedId),
                               treatmentRepository: TreatmentRepository = TreatmentJdbcRepository(jdbcx, idGenerator),
                               meridiansRepo: TreatmentMeridiansRepository = TreatmentMeridiansJdbcRepository(jdbcx),
                               haraDiagnosisRepository: HaraDiagnosisRepository = HaraDiagnosisJdbcRepository(jdbcx),
                               tongueDiagnosisRepository: TongueDiagnosisRepository = TongueDiagnosisJdbcRepository(jdbcx),
                               bloodPressureRepository: BloodPressureRepository = BloodPressureJdbcRepository(jdbcx),
                               pulseDiagnosisRepository: PulseDiagnosisRepository = PulseDiagnosisJdbcRepository(jdbcx),
                               repositoryFacade: RepositoryFacade = RepositoryFacadeImpl(
                                       haraDiagnosisRepository,
                                       tongueDiagnosisRepository,
                                       bloodPressureRepository,
                                       pulseDiagnosisRepository),
                               dynTreatmentService: DynTreatmentService = DynTreatmentServiceImpl(repositoryFacade),
                               multiProtocolRepository: MultiProtocolRepository = MultiProtocolJdbcRepository(jdbcx, idGenerator),
                               meridiansService: TreatmentMeridiansService = TreatmentMeridiansServiceImpl(meridiansRepo)
    ): TreatmentService {
        return TreatmentServiceImpl(treatmentRepository, dynTreatmentService, meridiansService, multiProtocolRepository, jdbcx, bus, clock)
    }
}
