package com.thoughtworks.rnr.service;

import com.thoughtworks.rnr.saml.Configuration;
import com.thoughtworks.rnr.saml.SAMLResponse;
import com.thoughtworks.rnr.saml.SAMLValidator;
import org.apache.commons.codec.binary.Base64;
import org.opensaml.DefaultBootstrap;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.validation.ValidationException;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.Principal;
import java.security.cert.CertificateException;

public class SAMLService2 {


    private final UnmarshallerFactory unmarshallerFactory;
    private final String loggedInKey = "okta.rnr.user";
    private final String loggedOutKey = "okta.rnr.logged_out_user";
    private SAMLValidator validator;
    private Configuration configuration;

    public SAMLService2() throws IOException {
        try {
            DefaultBootstrap.bootstrap();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        try {
            String file = readFile("src/test/resources/config.xml");
            validator = new SAMLValidator();
            configuration = validator.getConfiguration(file);
        } catch (SecurityPolicyException e) {
            e.printStackTrace();
        }


        unmarshallerFactory = org.opensaml.xml.Configuration.getUnmarshallerFactory();
    }

    public Principal verifyOKTASignOn(String responseString) throws UnmarshallingException, IOException, CertificateException, ValidationException, SAXException, ParserConfigurationException, SecurityPolicyException {
        SAMLResponse samlResponse = getSAMLResponse(responseString);
        Principal user = getUserPrincipal(samlResponse);
        return user;
    }

    public String readFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.forName("UTF-8").decode(bb).toString();
        }
        finally {
            stream.close();
        }
    }
    public Principal getUserPrincipal(final SAMLResponse response) {
        return new Principal() {
            public String getName() {
                return response.getUserID();
            }
        };
    }
    public void putPrincipalInSessionContext(HttpServletRequest request, Principal principal) {
        final HttpSession httpSession = request.getSession();
        httpSession.setAttribute(loggedInKey, principal);
        httpSession.setAttribute(loggedOutKey, null);
    }

    public SAMLResponse getSAMLResponse(String assertion) throws UnsupportedEncodingException, SecurityPolicyException {
        assertion = new String(Base64.decodeBase64(assertion.getBytes("UTF-8")), Charset.forName("UTF-8"));
        return validator.getSAMLResponse(assertion, configuration);
    }
}
