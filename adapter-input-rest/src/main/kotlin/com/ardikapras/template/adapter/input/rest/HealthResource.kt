package com.ardikapras.template.adapter.input.rest

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/api/v1/health")
@Produces(MediaType.APPLICATION_JSON)
class HealthResource {
    @GET
    fun health(): Map<String, String> = mapOf("status" to "UP")
}
