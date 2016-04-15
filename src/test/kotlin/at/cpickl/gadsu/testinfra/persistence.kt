package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.DatabaseManager
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod


abstract class HsqldbTest {

    private var dataSource: EmbeddedDatabase? = null
    private var jdbc: JdbcTemplate? = null

    abstract fun resetTables(): Array<String>

    @BeforeClass
    fun initDb() {
        val builder = EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).setSeparator(";")
        dataSource = builder.build()
        DatabaseManager(dataSource!!).migrateDatabase()
        jdbc = JdbcTemplate(dataSource)
    }

    @BeforeMethod
    fun resetDb() {
        resetTables().forEach { jdbc().execute("DELETE FROM $it") }
    }

    @AfterClass
    fun shutdownDb() {
        dataSource?.shutdown()
    }

    protected fun jdbc(): JdbcTemplate = jdbc!!

}
