package com.thoughtworks.rnr.saml;

import org.opensaml.ws.security.SecurityPolicyException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;

public class TestsHelper {
    public String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public Configuration loadConfig(String path) throws IOException, CertificateException, XPathExpressionException, SecurityPolicyException {
        InputStream stream = getClass().getResourceAsStream(path);
        String file;

        try {
            file = convertStreamToString(stream);
        } finally {
            stream.close();
        }

        return new Configuration(file);
    }
}
