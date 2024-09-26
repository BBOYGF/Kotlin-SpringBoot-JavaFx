package com.app.controller
import com.app.service.MyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class MyController {

    @Autowired
    private lateinit var myService: MyService

    @GetMapping("/greet")
    fun greet(): String {
        return myService.greet()
    }
}