package com.thoughtworks.rnr.saml;

import com.thoughtworks.rnr.saml.util.SimpleClock;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Response;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.Base64;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Test to verify the integrity of SAMLValidator
 */
public class SAMLValidatorTest {

    private static final String ISSUER = "http://www.okta.com/k21tpw64VPAMDOMKRXBS";
    private static final String AUTHENTICATION_URL = "https://thoughtworks.oktapreview.com/app/template_saml_2_0/k21tpw64VPAMDOMKRXBS/sso/saml";

    private SAMLValidator validator;
    private Configuration configuration;
    private Application application;
    private TestsHelper helper;

    @BeforeTest
    public void setup() throws SecurityPolicyException, IOException {
        helper = new TestsHelper();
        validator = new SAMLValidator(new SimpleClock());
        configuration = validator.getConfigurationFrom("src/test/resources/config.xml");
        application = configuration.getDefaultApplication();
    }

    @Test
    public void testGetConfigurationFrom() {
        assertEquals(configuration.getApplications().size(), 1);

        Application application = configuration.getApplication(ISSUER);
        assertEquals(application.getIssuer(), ISSUER);
        assertEquals(application.getAuthenticationURL(), AUTHENTICATION_URL);

    }

    @Test
    public void testGetSAMLRequest() {
        SAMLRequest request = validator.getSAMLRequest(application);
        AuthnRequest authnRequest = request.getAuthnRequest();
        assertEquals(authnRequest.getAssertionConsumerServiceURL(), application.getAuthenticationURL());
        assertEquals(authnRequest.getIssuer().getValue(), application.getIssuer());
    }

