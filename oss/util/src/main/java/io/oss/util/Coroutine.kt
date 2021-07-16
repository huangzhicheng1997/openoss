package io.oss.util

import kotlinx.coroutines.*
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Supplier
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.system.measureTimeMillis

/**
 * @Author zhicheng
 * @Date 2021/6/21 7:09 下午
 * @Version 1.0
 */
class Coroutine constructor(val a: Boolean?) {

    object test {
        @JvmStatic
        fun main(args: Array<String>) {

            runBlocking {

                launch {

                    withContext(Dispatchers.Unconfined) {
                        val async = async {
                            while (true) {
                                println(Thread.currentThread().id)
                            }
                        }
                        val async2 = async {
                            while (true) {
                                println(Thread.currentThread().id)
                            }
                        }
                        async.join()
                        async2.join()
                    }

                }

                launch {
                    withContext(Dispatchers.Unconfined) {
                        while (true) {
                            delay(1000)
                        }
                    }
                }

            }

        }

    }

}

fun test() {
    while (true) {

    }
}
