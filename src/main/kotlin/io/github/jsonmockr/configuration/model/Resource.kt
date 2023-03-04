package io.github.jsonmockr.configuration.model

import io.github.jsonmockr.Utils

data class Resource(
    val name: String,
    val seedData: SeedData? = null,
    val generator: GeneratorConfig? = null,
    val strictValidation: Boolean? = false,
    val envelope: String? = null,
    val idField: String? = Utils.DEFAULT_ID,
    val totalElements: String? = null,
    val idType: IdType? = IdType.String,
    val createOnPut: Boolean? = false,
    val idLength: Int? = Utils.DEFAULT_ID_LENGTH,
)
