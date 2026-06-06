package com.urlshortner.urlshortner.dtos.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

/**
 * This class defines the schema of the response. It is used to encapsulate data prepared by
 * the server side, this object will be serialized to JSON before sent back to the client end.
 */
/****/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String status; // Two values: success means success, error means not success
    private String message; // Response message
    private Object data; // The response payload
    private Object errors;
    private Integer statusCode; // Status code. e.g., 200
    private Object metadata;
    @Builder.Default
    private Instant timestamp = Instant.now();
}