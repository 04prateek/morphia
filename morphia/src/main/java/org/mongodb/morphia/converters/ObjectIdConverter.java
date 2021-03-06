package org.mongodb.morphia.converters;


import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.MappingException;


/**
 * Convert to an ObjectId from string
 *
 * @author scotthernandez
 */
@SuppressWarnings({"rawtypes" })
public class ObjectIdConverter extends TypeConverter implements SimpleValueConverter {

  public ObjectIdConverter() {
    super(ObjectId.class);
  }

  @Override
  public Object decode(final Class targetClass, final Object val, final MappedField optionalExtraInfo) throws MappingException {
    if (val == null) {
      return null;
    }

    if (val instanceof ObjectId) {
      return val;
    }

    return new ObjectId(val.toString()); // good luck
  }
}
