package com.json.demo.controller

import org.springframework.context.event.EventListener
import org.springframework.web.bind.annotation.RestController

@RestController
class PersonController(
) {
    @EventListener
    fun main(args: Array<String>){
        println("Hello World!")
    }
}