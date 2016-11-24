package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.service.GapiCredentials

fun readGapiCredentialsFromSysProps(): GapiCredentials {
    val gapiSecret = System.getProperty("GAPI_SECRET", "")
    val gapiId = System.getProperty("GAPI_ID", "")
    if (gapiId.isEmpty()) throw IllegalStateException("define VM argument: -DGAPI_ID")
    if (gapiSecret.isEmpty()) throw IllegalStateException("define VM argument: -DGAPI_SECRET")
    return GapiCredentials.buildNullSafe(gapiId, gapiSecret)!!
}
