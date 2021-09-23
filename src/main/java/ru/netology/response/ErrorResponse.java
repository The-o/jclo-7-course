package ru.netology.response;

public class ErrorResponse {

    public final String message;
    public final int id;

    public ErrorResponse(String message, int id) {
        this.message = message;
        this.id = id;
    }

}
