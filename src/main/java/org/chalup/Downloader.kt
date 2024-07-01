package org.chalup

import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import okio.use
import java.io.File

fun main(args: Array<String>) {
    val (inputsDir, sessionCookie) = args

    val httpClient = OkHttpClient()

    for (year in 2015..2023) {
        val yearDir = File(inputsDir, "$year").also { check((it.exists() && it.isDirectory) || it.mkdirs()) }

        for (day in 1..25) {
            val dayFile = File(yearDir, "day${day}.txt")

            print("Getting input for year $year day $day".padEnd(40, padChar = '.'))

            if (dayFile.exists()) {
                println("DONE")
            } else {
                val request = Request.Builder()
                    .url("https://adventofcode.com/$year/day/$day/input")
                    .addHeader("Cookie", "session=$sessionCookie")
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        println("FAIL!")
                    } else {
                        response.body!!.source().use { body ->
                            dayFile.sink().buffer().use { sink ->
                                sink.writeAll(body)
                                sink.flush()
                            }
                        }
                        println("DOWNLOADED!")
                    }
                }
            }
        }
    }
}