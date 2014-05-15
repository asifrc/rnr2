package com.thoughtworks.rnr.service;

import com.thoughtworks.rnr.saml.Certificate;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Response;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.validation.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import sun.security.x509.X509CertImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;

public class SAMLService2 {


    private final UnmarshallerFactory unmarshallerFactory;

    public SAMLService2() {
        try {
            DefaultBootstrap.bootstrap();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        unmarshallerFactory = org.opensaml.xml.Configuration.getUnmarshallerFactory();
    }

    public void verifyOKTASignOn(String responseString) throws UnmarshallingException, IOException, CertificateException, ValidationException, SAXException, ParserConfigurationException {
        Response response = getResponse(responseString);
//        validateResponse(response);

    }

    private void validateResponse(Response response) throws ValidationException, FileNotFoundException, CertificateException, CertificateException, ValidationException {
        Assertion assertion = response.getAssertions().get(0);

        String subject = assertion.getSubject().getNameID().getValue();

        String issuer = assertion.getIssuer().getValue();

        String audience = assertion.getConditions().getAudienceRestrictions().get(0).getAudiences().get(0).getAudienceURI();

        String statusCode = response.getStatus().getStatusCode().getValue();

        Signature sig = response.getSignature();
        X509CertImpl x509Cert = new X509CertImpl(new FileInputStream("/certificate.txt"));
        Certificate certificate = new Certificate(x509Cert);
        SignatureValidator validator = new SignatureValidator(certificate.getCredential());
        validator.validate(sig);
    }

    private Response getResponse(String responseString) throws UnmarshallingException, ParserConfigurationException, IOException, SAXException {
        byte[] base64DecodedResponse = Base64.decode(responseString);
        ByteArrayInputStream is = new ByteArrayInputStream(base64DecodedResponse);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();

        Document document = docBuilder.parse(is);
        Element element = document.getDocumentElement();


        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
        XMLObject responseXmlObj = unmarshaller.unmarshall(element);
        Response response = (Response) responseXmlObj;

        return response;
    }

}
