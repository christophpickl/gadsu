package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.persistence.Jdbcx
import com.google.inject.AbstractModule
import com.google.inject.assistedinject.Assisted
import org.slf4j.LoggerFactory

/*

@Guice(modules = arrayOf(MyTestngModule::class))
@Test
class SuperTestngTest {

    @Inject private var helperFact: GadsuTestHelperFactory = Mockito.mock(GadsuTestHelper::class.java) // avoid nullness

    private var helper: Helper = Mock(Helper)

    @BeforeMethod
    fun setup() {
        helper = helperFact.build(jdbcx())
    }

    fun test1() {

n         println("test1: $helper")
    }

    fun test2() {
        println("test2: $helper")
    }

}

 */
class MyTestngModule : AbstractModule() {
    override fun configure() {
//        bind(GadsuTestHelper::class.java).to(GadsuTestHelperImpl::class.java)
//        FIXME TEST build own test infrastructure with guice, which simplifies testing (manual wiring, full control)
        // BUT: also write infra to startup Gadsu guice context, plus override DataSource, for autowiring enabled
        // register factory
    }
}

interface GadsuTestHelperFactory {

}

interface GadsuTestHelper {

}
class GadsuTestHelperImpl(@Assisted private val jdbcx: Jdbcx) : GadsuTestHelper {
    private val log = LoggerFactory.getLogger(javaClass)
    init {
        log.debug("GadsuTestHelperImpl()")
    }
}
