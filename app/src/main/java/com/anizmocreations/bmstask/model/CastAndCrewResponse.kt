package com.anizmocreations.bmstask.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CastAndCrewResponse (
    var crew : ArrayList<Crew>?= null,
    var cast : ArrayList<Cast>?= null
): Serializable