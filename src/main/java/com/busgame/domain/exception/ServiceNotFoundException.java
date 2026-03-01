package com.busgame.domain.exception;

import com.busgame.domain.model.ServiceId;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(ServiceId id) {
        super("Service introuvable : " + id);
    }
}
