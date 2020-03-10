package af.gov.anar.lib.csv.resultset;


import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * helper class for processing JDBC ResultSet objects
 */
public class ResultSetHelperService implements ResultSetHelper {
  public static final int CLOBBUFFERSIZE = 2048;

  // note: we want to maintain compatibility with Java 5 VM's
  // These types don't exist in Java 5
  public static final int NVARCHAR = -9;
  public static final int NCHAR = -15;
  public static final int LONGNVARCHAR = -16;
  public static final int NCLOB = 2011;

  public static final String DEFAULT_DATE_FORMAT = "dd-MMM-yyyy";
  public static final String DEFAULT_TIMESTAMP_FORMAT = "dd-MMM-yyyy HH:mm:ss";

  public List<String> getColumnNames(ResultSet rs) throws SQLException {
    List<String> names = new ArrayList<String>();
    ResultSetMetaData metadata = rs.getMetaData();

    for (int i = 0; i < metadata.getColumnCount(); i++) {
      names.add(metadata.getColumnName(i + 1));
    }
    return names;
  }

  public List<String> getColumnValues(ResultSet rs) throws SQLException, IOException {
    return this.getColumnValues(rs, false, DEFAULT_DATE_FORMAT, DEFAULT_TIMESTAMP_FORMAT);
  }

  public List<String> getColumnValues(ResultSet rs, boolean trim) throws SQLException, IOException {
    return this.getColumnValues(rs, trim, DEFAULT_DATE_FORMAT, DEFAULT_TIMESTAMP_FORMAT);
  }

  public List<String> getColumnValues(ResultSet rs, boolean trim, String dateFormatString, String timeFormatString) throws SQLException, IOException {
    List<String> values = new ArrayList<String>();
    ResultSetMetaData metadata = rs.getMetaData();

    for (int i = 0; i < metadata.getColumnCount(); i++) {
      values.add(getColumnValue(rs, metadata.getColumnType(i + 1), i + 1, trim, dateFormatString, timeFormatString));
    }
    return values;
  }

  private String handleObject(Object obj) {
    return obj == null ? "" : String.valueOf(obj);
  }

  private String handleBigDecimal(BigDecimal decimal) {
    return decimal == null ? "" : decimal.toString();
  }

  private String handleLong(ResultSet rs, int columnIndex) throws SQLException {
    long lv = rs.getLong(columnIndex);
    return rs.wasNull() ? "" : Long.toString(lv);
  }
  
  private String handleLongOrInt(ResultSet rs, int colIdx) throws SQLException {
    long lv = rs.getLong(colIdx);
    if (rs.wasNull()) return "";
    
    if (lv <= Integer.MAX_VALUE && lv >= Integer.MIN_VALUE) {
      int iv = rs.getInt(colIdx);
      return Integer.toString(iv);
    } else {
      return Long.toString(lv);
    }
  }

  private String handleInteger(ResultSet rs, int columnIndex) throws SQLException {
    int i = rs.getInt(columnIndex);
    return rs.wasNull() ? "" : Integer.toString(i);
  }

  private String handleDate(ResultSet rs, int columnIndex, String dateFormatString) throws SQLException {
    Date date = rs.getDate(columnIndex);
    String value = null;
    if (date != null) {
      SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
      value = dateFormat.format(date);
    }
    return value;
  }

  private String handleTime(Time time) {
    return time == null ? null : time.toString();
  }

  private String handleTimestamp(Timestamp timestamp, String timestampFormatString) {
    SimpleDateFormat timeFormat = new SimpleDateFormat(timestampFormatString);
    return timestamp == null ? null : timeFormat.format(timestamp);
  }

  private String getColumnValue(ResultSet rs, int colType, int colIndex, boolean trim, String dateFormatString, String timestampFormatString)
      throws SQLException, IOException {

    String value = "";

    switch (colType) {
      case Types.BIT:
      case Types.JAVA_OBJECT:
        value = handleObject(rs.getObject(colIndex));
        break;
      case Types.BOOLEAN:
        boolean b = rs.getBoolean(colIndex);
        value = Boolean.valueOf(b).toString();
        break;
      case NCLOB: // todo : use rs.getNClob
      case Types.CLOB:
        Clob c = rs.getClob(colIndex);
        if (c != null) {
          value = read(c);
        }
        break;
      case Types.BIGINT:
        value = handleLong(rs, colIndex);
        break;
      case Types.DECIMAL:
      case Types.DOUBLE:
      case Types.FLOAT:
      case Types.REAL:
      case Types.NUMERIC:
        value = handleBigDecimal(rs.getBigDecimal(colIndex));
        break;
      case Types.INTEGER:
        // Some DBs, like MySQL, can have unsigned ints, which will cause an overflow with this code
        // so first grab as a Long and if within the INT size limits, change it to an Int
        value = handleLongOrInt(rs, colIndex);
        break;
      case Types.TINYINT:
      case Types.SMALLINT:
        value = handleInteger(rs, colIndex);
        break;
      case Types.DATE:
        value = handleDate(rs, colIndex, dateFormatString);
        break;
      case Types.TIME:
        value = handleTime(rs.getTime(colIndex));
        break;
      case Types.TIMESTAMP:
        value = handleTimestamp(rs.getTimestamp(colIndex), timestampFormatString);
        break;
      case NVARCHAR: // todo : use rs.getNString
      case NCHAR: // todo : use rs.getNString
      case LONGNVARCHAR: // todo : use rs.getNString
      case Types.LONGVARCHAR:
      case Types.VARCHAR:
      case Types.CHAR:
        String columnValue = rs.getString(colIndex);
        if (trim && columnValue != null) {
          value = columnValue.trim();
        } else {
          value = columnValue;
        }
        break;
      default:
        value = "";
    }


    if (value == null) {
      value = "";
    }

    return value;

  }

  private static String read(Clob c) throws SQLException, IOException {
    StringBuilder sb = new StringBuilder((int) c.length());
    Reader r = c.getCharacterStream();
    char[] cbuf = new char[CLOBBUFFERSIZE];
    int n;
    while ((n = r.read(cbuf, 0, cbuf.length)) != -1) {
      sb.append(cbuf, 0, n);
    }
    return sb.toString();
  }

}
