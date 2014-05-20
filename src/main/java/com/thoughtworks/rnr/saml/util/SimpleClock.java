package com.thoughtworks.rnr.saml.util;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

/**
 * A basic implementation for Clock interface
 */
@Component
public class SimpleClock implements Clock {

    @Override
    public String instant() {
        return new DateTime().toInstant().toString();
    }

    @Override
    public DateTime dateTimeNow() {
        return new DateTime();
    }

}
