package at.cpickl.gadsu.client.xprops

import com.google.inject.AbstractModule


class XPropsModule : AbstractModule() {
    override fun configure() {
        bind(XPropsSqlRepository::class.java).to(XPropsSqlJdbcRepository::class.java)
        bind(XPropsService::class.java).to(XPropsServiceImpl::class.java)
    }
}
