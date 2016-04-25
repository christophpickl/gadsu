package at.cpickl.gadsu.client.props

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientProps
import at.cpickl.gadsu.client.Prop
import org.slf4j.LoggerFactory
import java.util.HashMap
import javax.inject.Inject

interface PropsService {

    fun load(client: Client): ClientProps
    fun update(client: Client): Client

}

class PropsServiceImpl @Inject constructor(
        private val repository: ClientPropsRepository
) : PropsService {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun load(client: Client): ClientProps {
        log.debug("load(client.id={})", client.id)

        val properties = HashMap<String, Prop>()
        val sqlProps = repository.readAllFor(client)
        sqlProps.data.keys.forEach {
            val sqlProp = sqlProps.data.get(it)!!
            val prop = transformSqlPropToProp(it, sqlProp)
            properties.put(it, prop)
        }
        return ClientProps(properties)
    }

    override fun update(client: Client): Client {
        log.debug("update(client={})", client)

        val sqlData = HashMap<String, SqlPropType>()
        val props = client.props.properties
        props.keys.forEach {
            val genericProp = props[it]!!
            val sqlProp = transformPropToSqlProp(it, genericProp)
            sqlData.put(it, sqlProp)
        }
        val sqlProps = SqlProps(sqlData)

        repository.reset(client.id!!, sqlProps)
        return client
    }

}

