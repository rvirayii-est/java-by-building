package dev.javabybuilding.api;

/**
 * Every error the API sends has this ONE shape, so a client writes one piece of code to read all of them:
 * the status number, its standard words, a message a person can read, and the path that was asked for.
 */
public record ErrorResponse(int status, String error, String message, String path) {}
