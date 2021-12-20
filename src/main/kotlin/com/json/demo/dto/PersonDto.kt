package com.json.demo.dto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component("personDto")
data class PersonDto (
    @Value("personDto.name")
    var name: String = "",
    @Value("personDto.lastname")
    var lastname: String = "",
)