package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.persistence.SpringJdbcx
import at.cpickl.gadsu.testinfra.assertEmptyTable
import at.cpickl.gadsu.testinfra.setupTestDatabase
import org.hsqldb.jdbc.JDBCDataSource
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test(groups = arrayOf("hsqldb", "integration"))
class JdbcPrefsTest : PrefsTest() {

    companion object {
        val TABLE = JdbcPrefs.TABLE
    }

    private var dataSource: JDBCDataSource? = null
    private lateinit var jdbcx: SpringJdbcx

    @BeforeClass
    fun initDb() {
        val (dataSource, jdbcx) = setupTestDatabase(javaClass)
        this.dataSource = dataSource
        this.jdbcx = jdbcx
    }

    @BeforeMethod
    fun resetState() {
        jdbcx.deleteTable(TABLE)
    }

    @AfterClass
    fun shutdownDb() {
        // could have happened, that @BeforeClass failed, in this case shutting down will fail as a consequence. avoid this!
        dataSource?.connection?.close()
    }

    override fun createPrefs(): Prefs {
        return JdbcPrefs(jdbcx)
    }

    fun `clear, empties table`() {
        prefs.clear()

        jdbcx.assertEmptyTable(TABLE)
    }

}
