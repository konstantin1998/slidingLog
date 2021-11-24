package ru.sber;

import lombok.RequiredArgsConstructor;


public class Response {
    private final Status status;

    public Response(Status status) {
        this.status = status;
    }
}
