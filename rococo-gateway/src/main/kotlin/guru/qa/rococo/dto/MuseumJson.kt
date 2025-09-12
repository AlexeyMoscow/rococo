package guru.qa.rococo.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
data class MuseumJson(
    val id: UUID,
    val title: String,
    val description: String?,
    val photo: String?,
    val geo: GeoJson
)
@JsonIgnoreProperties(ignoreUnknown = true)
data class GeoJson (
    val city: String,
    val country: Country?
)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Country (
    val id: UUID,
    val name: String
)

