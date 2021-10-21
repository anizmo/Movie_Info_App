package com.anizmocreations.bmstask.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Crew (
    var job : String?= null,
    var name : String?= null,
    var profile_path : String? = null
): Serializable