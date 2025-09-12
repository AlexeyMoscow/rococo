package guru.qa.rococo.dto

import java.util.UUID

data class PaintingJson(
    val id: UUID,
    val title: String,
    val description: String,
    val content: String
)
