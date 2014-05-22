package com.thoughtworks.rnr.service;

import com.thoughtworks.rnr.saml.Configuration;
import com.thoughtworks.rnr.saml.SAMLResponse;
import com.thoughtworks.rnr.saml.SAMLValidator;
import com.thoughtworks.rnr.saml.util.Clock;
import org.apache.commons.codec.binary.Base64;
import org.opensaml.DefaultBootstrap;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

@Component
public class SAMLService {
    private final String LOGGED_IN_KEY = "okta.rnr.user";
    private final String LOGGED_OUT_KEY = "okta.rnr.logged_out_user";
    private final String CONFIG_FILE_PATH = "src/main/java/com/thoughtworks/rnr/config.xml";

    private SAMLValidator validator;
    private String file;
    private Configuration configuration;

    // Autowired bean in dispatcher servlet, used for testing purposes.
    // (Switches whether a real clock or a fake clock is used)
    @Autowired
    private Clock timeProvider;

    public SAMLService() throws IOException {
        bootstrapOpenSAMLLibrary();
        setUpValidatorAndConfiguration();
    }

    public void setSessionWhenSAMLResponseIsValid(HttpServletRequest request, String samlResponse) throws IOException, SecurityPolicyException, CertificateException, ParserConfigurationException, ValidationException, SAXException, UnmarshallingException {
        SAMLResponse decodedResponse = decodeAndUnmarshall(samlResponse);
        Principal user = getUserFromSAMLResponse(decodedResponse);
        putUserInSession(request, user);
    }

    private void bootstrapOpenSAMLLibrary() {
        try {
            DefaultBootstrap.bootstrap();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void setUpValidatorAndConfiguration() throws IOException {
        file = readFile(CONFIG_FILE_PATH);
        try {
            validator = new SAMLValidator(timeProvider);
            configuration = validator.getConfiguration(file);
        } catch (SecurityPolicyException e) {
            e.printStackTrace();
        }
    }

    private String readFile(String path) throws IOException {
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

    public SAMLResponse decodeAndUnmarshall(String assertion) throws UnsupportedEncodingException, SecurityPolicyException {
        assertion = new String(Base64.decodeBase64(assertion.getBytes("UTF-8")), Charset.forName("UTF-8"));
        return validator.getSAMLResponse(assertion, configuration, timeProvider);
    }

    public String getUserIdFromSAMLString(String samlResponse) throws IOException, SecurityPolicyException, CertificateException, ParserConfigurationException, ValidationException, SAXException, UnmarshallingException {
        return decodeAndUnmarshall(samlResponse).getUserID();
    }

    private Principal getUserFromSAMLResponse(final SAMLResponse samlResponse) throws UnmarshallingException, IOException, CertificateException, ValidationException, SAXException, ParserConfigurationException, SecurityPolicyException {
        Principal user =  new Principal() {
            public String getName() {
                return samlResponse.getUserID();
            }
        };
        return user;
    }

    private void putUserInSession(HttpServletRequest request, Principal user) {
        final HttpSession httpSession = request.getSession();
        httpSession.setAttribute(LOGGED_IN_KEY, user);
        httpSession.setAttribute(LOGGED_OUT_KEY, null);
    }
}
