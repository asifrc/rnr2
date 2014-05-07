package com.thoughtworks.rnr.model;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public final class Constants {
    public static final double YEAR_IN_DAYS = 365.25d;
    public static final double DEFAULT_ACCRUAL_RATE = 10.0;
    public static final LocalDate START_DATE_JAN_1_2014 = new LocalDate(2014, 1, 1);

    public static final List<String> PERSONAL_DAY_CODES = new ArrayList<String>();
    public static final List<String> VACATION_DAY_CODES = new ArrayList<String>();

    public static final String OKTA_REDIRECT_URL = "https://thoughtworks.oktapreview.com/app/template_saml_2_0/k21tpw64VPAMDOMKRXBS/sso/saml";

    static{
        PERSONAL_DAY_CODES.add("Caregiver leave");
        PERSONAL_DAY_CODES.add("Sick (all except US/CAN)");
        PERSONAL_DAY_CODES.add("Dr, dentist ,antenatel (UK)");
        PERSONAL_DAY_CODES.add("Personal/Sick (US/CAN only)");
        PERSONAL_DAY_CODES.add("FMLA Personal time (US only)");

        VACATION_DAY_CODES.add("Annual lv; vacation");
        VACATION_DAY_CODES.add("FMLA Vacation time (US only)");
    }
}