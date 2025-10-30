package com.rr.room

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.rr"])
class RoomApplication
fun main(args: Array<String>) = runApplication<RoomApplication>(*args)
