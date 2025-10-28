package com.tlsu.opluscamerapro.hook

abstract class BaseHook {
    var isInit: Boolean = false
    abstract fun init()
    companion object {
        var MODULE_PATH: String? = null
    }
}
