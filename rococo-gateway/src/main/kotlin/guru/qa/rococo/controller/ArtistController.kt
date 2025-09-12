package guru.qa.rococo.controller

import guru.qa.rococo.dto.ArtistJson
import guru.qa.rococo.service.ArtistService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ArtistController(
    private val artistService: ArtistService
) {
    /**
     * Пример: /api/artist?page=0&size=10
     * С фильтром: /api/artist?name=5&page=0&size=5  (вернёт Artist 5, 15, 25, ...)
     */
    @GetMapping("/api/artist")
    fun getAll(
        @RequestParam(required = false) name: String?,
        @PageableDefault(size = 10) pageable: Pageable
    ): Page<ArtistJson> = artistService.getAll(name, pageable)
}