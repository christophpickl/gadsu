package at.cpickl.gadsu.client.xprops

import at.cpickl.gadsu.client.Client
import org.slf4j.LoggerFactory
import javax.inject.Inject

interface XPropsService {

    fun load(client: Client): ClientXProps
    fun update(client: Client)

}

class XPropsServiceImpl @Inject constructor(
        private val repo: XPropsSqlRepository
) : XPropsService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun load(client: Client): ClientXProps {
        log.debug("load(client={})", client)
        return ClientXProps(emptyMap())
    }

    override fun update(client: Client) {
        log.debug("loaupdate(client={})", client)
    }

}
