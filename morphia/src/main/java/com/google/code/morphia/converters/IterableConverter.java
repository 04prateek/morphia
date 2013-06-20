package com.google.code.morphia.converters;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.code.morphia.ObjectFactory;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.utils.ReflectionUtils;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */

@SuppressWarnings({ "unchecked", "rawtypes" })
public class IterableConverter extends TypeConverter {
  private final DefaultConverters chain;

  public IterableConverter(final DefaultConverters chain) {
    this.chain = chain;
  }

  @Override
  protected boolean isSupported(final Class c, final MappedField mf) {
    if (mf != null) {
      return mf.isMultipleValues() && !mf.isMap(); //&& !mf.isTypeMongoCompatible();
    } else {
      return c.isArray() || ReflectionUtils.implementsInterface(c, Iterable.class);
    }
  }

  @Override
  public Object decode(final Class targetClass, final Object fromDBObject, final MappedField mf) throws MappingException {
    if (mf == null || fromDBObject == null) {
      return fromDBObject;
    }

    final Class subtypeDest = mf.getSubClass();
    final Collection values = createNewCollection(mf);

    if (fromDBObject.getClass().isArray()) {
      //This should never happen. The driver always returns list/arrays as a List
      for (final Object o : (Object[]) fromDBObject) {
        values.add(chain.decode((subtypeDest != null) ? subtypeDest : o.getClass(), o));
      }
    } else if (fromDBObject instanceof Iterable) {
      // map back to the java data type
      // (List/Set/Array[])
      for (final Object o : (Iterable) fromDBObject) {
        values.add(chain.decode((subtypeDest != null) ? subtypeDest : o.getClass(), o));
      }
    } else {
      //Single value case.
      values.add(chain.decode((subtypeDest != null) ? subtypeDest : fromDBObject.getClass(), fromDBObject));
    }

    //convert to and array if that is the destination type (not a list/set)
    if (mf.getType().isArray()) {
      return ReflectionUtils.convertToArray(subtypeDest, (ArrayList) values);
    } else {
      return values;
    }
  }

  private Collection<?> createNewCollection(final MappedField mf) {
    final ObjectFactory of = mapper.getOptions().objectFactory;
    return mf.isSet() ? of.createSet(mf) : of.createList(mf);
  }

  @Override
  public Object encode(final Object value, final MappedField mf) {

    if (value == null) {
      return null;
    }

    final Iterable<?> iterableValues;

    if (value.getClass().isArray()) {

      if (Array.getLength(value) == 0) {
        return value;
      }

      if (value.getClass().getComponentType().isPrimitive()) {
        return value;
      }

      iterableValues = Arrays.asList((Object[]) value);
    } else {
      if (!(value instanceof Iterable)) {
        throw new ConverterException("Cannot cast " + value.getClass() + " to Iterable for MappedField: " + mf);
      }

      // cast value to a common interface
      iterableValues = (Iterable<?>) value;
    }

    final List values = new ArrayList();
    if (mf != null && mf.getSubClass() != null) {
      for (final Object o : iterableValues) {
        values.add(chain.encode(mf.getSubClass(), o));
      }
    } else {
      for (final Object o : iterableValues) {
        values.add(chain.encode(o));
      }
    }
    if (!values.isEmpty() || mapper.getOptions().storeEmpties) {
      return values;
    } else {
      return null;
    }
  }
}
