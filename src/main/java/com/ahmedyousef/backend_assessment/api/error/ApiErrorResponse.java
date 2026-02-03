package com.ahmedyousef.backend_assessment.api.error;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        String traceId,
        List<Violation> violations
) {
    public record Violation(String field, String message) {
    }
}
