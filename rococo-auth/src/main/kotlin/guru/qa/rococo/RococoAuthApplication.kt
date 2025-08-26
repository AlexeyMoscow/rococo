package guru.qa.rococo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RococoAuthApplication

fun main(args: Array<String>) {
    runApplication<RococoAuthApplication>(*args)
}