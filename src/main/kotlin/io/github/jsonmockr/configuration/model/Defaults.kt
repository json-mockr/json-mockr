package io.github.jsonmockr.configuration.model

import io.github.jsonmockr.Utils

data class Defaults(
    val envelope: String? = null,
    val idField: String = Utils.DEFAULT_ID,
    val totalElements: String? = null,
    val idType: IdType = IdType.String,
    val strictValidation: Boolean = false,
    val createOnPut: Boolean = false,
    val authorization: Authorization = Authorization.None,
    val idLength: Int = Utils.DEFAULT_ID_LENGTH,
)
