package af.gov.anar.lib.csv.test;

import af.gov.anar.lib.csv.parser.CsvParser;
import af.gov.anar.lib.csv.parser.CsvParserBuilder;
import af.gov.anar.lib.csv.parser.MultiLineCsvParser;
import af.gov.anar.lib.csv.parser.SimpleCsvParser;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CsvParserBuilderTest {

  @Test
  public void testCreateStandardSimpleParser() {
    CsvParser p = new CsvParserBuilder().build();
    assertTrue(p instanceof SimpleCsvParser);

    p = new CsvParserBuilder().supportRfc4180QuotedQuotes(false).build();
    assertTrue(p instanceof SimpleCsvParser);
    
    p = new CsvParserBuilder().threadSafe(false).build();
    assertTrue(p instanceof SimpleCsvParser);

    p = new CsvParserBuilder().trimWhitespace(true).build();
    assertTrue(p instanceof SimpleCsvParser);

    p = new CsvParserBuilder().
        retainEscapeChars(true).
        strictQuotes(true).
        alwaysQuoteOutput(true).
        trimWhitespace(true).
        build();
    assertTrue(p instanceof SimpleCsvParser);
    
    p = new CsvParserBuilder().
        threadSafe(false).
        supportRfc4180QuotedQuotes(false).
        multiLine(false).
        retainEscapeChars(true).
        strictQuotes(true).
        alwaysQuoteOutput(true).
        trimWhitespace(true).
        build();
    assertTrue(p instanceof SimpleCsvParser);
  }
  
  @Test
  public void testCreateMultiLineParser() {
    CsvParser p = new CsvParserBuilder().supportRfc4180QuotedQuotes(true).build();
    assertTrue(p instanceof MultiLineCsvParser);
    
    p = new CsvParserBuilder().threadSafe(true).build();
    assertTrue(p instanceof MultiLineCsvParser);
    
    p = new CsvParserBuilder().multiLine(true).build();
    assertTrue(p instanceof MultiLineCsvParser);

    p = new CsvParserBuilder().threadSafe(true).supportRfc4180QuotedQuotes(true).build();
    assertTrue(p instanceof MultiLineCsvParser);

    p = new CsvParserBuilder().threadSafe(false).supportRfc4180QuotedQuotes(true).build();
    assertTrue(p instanceof MultiLineCsvParser);

    p = new CsvParserBuilder().threadSafe(true).supportRfc4180QuotedQuotes(false).build();
    assertTrue(p instanceof MultiLineCsvParser);
    
    p = new CsvParserBuilder().
        threadSafe(false).
        supportRfc4180QuotedQuotes(true).
        retainEscapeChars(true).
        strictQuotes(true).
        alwaysQuoteOutput(true).
        trimWhitespace(true).
        build();
    assertTrue(p instanceof MultiLineCsvParser);
    
    p = new CsvParserBuilder().
        threadSafe(false).
        supportRfc4180QuotedQuotes(false).
        multiLine(true).
        retainEscapeChars(true).
        strictQuotes(true).
        alwaysQuoteOutput(true).
        trimWhitespace(true).
        build();
    assertTrue(p instanceof MultiLineCsvParser);
  }

  @Test(expected=IllegalStateException.class)
  public void testRequestThreadSafeAndSettingMultiLineSetToFalseThrowsException() {
    new CsvParserBuilder().multiLine(false).threadSafe(true).build();
  }
  
  @Test(expected=IllegalStateException.class)
  public void testRequestAllowDoubleEscapedQuotestAndSettingMultiLineSetToFalseThrowsException() {
    new CsvParserBuilder().multiLine(false).supportRfc4180QuotedQuotes(true).build();
  }
}
