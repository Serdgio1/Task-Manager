package com.serdgio.taskmanager.service;

public record LoadWarning(Code code, String message, String sourceLine) {
    public enum Code {
        LEGACY_FORMAT,
        UNSUPPORTED_LEGACY_LINE,
        MALFORMED_LINE
    }
}

