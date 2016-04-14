package at.cpickl.gadsu.testinfra

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod


abstract class HsqldbTest {

    private var database: EmbeddedDatabase? = null
    private var unsafeJdbc: JdbcTemplate? = null

    abstract fun sqlScripts(): Array<String>
    abstract fun resetTables(): Array<String>

    @BeforeClass
    fun initDb() {
        val builder = EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).setSeparator(";")
        sqlScripts().forEach { builder.addScript("/gadsu/persistence/" + it) }
        database = builder.build()
        unsafeJdbc = JdbcTemplate(database)
    }

    @BeforeMethod
    fun resetDb() {
        resetTables().forEach { jdbc().execute("DELETE FROM $it") }
    }

    @AfterClass
    fun shutdownDb() {
        database?.shutdown()
    }

    protected fun jdbc(): JdbcTemplate = unsafeJdbc!!

}
