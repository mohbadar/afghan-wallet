package af.gov.anar.lib.xml;

import af.gov.anar.lib.xml.builder.BaseXMLBuilder;
import af.gov.anar.lib.xml.builder.XMLBuilder2;
import af.gov.anar.lib.xml.exception.XMLBuilderRuntimeException;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

public class XMLBuilderTest2 extends BaseXMLBuilderTests {

    @Override
    public Class<? extends BaseXMLBuilder> XMLBuilderToTest() throws Exception {
        return XMLBuilder2.class;
    }

    @Override
    protected boolean isRuntimeExceptionsOnly() {
        return true;
    }

    // NOTE: No checked exceptions for API calls made in this test method
    public void testNoCheckedExceptions() {
        XMLBuilder2 builder = XMLBuilder2.create("Blah");
        builder = XMLBuilder2.parse(EXAMPLE_XML_DOC);
        builder.stripWhitespaceOnlyTextNodes();
        builder.asString();
        builder.elementAsString();
        builder.xpathQuery("/*", XPathConstants.NODESET);
        builder.xpathFind("/Projects");
    }

    public void testExceptionWrappedInXMLBuilderRuntimeException() {
        XMLBuilder2 builder = XMLBuilder2.parse(EXAMPLE_XML_DOC);
        try {
            builder.xpathFind("/BadPath");
            fail("Expected XMLBuilderRuntimeException");
        } catch (XMLBuilderRuntimeException e) {
            assertEquals(XMLBuilderRuntimeException.class, e.getClass());
            Throwable cause = e.getCause();
            assertEquals(XPathExpressionException.class, cause.getClass());
            assertTrue(cause.getMessage().contains("does not resolve to an Element"));
        }
    }

}