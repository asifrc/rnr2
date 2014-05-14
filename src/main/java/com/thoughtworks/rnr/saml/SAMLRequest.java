package com.thoughtworks.rnr.saml;

import com.thoughtworks.rnr.saml.util.Clock;
import com.thoughtworks.rnr.saml.util.Identifier;
import com.thoughtworks.rnr.saml.util.SimpleClock;
import com.thoughtworks.rnr.saml.util.UUIDIdentifer;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.*;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.io.StringWriter;

/**
 * Wrapper for an outgoing SAMLRequest
 */
public class SAMLRequest {

    private final AuthnRequest authnRequest;

    /**
     * Creates a SAML request based on the given Application
     * @param application Application that includes the URL where the SAMLRequest should be sent to and the issuer
     */
    public SAMLRequest(Application application) {
        this(application, new UUIDIdentifer(), new SimpleClock());
    }

    public SAMLRequest(Application application, Identifier identifier, Clock clock) {
        authnRequest = build(AuthnRequest.DEFAULT_ELEMENT_NAME);
        authnRequest.setID(identifier.getId());
        authnRequest.setVersion(SAMLVersion.VERSION_20);
        authnRequest.setIssueInstant(clock.dateTimeNow());
        authnRequest.setProtocolBinding(SAMLConstants.SAML2_POST_BINDING_URI);
        authnRequest.setAssertionConsumerServiceURL(application.getAuthenticationURL());

        Issuer issuer = build(Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue(application.getIssuer());
        authnRequest.setIssuer(issuer);

        NameIDPolicy nameIDPolicy = build(NameIDPolicy.DEFAULT_ELEMENT_NAME);
        nameIDPolicy.setFormat(NameIDType.UNSPECIFIED);
        authnRequest.setNameIDPolicy(nameIDPolicy);

        RequestedAuthnContext requestedAuthnContext = build(RequestedAuthnContext.DEFAULT_ELEMENT_NAME);
        requestedAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.EXACT);
        authnRequest.setRequestedAuthnContext(requestedAuthnContext);

        AuthnContextClassRef authnContextClassRef = build(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        authnContextClassRef.setAuthnContextClassRef(AuthnContext.PPT_AUTHN_CTX);
        requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRef);
    }

    @SuppressWarnings("unchecked")
    private <T extends SAMLObject> T build(QName qName) {
        return (T) org.opensaml.Configuration.getBuilderFactory().getBuilder(qName).buildObject(qName);
    }

    /**
     * @return the created SAML request in a string format
     */
    public String toString() {
        try {
            Marshaller marshaller = org.opensaml.Configuration.getMarshallerFactory().getMarshaller(authnRequest);
            Element dom = marshaller.marshall(authnRequest);
            StringWriter stringWriter = new StringWriter();
            XMLHelper.writeNode(dom, stringWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @return the created SAML request in openSAML AuthnRequest
     */
    public AuthnRequest getAuthnRequest() {
        return authnRequest;
    }
}
