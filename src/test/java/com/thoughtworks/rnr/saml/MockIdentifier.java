package com.thoughtworks.rnr.saml;

import com.thoughtworks.rnr.saml.util.Identifier;

/**
 * Implementation for Identifier used for testing
 */
public class MockIdentifier implements Identifier {

    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
