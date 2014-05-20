package com.thoughtworks.rnr.saml.util;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.stereotype.Component;

/**
 * An implementation of Clock for testing purposes
 */
@Component
public class NineFiftyFiveAMClock implements com.thoughtworks.rnr.saml.util.Clock {

    private String instant = "2014-05-20T09:56:00.000-05:00";
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
