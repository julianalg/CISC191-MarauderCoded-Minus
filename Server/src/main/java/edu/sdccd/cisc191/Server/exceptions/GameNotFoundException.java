package edu.sdccd.cisc191.Server.exceptions;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(Long id) {
        super("Could not find game " + id);

    }
}

