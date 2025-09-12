package guru.qa.rococo.controller

import guru.qa.rococo.dto.MuseumJson
import guru.qa.rococo.service.MuseumService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MuseumController (
    private val museumService: MuseumService
){
    @GetMapping("/api/museum")
    fun getAllMuseum(
        @RequestParam(required = false) name: String?,
        @PageableDefault(size = 10) pageable: Pageable
    ): Page<MuseumJson> = museumService.getAll(name, pageable)

}