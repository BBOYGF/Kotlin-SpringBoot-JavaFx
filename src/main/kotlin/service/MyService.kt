package com.telecwin.javafx.service
import org.springframework.stereotype.Service

@Service
class MyService {
    fun greet(): String {
        return "Hello from MyService!"
    }
}