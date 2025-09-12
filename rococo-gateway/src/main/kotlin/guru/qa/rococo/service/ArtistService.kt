package guru.qa.rococo.service

import guru.qa.rococo.dto.ArtistJson
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class ArtistService {

    //Имитация БД на 125 строк
    private val data: List<ArtistJson> = (1..125).map { i ->
        ArtistJson(
            id = UUID.nameUUIDFromBytes("artist-$i".toByteArray()),
            name = "Artist $i",
            country = when {
                i % 3 == 0 -> "France"
                i % 3 == 1 -> "Usa"
                else -> "Spain"
            }
        )
    }

    /**
     * Возвращает страницу художников с опциональным фильтром по имени (contains, ignore-case).
     * ВАЖНО: Pageable.pageNumber — это НОМЕР СТРАНИЦЫ (0..N), Pageable.pageSize — размер страницы.
     */

    fun getAll(name: String?, pageable: Pageable): Page<ArtistJson> {
        val filtered = if (name.isNullOrBlank()) {
            data
        } else {
            data.filter { it.name.contains(name, ignoreCase = true) }
        }

        val startIndex = pageable.pageNumber * pageable.pageSize
        val endIndex = minOf(startIndex + pageable.pageSize, filtered.size)

        val content = if (startIndex >= filtered.size) {
            emptyList()
        } else {
            filtered.subList(startIndex, endIndex)
        }
        return PageImpl(content, pageable, filtered.size.toLong())
    }
}