package at.cpickl.gadsu.persistence

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import javax.sql.DataSource


interface Jdbcx {

    val jdbc: JdbcTemplate

    fun <E> query(sql: String, rowMapper: RowMapper<E>): MutableList<E>
    fun <E> query(sql: String, args: Array<out Any?>, rowMapper: RowMapper<E>): MutableList<E>
    fun <E> queryMaybeSingle(rowMapper: RowMapper<E>, sql: String, args: Array<out Any?>): E?
    fun <E> querySingle(rowMapper: RowMapper<E>, sql: String, vararg args: Any?): E

    fun update(sql: String, vararg args: Any?): Int
    fun updateSingle(sql: String, vararg args: Any?)

    fun deleteSingle(sql: String, vararg args: Any?)

    fun transactionSafe(function: () -> Unit)
    fun count(table: String, args: Array<in Any>, optionalWhereClause: String = ""): Int
    fun execute(sql: String)

}

class SpringJdbcx(private val dataSource: DataSource) : Jdbcx {
    private val log = LoggerFactory.getLogger(javaClass)

    override val jdbc = JdbcTemplate(dataSource)
    override fun <E> query(sql: String, rowMapper: RowMapper<E>): MutableList<E> {
        log.trace("query(sql='{}', rowMapper)", sql)
        return encapsulateException({ jdbc.query(sql, rowMapper) })
    }

    override fun <E> query(sql: String, args: Array<out Any?>, rowMapper: RowMapper<E>): MutableList<E> {
        log.trace("query(sql='{}', args={}, rowMapper)", sql, args)
        return encapsulateException({ jdbc.query(sql, args, rowMapper) })
    }

    override fun <E> queryMaybeSingle(rowMapper: RowMapper<E>, sql: String, args: Array<out Any?>): E? {
        return encapsulateException({
            val result = jdbc.query(sql, args, rowMapper)
            when (result.size) {
                0 -> null
                1 -> result[0]
                else -> throw PersistenceException("Expected not more than one returned but was ${result.size},by sql code: '$sql'!", PersistenceErrorCode.EXPECT_QUERY_SINGLE_ONE)
            }
        })
    }
    override fun <E> querySingle(rowMapper: RowMapper<E>, sql: String, args: Array<out Any?>): E {
        log.trace("querySingle(rowMapper, sql='{}', args)", sql)
        val maybe = queryMaybeSingle(rowMapper, sql, args)
        if (maybe == null) {
            throw PersistenceException("Expected exactly one item to be returned but was 0, by sql code: '$sql'!", PersistenceErrorCode.EXPECT_QUERY_SINGLE_ONE)
        }
        return maybe
    }

    override fun update(sql: String, vararg args: Any?): Int {
        log.trace("update(sql='{}', args={})", sql, args)
        return encapsulateException({ jdbc.update(sql, *args) })
    }

    override fun updateSingle(sql: String, vararg args: Any?) {
        val affectedRows = jdbc.update(sql, *args)
        if (affectedRows != 1) {
            throw PersistenceException("Expected exactly one row to be updated, but was: $affectedRows!", PersistenceErrorCode.EXPECT_UPDATE_ONE)
        }
    }

    override fun deleteSingle(sql: String, vararg args: Any?) {
        log.trace("deleteSingle(sql='{}', args={})", sql, args)
        encapsulateException {
            val affectedRows = jdbc.update(sql, *args)
            if (affectedRows != 1) {
                throw PersistenceException("Expected exactly one row to be deleted, but was: $affectedRows!", PersistenceErrorCode.EXPECT_DELETED_ONE)
            }
        }
    }

    override fun count(table: String, args: Array<in Any>, optionalWhereClause: String): Int =
            jdbc.queryForObject("SELECT COUNT(*) FROM $table $optionalWhereClause", args) { rs, rowNum -> rs.getInt(1) }

    override fun execute(sql: String) {
        log.trace("execute(sql='{}')", sql)
        encapsulateException { jdbc.execute(sql) }
    }

    override fun transactionSafe(function: () -> Unit) {
        log.trace("transactionSafe(function)")

        val wasAutoCommit = dataSource.connection.autoCommit
        dataSource.connection.autoCommit = false
        try {

            var committed = false
            try {
                function()
                dataSource.connection.commit()
                log.trace("Transaction committed successfully.")
                committed = true
            } finally {
                if (committed === false) {
                    log.warn("Rolling back transaction!")
                    dataSource.connection.rollback()
                }
            }

        } finally {
            dataSource.connection.autoCommit = wasAutoCommit
        }
    }

    private fun <E> encapsulateException(body: () -> E): E {
        try {
            return body()
        } catch (e: Exception) {
            throw PersistenceException("SQL execution failed! See cause for more details.", PersistenceErrorCode.UNKNOWN, e)
        }
    }

}
