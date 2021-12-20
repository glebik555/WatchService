package com.json.demo.service

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.json.demo.dao.PersonDao
import com.json.demo.dto.PersonDto
import com.json.demo.model.person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.lang.reflect.Type
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchService


@Service
@Component("personServiceImpl")
class PersonServiceImpl(

    @Autowired
    private val personDto: PersonDto, override val personDao: PersonDao
) : PersonService, ApplicationListener<ApplicationReadyEvent> {

    override fun getAllPerson(): List<person> = personDao.findAll().toList()

    override fun create(personIn: PersonDto) {
        val newPerson = person(
            name = personIn.name,
            lastname = personIn.lastname,
        )
        println(newPerson.name)
        personDao.save(newPerson)
    }

    private fun prompt(msg: String): String {
        print("$msg => ")
        return readLine() ?: ""
    }


    private fun Path.watch(): WatchService {
        //Create a watch service
        val watchService = this.fileSystem.newWatchService()

        //Register the service, specifying which events to watch
        register(
            watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.OVERFLOW,
            StandardWatchEventKinds.ENTRY_DELETE
        )

        //Return the watch service
        return watchService
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        val folder = "C:\\Users\\User\\Desktop\\WatchService" // delete this line and uncomment line from below to write path yourself
        //prompt("Enter a folder to watch")
        val path = Paths.get(folder)
        val watcher = path.watch()

        println("Press ctrl+c to exit")

        while (true) {
            //The watcher blocks until an event is available
            val key = watcher.take()

            key.pollEvents().forEach { it ->
                var fileName: String = it.context().toString()
                if (fileName.contains(".json")) {
                    var gson = Gson()
                    val bufferedReader: BufferedReader = File(folder + "\\" + fileName).bufferedReader()
                    val inputString = bufferedReader.use { it.readText() }
                    var personArray: ArrayList<PersonDto> = arrayListOf()
                    val typeMyType: Type = object : TypeToken<ArrayList<PersonDto?>?>() {}.getType()
                    val post: ArrayList<PersonDto> = gson.fromJson(inputString, typeMyType)
                    val size: Int = post.size

                    val personList: List<person> = getAllPerson()
                    for (index in 0 until size) {
                        val newPerson = person(
                            name = post[index].name,
                            lastname = post[index].lastname,
                        )
                        var flag: Boolean = true
                        for (indexPersonList in personList.indices) {
                            if (newPerson.name.equals(personList[indexPersonList].name) && newPerson.lastname.equals(personList[indexPersonList].lastname)) {
                                println("ignoring Person" + newPerson.name + " " + newPerson.lastname)
                                flag = false
                            }
                        }
                        if (flag) {
                            personDao.save(newPerson)
                        }
                    }
                }
                when (it.kind()
                    .name()) {
                    "ENTRY_CREATE" -> println("${it.context()} was created")
                    "ENTRY_MODIFY" -> println("${it.context()} was modified")
                    "OVERFLOW" -> println("${it.context()} overflow")
                    "ENTRY_DELETE" -> println("${it.context()} was deleted")
                }
            }
            //Call reset() on the key to watch for future events
            key.reset()

        }
    }


}

