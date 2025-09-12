package guru.qa.rococo.dto

import java.util.UUID

data class ArtistJson(
    val id: UUID,
    val name: String,
    val country: String? = null
)