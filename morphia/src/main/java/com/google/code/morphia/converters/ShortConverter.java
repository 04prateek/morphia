package com.google.code.morphia.converters;


import java.lang.reflect.Array;
import java.util.List;

import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
@SuppressWarnings({"rawtypes"})
public class ShortConverter extends TypeConverter implements SimpleValueConverter {
  public ShortConverter() {
    super(short.class, Short.class, short[].class, Short[].class);
  }

  @Override
  public Object decode(final Class targetClass, final Object val, final MappedField optionalExtraInfo) throws MappingException {
    if (val == null) {
      return null;
    }

    if (val instanceof Short) {
      return val;
    }

    if (val instanceof Number) {
      return ((Number) val).shortValue();
    }

    if (val instanceof List) {
      final Class<?> type = targetClass.isArray() ? targetClass.getComponentType() : targetClass;
      return convertToArray(type, (List<?>) val);
    }

    return Short.parseShort(val.toString());
  }

  public static Object convertToArray(final Class type, final List<?> values) {
    final Object array = Array.newInstance(type, values.size());
    for (int i = 0; i < values.size(); i++) {
      Array.set(array, i, ((Number)values.get(i)).shortValue());
    }
    return array;
  }

}
