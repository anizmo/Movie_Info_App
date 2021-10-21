package com.anizmocreations.bmstask.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Movie (
    var id: Int?= null,
    var title: String?= null,
    var poster_path: String?= null,
    var backdrop_path: String?= null,
    var release_date: String?= null,
    var overview: String?= null,
    var adult: Boolean?= null,
    var original_language: String?= null
):Serializable