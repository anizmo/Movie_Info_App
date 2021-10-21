package com.anizmocreations.bmstask.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimilarMovieResponse (
    var results : ArrayList<Movie>?= null,
    var total_pages : Int?= null,
    var total_results : Int?= null
): Serializable