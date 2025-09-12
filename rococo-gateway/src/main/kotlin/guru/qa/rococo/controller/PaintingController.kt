package guru.qa.rococo.controller

import guru.qa.rococo.dto.PaintingJson
import guru.qa.rococo.service.PaintingService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PaintingController(
    private val paintingService: PaintingService
) {
    @GetMapping("/api/painting")
    fun getAll(
        @RequestParam(required = false) name: String?,
        @PageableDefault(size = 10) pageable: Pageable
    ): Page<PaintingJson> = paintingService.getAll(name, pageable)
}