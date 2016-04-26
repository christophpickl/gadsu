package at.cpickl.gadsu.client.props

import com.google.inject.AbstractModule

class PropsModule : AbstractModule() {
    override fun configure() {
        bind(ClientPropsRepository::class.java).to(ClientPropsSpringJdbcRepository::class.java)
        bind(PropsService::class.java).to(PropsServiceImpl::class.java)
    }
}
