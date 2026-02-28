// domain/exception/DuplicateBusNumberException.java
package com.busgame.domain.exception;

public class DuplicateBusNumberException extends RuntimeException {
    public DuplicateBusNumberException(String busNumber) {
        super("Le numero de bus '" + busNumber + "' est deja utilise.");
    }
}