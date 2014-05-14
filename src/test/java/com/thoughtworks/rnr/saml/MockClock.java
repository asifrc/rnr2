package com.thoughtworks.rnr.saml;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

/**
 * An implementation of Clock for testing purposes
 */
public class MockClock implements com.thoughtworks.rnr.saml.util.Clock {

    private String instant;

    /**
     * @param instant a timestamp in ISO8601 format (yyyy-MM-ddTHH:mm:ss.SSSZZ)
     */
    public void setInstant(String instant) {
        this.instant = instant;
    }

    /**
     * @return the instant set by setInstant()
     */
    @Override
    public String instant() {
        return instant;
    }

    /**
     * @return teh instant set by setInstant() as a DateTime object
     */
    @Override
    public DateTime dateTimeNow() {
        return ISODateTimeFormat.dateTime().parseDateTime(instant);
    }
}
