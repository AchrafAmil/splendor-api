package com.neogineer.splendor.api.utils


interface Logger {
    fun v(tag: String = "", message: String)
    fun d(tag: String = "", message: String)
    fun i(tag: String = "", message: String)
    fun w(tag: String = "", message: String)
    fun e(tag: String = "", message: String)
}

class PrintLogger : Logger {
    override fun v(tag: String, message: String) = println("V/$tag : $message")
    override fun d(tag: String, message: String) = println("D/$tag : $message")
    override fun i(tag: String, message: String) = println("I/$tag : $message")
    override fun w(tag: String, message: String) = println("W/$tag : $message")
    override fun e(tag: String, message: String) = println("E/$tag : $message")
}