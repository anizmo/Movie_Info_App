package com.anizmocreations.bmstask.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ReviewsResponse (
    var results : ArrayList<Review>?= null,
    var total_pages : Int? = null,
    var total_results : Int? = null,
    var page: Int? = null,
    var id: Int? = null
): Serializable