package af.gov.anar.lib.csv.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public interface CsvParser {
  List<String> parse(String s);
  List<String> parseNext(Reader reader) throws IOException;
}
