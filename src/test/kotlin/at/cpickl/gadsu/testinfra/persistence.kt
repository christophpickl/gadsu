package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.DatabaseManager
import at.cpickl.gadsu.JdbcX
import at.cpickl.gadsu.SpringJdbcX
import at.cpickl.gadsu.service.IdGenerator
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod


abstract class HsqldbTest {
    companion object {
        init {
            TestLogger().configureLog()
        }
    }
    private var dataSource: EmbeddedDatabase? = null
    private var jdbcx: JdbcX? = null

    abstract fun resetTables(): Array<String>

    protected var idGenerator: IdGenerator = mock(IdGenerator::class.java)

    @BeforeClass
    fun initDb() {
        val builder = EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).setSeparator(";")
        dataSource = builder.build()
        DatabaseManager(dataSource!!).migrateDatabase()
        jdbcx = SpringJdbcX(dataSource!!)
    }

    @BeforeMethod
    fun resetDb() {
        resetTables().forEach { jdbcx!!.execute("DELETE FROM $it") }
    }

    @AfterClass
    fun shutdownDb() {
        dataSource?.shutdown()
    }

    protected fun jdbcx(): JdbcX = jdbcx!!

    protected fun whenGenerateIdReturnTestUuid() {
        Mockito.`when`(idGenerator.generate()).thenReturn(TEST_UUID)
    }

    protected fun nullJdbcx() = mock(JdbcX::class.java)

}

