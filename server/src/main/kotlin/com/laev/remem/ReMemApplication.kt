package com.laev.remem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class ReMemApplication {

}

fun main(args: Array<String>) {
	runApplication<ReMemApplication>(*args)
}
