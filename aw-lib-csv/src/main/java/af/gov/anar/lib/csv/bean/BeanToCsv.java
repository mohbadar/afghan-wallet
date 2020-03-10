package af.gov.anar.lib.csv.bean;

import af.gov.anar.lib.csv.writer.CsvWriter;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows to export Java beans content to a new CSV spreadsheet file.
 */
public class BeanToCsv<T> {

  public BeanToCsv() {}

  public boolean write(MappingStrategy<T> mapper, Writer writer,
      List<?> objects) {
    return write(mapper, new CsvWriter(writer), objects);
  }

  public boolean write(MappingStrategy<T> mapper, CsvWriter csv,
      List<?> objects) {
    if (objects == null || objects.isEmpty())
      return false;

    try {
      csv.writeNext(processHeader(mapper));
      List<Method> getters = findGetters(mapper);
      for (Object obj : objects) {
        List<String> line = processObject(getters, obj);
        csv.writeNext(line);
      }
      return true;
    } catch (Exception e) {
      throw new RuntimeException("Error writing CSV !", e);
    }
  }

  protected List<String> processHeader(MappingStrategy<T> mapper)
      throws IntrospectionException {
    List<String> values = new ArrayList<String>();
    int i = 0;
    PropertyDescriptor prop = mapper.findDescriptor(i);
    while (prop != null) {
      values.add(prop.getName());
      i++;
      prop = mapper.findDescriptor(i);
    }
    return values;
  }

  protected List<String> processObject(List<Method> getters, Object bean)
      throws IntrospectionException, IllegalArgumentException,
      IllegalAccessException, InvocationTargetException {
    List<String> values = new ArrayList<String>();
    // retrieve bean values
    for (Method getter : getters) {
      Object value = getter.invoke(bean, (Object[]) null);
      if (value == null) {
        values.add("null");
      } else {
        values.add(value.toString());
      }
    }
    return values;
  }

  /**
   * Build getters list from provided mapper.
   */
  private List<Method> findGetters(MappingStrategy<T> mapper)
      throws IntrospectionException {
    int i = 0;
    PropertyDescriptor prop = mapper.findDescriptor(i);
    // build getters methods list
    List<Method> readers = new ArrayList<Method>();
    while (prop != null) {
      readers.add(prop.getReadMethod());
      i++;
      prop = mapper.findDescriptor(i);
    }
    return readers;
  }
}
