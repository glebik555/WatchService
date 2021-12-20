package com.json.demo.service

import com.json.demo.dao.PersonDao
import com.json.demo.dto.PersonDto
import com.json.demo.model.person


interface PersonService {
    abstract val personDao: PersonDao
    fun create(personIn: PersonDto)
    fun getAllPerson(): List<person>
}