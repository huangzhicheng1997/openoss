package io.oss.util.util

import kotlinx.coroutines.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


/**
 * @Author zhicheng
 * @Date 2021/6/20 8:46 下午
 * @Version 1.0
 */

open class Test public constructor(test: Int) {
    public var xx: String = ""
    var b: String? = null
        get() {
            if (field == null) {
                field = "xxxxxx"
            }
            return field
        }

    lateinit var c: String
    open fun xxx() {
        val initialized = ::c.isInitialized

    }

}

class Test2 constructor(var test: Int) : Test(test) {
    var a: Int = 0

    constructor(test: Int, a: Int) : this(test) {
        this.a = a
        this.xx = "12"
    }

    constructor(test: Int, b: String) : this(test) {

    }


}


object main {
    @JvmStatic
    fun main(args: Array<String>) {
        var a=AtomicInteger(100);
        val threadPoolExecutor = ThreadPoolExecutor(10, 10, 0, TimeUnit.MINUTES, ArrayBlockingQueue(100))
        threadPoolExecutor.execute {
            repeat(100){
                GlobalScope.launch {
                   println(a.getAndDecrement())
                    delay(100000);
                }
            }
        }
    }
}