    @Test
    public void testGetSAMLResponse() throws IOException {
        String responseStr = null;
        InputStream stream = getClass().getResourceAsStream("/samlResponse.xml");
        try {
            responseStr = new String(DatatypeConverter.parseBase64Binary(helper.convertStreamToString(stream)), "UTF-8");
            PrintWriter writer = new PrintWriter("testOutPut.txt", "UTF-8");
            writer.println(responseStr);
            writer.close();

        } finally {
            stream.close();
        }

        try {
            SAMLResponse response = validator.getSAMLResponse(responseStr, configuration, new SimpleClock());
        } catch (SecurityPolicyException e) {
            assertEquals(e.getMessage(), "Conditions have expired");
        } catch (Exception e) {
            fail();
        }
    }
    @Test
    public void testTesting() throws IOException, SAXException, UnmarshallingException, ParserConfigurationException {
        String responseString = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48c2FtbDJwOlJlc3BvbnNlIHhtbG5zOnNhbWwycD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOnByb3RvY29sIiBEZXN0aW5hdGlvbj0iaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvc2FtbC9jYWxsYmFjayIgSUQ9ImlkMjI1NDE4OTI2MjkwNTMxOTUzOTIxNzg0MDkiIElzc3VlSW5zdGFudD0iMjAxNC0wNS0xNFQyMDo1NzozNy43NDNaIiBWZXJzaW9uPSIyLjAiPjxzYW1sMjpJc3N1ZXIgeG1sbnM6c2FtbDI9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphc3NlcnRpb24iIEZvcm1hdD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOm5hbWVpZC1mb3JtYXQ6ZW50aXR5Ij5odHRwOi8vd3d3Lm9rdGEuY29tL2syMXRwdzY0VlBBTURPTUtSWEJTPC9zYW1sMjpJc3N1ZXI+PGRzOlNpZ25hdHVyZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI+PGRzOlNpZ25lZEluZm8+PGRzOkNhbm9uaWNhbGl6YXRpb25NZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiLz48ZHM6U2lnbmF0dXJlTWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3JzYS1zaGExIi8+PGRzOlJlZmVyZW5jZSBVUkk9IiNpZDIyNTQxODkyNjI5MDUzMTk1MzkyMTc4NDA5Ij48ZHM6VHJhbnNmb3Jtcz48ZHM6VHJhbnNmb3JtIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI2VudmVsb3BlZC1zaWduYXR1cmUiLz48ZHM6VHJhbnNmb3JtIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8xMC94bWwtZXhjLWMxNG4jIi8+PC9kczpUcmFuc2Zvcm1zPjxkczpEaWdlc3RNZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjc2hhMSIvPjxkczpEaWdlc3RWYWx1ZT5OeVVId1pvQWlydGxKQ3E1WWRxaXpnc1ZPek09PC9kczpEaWdlc3RWYWx1ZT48L2RzOlJlZmVyZW5jZT48L2RzOlNpZ25lZEluZm8+PGRzOlNpZ25hdHVyZVZhbHVlPlBsQVRrWUlIcGZZTm1JY1hQV0gwcU1Da0tTRmViWWdnZ2wwcmYwdzJHQ0I3MXBFQitIQXhmSTdSY0pVcThYc21heDhRL0JCMGdSSUsrcVNCSnE0WEdlbEtTYVhvM01nKzREUUFhTHBjVkF3amRBeTVJRHQveGxEZlpjSFc5YzMwWnVqbnFQeWg1TUNmUVdmbTQrQndodmFmR1ZUV2tRVHFpUW9sSU9ucHFIMD08L2RzOlNpZ25hdHVyZVZhbHVlPjxkczpLZXlJbmZvPjxkczpYNTA5RGF0YT48ZHM6WDUwOUNlcnRpZmljYXRlPk1JSUNvekNDQWd5Z0F3SUJBZ0lHQVQrZmF1SU9NQTBHQ1NxR1NJYjNEUUVCQlFVQU1JR1VNUXN3Q1FZRFZRUUdFd0pWVXpFVE1CRUcKQTFVRUNBd0tRMkZzYVdadmNtNXBZVEVXTUJRR0ExVUVCd3dOVTJGdUlFWnlZVzVqYVhOamJ6RU5NQXNHQTFVRUNnd0VUMnQwWVRFVQpNQklHQTFVRUN3d0xVMU5QVUhKdmRtbGtaWEl4RlRBVEJnTlZCQU1NREhSb2IzVm5hSFIzYjNKcmN6RWNNQm9HQ1NxR1NJYjNEUUVKCkFSWU5hVzVtYjBCdmEzUmhMbU52YlRBZUZ3MHhNekEzTURJeE1qUTBORGxhRncwME16QTNNREl4TWpRMU5EbGFNSUdVTVFzd0NRWUQKVlFRR0V3SlZVekVUTUJFR0ExVUVDQXdLUTJGc2FXWnZjbTVwWVRFV01CUUdBMVVFQnd3TlUyRnVJRVp5WVc1amFYTmpiekVOTUFzRwpBMVVFQ2d3RVQydDBZVEVVTUJJR0ExVUVDd3dMVTFOUFVISnZkbWxrWlhJeEZUQVRCZ05WQkFNTURIUm9iM1ZuYUhSM2IzSnJjekVjCk1Cb0dDU3FHU0liM0RRRUpBUllOYVc1bWIwQnZhM1JoTG1OdmJUQ0JuekFOQmdrcWhraUc5dzBCQVFFRkFBT0JqUUF3Z1lrQ2dZRUEKa0FZcW1DUzYzREJzYXYvemhDTU5XMzhKUXhXNGhOZ08xNURDbzJSdm5rRU4xakRLK3BuQWxSdTZwR0tKbXB2VlZ4M0swemdTeE5CTQpYRnZXQ1BmV2RrMVJhUm9vK1AvNHBjTUJBcnkveXNiVkFKM3IxdHBVdVA5bk10NHp1R2t3TCtUcG5GVUtWc1M2OTBmd0lEK21SeWR4CkFiMWhUYTNFY0c1Z1hkdTdwRDBDQXdFQUFUQU5CZ2txaGtpRzl3MEJBUVVGQUFPQmdRQVhXTyt3eE02V1NaNk1UWnZkaDJnMXdGMGQKR3ZaaFM1TE8zcTJQVXZxNHFIeDFTY2hpS2J4amUrQ1VIQ3FPT09ET2pRZUQrU1ZjVUJVUEo4STlPV2k5YURpUWpLbm1wcjg3aDhQSAorTmkxeUIyQzJLUkhkeHhTUjZTZlJqa3lOZVZFd3pUeWgyWTJ6dStoZ2hkZEt2bGxXUW9TZndYaEljU3JMS3RzTDcxTnJRPT08L2RzOlg1MDlDZXJ0aWZpY2F0ZT48L2RzOlg1MDlEYXRhPjwvZHM6S2V5SW5mbz48L2RzOlNpZ25hdHVyZT48c2FtbDJwOlN0YXR1cyB4bWxuczpzYW1sMnA9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpwcm90b2NvbCI+PHNhbWwycDpTdGF0dXNDb2RlIFZhbHVlPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6c3RhdHVzOlN1Y2Nlc3MiLz48L3NhbWwycDpTdGF0dXM+PHNhbWwyOkFzc2VydGlvbiB4bWxuczpzYW1sMj0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmFzc2VydGlvbiIgSUQ9ImlkMjI1NDE4OTI2MjkxNjMxMzU5NjI1ODcwOTYiIElzc3VlSW5zdGFudD0iMjAxNC0wNS0xNFQyMDo1NzozNy43NDNaIiBWZXJzaW9uPSIyLjAiPjxzYW1sMjpJc3N1ZXIgRm9ybWF0PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6bmFtZWlkLWZvcm1hdDplbnRpdHkiIHhtbG5zOnNhbWwyPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YXNzZXJ0aW9uIj5odHRwOi8vd3d3Lm9rdGEuY29tL2syMXRwdzY0VlBBTURPTUtSWEJTPC9zYW1sMjpJc3N1ZXI+PGRzOlNpZ25hdHVyZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI+PGRzOlNpZ25lZEluZm8+PGRzOkNhbm9uaWNhbGl6YXRpb25NZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiLz48ZHM6U2lnbmF0dXJlTWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3JzYS1zaGExIi8+PGRzOlJlZmVyZW5jZSBVUkk9IiNpZDIyNTQxODkyNjI5MTYzMTM1OTYyNTg3MDk2Ij48ZHM6VHJhbnNmb3Jtcz48ZHM6VHJhbnNmb3JtIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI2VudmVsb3BlZC1zaWduYXR1cmUiLz48ZHM6VHJhbnNmb3JtIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8xMC94bWwtZXhjLWMxNG4jIi8+PC9kczpUcmFuc2Zvcm1zPjxkczpEaWdlc3RNZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjc2hhMSIvPjxkczpEaWdlc3RWYWx1ZT5VU1ZhbEJJbDFSeFdDMW8ydnBMV29rdWlpVEk9PC9kczpEaWdlc3RWYWx1ZT48L2RzOlJlZmVyZW5jZT48L2RzOlNpZ25lZEluZm8+PGRzOlNpZ25hdHVyZVZhbHVlPmI3YVNHT3UrOU5CSUZpcTN4VG5odHliYnhTVGtoNDJnZWIvZ2VjSUs5RkI1UnBPeWo4TVhrSyt5UWEzNnVPZWpycGp6Z1oxNVZmMUtqUyszWk00SGd1aFFBODVTd3U2TTdBR0ZHRnRuM3FZWDRjMmlqM29JOC9Bd1U2RWNtbVByZFFqREhzYk1WajJkbjM0SmRaamJ3dGZESkUzbmJDc084Y3Z0cllBaHRNUT08L2RzOlNpZ25hdHVyZVZhbHVlPjxkczpLZXlJbmZvPjxkczpYNTA5RGF0YT48ZHM6WDUwOUNlcnRpZmljYXRlPk1JSUNvekNDQWd5Z0F3SUJBZ0lHQVQrZmF1SU9NQTBHQ1NxR1NJYjNEUUVCQlFVQU1JR1VNUXN3Q1FZRFZRUUdFd0pWVXpFVE1CRUcKQTFVRUNBd0tRMkZzYVdadmNtNXBZVEVXTUJRR0ExVUVCd3dOVTJGdUlFWnlZVzVqYVhOamJ6RU5NQXNHQTFVRUNnd0VUMnQwWVRFVQpNQklHQTFVRUN3d0xVMU5QVUhKdmRtbGtaWEl4RlRBVEJnTlZCQU1NREhSb2IzVm5hSFIzYjNKcmN6RWNNQm9HQ1NxR1NJYjNEUUVKCkFSWU5hVzVtYjBCdmEzUmhMbU52YlRBZUZ3MHhNekEzTURJeE1qUTBORGxhRncwME16QTNNREl4TWpRMU5EbGFNSUdVTVFzd0NRWUQKVlFRR0V3SlZVekVUTUJFR0ExVUVDQXdLUTJGc2FXWnZjbTVwWVRFV01CUUdBMVVFQnd3TlUyRnVJRVp5WVc1amFYTmpiekVOTUFzRwpBMVVFQ2d3RVQydDBZVEVVTUJJR0ExVUVDd3dMVTFOUFVISnZkbWxrWlhJeEZUQVRCZ05WQkFNTURIUm9iM1ZuYUhSM2IzSnJjekVjCk1Cb0dDU3FHU0liM0RRRUpBUllOYVc1bWIwQnZhM1JoTG1OdmJUQ0JuekFOQmdrcWhraUc5dzBCQVFFRkFBT0JqUUF3Z1lrQ2dZRUEKa0FZcW1DUzYzREJzYXYvemhDTU5XMzhKUXhXNGhOZ08xNURDbzJSdm5rRU4xakRLK3BuQWxSdTZwR0tKbXB2VlZ4M0swemdTeE5CTQpYRnZXQ1BmV2RrMVJhUm9vK1AvNHBjTUJBcnkveXNiVkFKM3IxdHBVdVA5bk10NHp1R2t3TCtUcG5GVUtWc1M2OTBmd0lEK21SeWR4CkFiMWhUYTNFY0c1Z1hkdTdwRDBDQXdFQUFUQU5CZ2txaGtpRzl3MEJBUVVGQUFPQmdRQVhXTyt3eE02V1NaNk1UWnZkaDJnMXdGMGQKR3ZaaFM1TE8zcTJQVXZxNHFIeDFTY2hpS2J4amUrQ1VIQ3FPT09ET2pRZUQrU1ZjVUJVUEo4STlPV2k5YURpUWpLbm1wcjg3aDhQSAorTmkxeUIyQzJLUkhkeHhTUjZTZlJqa3lOZVZFd3pUeWgyWTJ6dStoZ2hkZEt2bGxXUW9TZndYaEljU3JMS3RzTDcxTnJRPT08L2RzOlg1MDlDZXJ0aWZpY2F0ZT48L2RzOlg1MDlEYXRhPjwvZHM6S2V5SW5mbz48L2RzOlNpZ25hdHVyZT48c2FtbDI6U3ViamVjdCB4bWxuczpzYW1sMj0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmFzc2VydGlvbiI+PHNhbWwyOk5hbWVJRCBGb3JtYXQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjEuMTpuYW1laWQtZm9ybWF0OmVtYWlsQWRkcmVzcyI+Y3NjaG9maUB0aG91Z2h0d29ya3MuY29tPC9zYW1sMjpOYW1lSUQ+PHNhbWwyOlN1YmplY3RDb25maXJtYXRpb24gTWV0aG9kPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6Y206YmVhcmVyIj48c2FtbDI6U3ViamVjdENvbmZpcm1hdGlvbkRhdGEgTm90T25PckFmdGVyPSIyMDE0LTA1LTE0VDIxOjAyOjM3Ljc0M1oiIFJlY2lwaWVudD0iaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvc2FtbC9jYWxsYmFjayIvPjwvc2FtbDI6U3ViamVjdENvbmZpcm1hdGlvbj48L3NhbWwyOlN1YmplY3Q+PHNhbWwyOkNvbmRpdGlvbnMgTm90QmVmb3JlPSIyMDE0LTA1LTE0VDIwOjUyOjM3Ljc0M1oiIE5vdE9uT3JBZnRlcj0iMjAxNC0wNS0xNFQyMTowMjozNy43NDNaIiB4bWxuczpzYW1sMj0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmFzc2VydGlvbiI+PHNhbWwyOkF1ZGllbmNlUmVzdHJpY3Rpb24+PHNhbWwyOkF1ZGllbmNlPmh0dHA6Ly9sb2NhbGhvc3Q6ODA4MDwvc2FtbDI6QXVkaWVuY2U+PC9zYW1sMjpBdWRpZW5jZVJlc3RyaWN0aW9uPjwvc2FtbDI6Q29uZGl0aW9ucz48c2FtbDI6QXV0aG5TdGF0ZW1lbnQgQXV0aG5JbnN0YW50PSIyMDE0LTA1LTE0VDIwOjU3OjM3Ljc0M1oiIFNlc3Npb25JbmRleD0iaWQxNDAwMTAxMDU3NzQzLjEyNDYwODUwOTQiIHhtbG5zOnNhbWwyPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YXNzZXJ0aW9uIj48c2FtbDI6QXV0aG5Db250ZXh0PjxzYW1sMjpBdXRobkNvbnRleHRDbGFzc1JlZj51cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YWM6Y2xhc3NlczpQYXNzd29yZFByb3RlY3RlZFRyYW5zcG9ydDwvc2FtbDI6QXV0aG5Db250ZXh0Q2xhc3NSZWY+PC9zYW1sMjpBdXRobkNvbnRleHQ+PC9zYW1sMjpBdXRoblN0YXRlbWVudD48L3NhbWwyOkFzc2VydGlvbj48L3NhbWwycDpSZXNwb25zZT4=";
        byte[] base64DecodedResponse = Base64.decode(responseString);
        ByteArrayInputStream is = new ByteArrayInputStream(base64DecodedResponse);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();

        Document document = docBuilder.parse(is);
        Element element = document.getDocumentElement();


        UnmarshallerFactory unmarshallerFactory = org.opensaml.Configuration.getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
        XMLObject responseXmlObj = unmarshaller.unmarshall(element);
        Response response = (Response) responseXmlObj;

            PrintWriter writer = new PrintWriter("testOutPut.txt", "UTF-8");
            writer.println(response);
            writer.close();

        }



}
