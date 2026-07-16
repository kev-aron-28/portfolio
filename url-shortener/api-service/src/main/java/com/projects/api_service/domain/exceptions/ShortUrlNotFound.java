package com.projects.api_service.domain.exceptions;

public class ShortUrlNotFound extends RuntimeException {

    public ShortUrlNotFound() {
        super("The shortUrl provided does not exist");
    }
}
