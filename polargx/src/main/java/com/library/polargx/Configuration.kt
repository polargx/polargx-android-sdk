package com.library.polargx

open class EnvConfiguration(
    open val name: String,
    open val server: String,
    open val supportedBaseDomains: String,
    open val isDevelopment: Boolean,
    open val isDebugging: Boolean
)

class DevEnvConfiguration(
    override val name: String = "Development",
    override val server: String = "8mr6rftgmb.execute-api.us-east-1.amazonaws.com/dev",
    override val supportedBaseDomains: String = ".biglittlecookies.com",
    override val isDevelopment: Boolean = true,
    override val isDebugging: Boolean,
) : EnvConfiguration(
    name = name,
    server = server,
    supportedBaseDomains = supportedBaseDomains,
    isDevelopment = isDevelopment,
    isDebugging = isDebugging
)

class ProdEnvConfiguration(
    override val name: String = "Production",
    override val server: String = "8mr6rftgmb.execute-api.us-east-1.amazonaws.com/prod",
    override val supportedBaseDomains: String = ".gxlnk.com",
    override val isDevelopment: Boolean = false,
    override val isDebugging: Boolean = false,
) : EnvConfiguration(
    name = name,
    server = server,
    supportedBaseDomains = supportedBaseDomains,
    isDevelopment = isDevelopment,
    isDebugging = isDebugging
)

object Configuration {
    const val BRAND = "Polar"
    var Env: EnvConfiguration = ProdEnvConfiguration()
}
