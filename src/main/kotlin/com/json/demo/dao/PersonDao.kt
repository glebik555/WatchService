package com.json.demo.dao

import com.json.demo.model.person
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component

@Component
interface PersonDao : CrudRepository<person,Int> {
}