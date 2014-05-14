package com.thoughtworks.rnr.saml;

import com.thoughtworks.rnr.saml.util.IPRange;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class IPRangeTest {

    @Test
    public void testInvalidAddress_NonNumeric() {
        try {
            new IPRange("1.2.3.a", "1.2.3.4");
            fail("Exception expected");
        } catch (NumberFormatException ex) {
            //expected
        } catch (Exception ex) {
            fail("Expected NumberFormatException");
        }
    }

    @Test
    public void testInvalidAddress_RangeTooLow() {
        try {
            new IPRange("1.2.3.-1", "1.2.3.4");
            fail("Exception expected");
        } catch (NumberFormatException ex) {
            //expected
        } catch (Exception ex) {
            fail("Expected NumberFormatException");
        }
    }

    @Test
    public void testInvalidAddress_RangeTooHigh() {
        try {
            new IPRange("1.2.3.266", "1.2.3.4");
            fail("Exception expected");
        } catch (NumberFormatException ex) {
            //expected
        } catch (Exception ex) {
            fail("Expected NumberFormatException");
        }
    }

    @Test
    public void testIPWildcard() {
        IPRange ipRange = new IPRange("1.2.3.*", null);
        assertTrue(ipRange.isAddressInRange("1.2.3.10"));
        assertTrue(ipRange.isAddressInRange("1.2.3.0"));
        assertTrue(ipRange.isAddressInRange("1.2.3.255"));
    }

    @Test
    public void testIPInMatch() {
        IPRange ipRange = new IPRange("1.2.3.4", null);
        assertTrue(ipRange.isAddressInRange("1.2.3.4"));
    }

    @Test
    public void testIPNotMatch() {
        IPRange ipRange = new IPRange("1.2.3.5", null);
        assertFalse(ipRange.isAddressInRange("1.2.3.4"));
    }

    @Test
    public void testIPInRange() {
        IPRange ipRange = new IPRange("1.2.3.4", "5.6.7.8");
        assertTrue(ipRange.isAddressInRange("1.2.3.4"));
        assertTrue(ipRange.isAddressInRange("4.4.4.4"));
        assertTrue(ipRange.isAddressInRange("5.5.5.5"));
        assertTrue(ipRange.isAddressInRange("5.6.7.8"));
        assertTrue(ipRange.isAddressInRange("1.6.7.8"));
    }

    @Test
    public void testIPInRangeFirstWildcard() {
        IPRange ipRange = new IPRange("1.2.3.4", "*.6.7.8");
        assertTrue(ipRange.isAddressInRange("255.4.5.6"));
    }

    @Test
    public void testIPInRangeSecondWildcard() {
        IPRange ipRange = new IPRange("1.2.3.4", "5.*.7.8");
        assertTrue(ipRange.isAddressInRange("5.255.5.6"));
    }

    @Test
    public void testIPInRangeThirdWildcard() {
        IPRange ipRange = new IPRange("1.2.3.4", "5.6.*.8");
        assertTrue(ipRange.isAddressInRange("4.5.255.7"));
    }

    @Test
    public void testIPInRangeFourthWildcard() {
        IPRange ipRange = new IPRange("1.2.3.4", "5.6.7.*");
        assertTrue(ipRange.isAddressInRange("4.5.6.255"));
    }
    
    @Test public void testIPOutOfRange() {
        IPRange ipRange = new IPRange("1.2.3.4", "5.6.7.8");
        assertFalse(ipRange.isAddressInRange("6.0.4.5"));
        assertFalse(ipRange.isAddressInRange("2.7.0.5"));
        assertFalse(ipRange.isAddressInRange("2.3.8.0"));
        assertFalse(ipRange.isAddressInRange("22.3.4.9"));
    }
}
