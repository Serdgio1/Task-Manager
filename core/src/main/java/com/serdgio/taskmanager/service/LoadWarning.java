package com.serdgio.taskmanager.service;

/**
 * Structured non-fatal warning produced while loading persisted tasks.
 *
 * @param code warning category
 * @param message human-readable warning text
 * @param sourceLine original line that triggered the warning
 */
public record LoadWarning(Code code, String message, String sourceLine) {
    /**
     * Known warning categories for load-time diagnostics.
     */
    public enum Code {
        LEGACY_FORMAT,
        UNSUPPORTED_LEGACY_LINE,
        MALFORMED_LINE
    }
}
