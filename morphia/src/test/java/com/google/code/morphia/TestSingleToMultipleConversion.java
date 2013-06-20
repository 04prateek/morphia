package com.google.code.morphia;


import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.junit.Test;
import com.google.code.morphia.annotations.AlsoLoad;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import junit.framework.Assert;


public class TestSingleToMultipleConversion extends TestBase {
  @Embedded
  private static class HasString {
    String s = "foo";
  }

  @Entity(value = "B", noClassnameStored = true)
  private static class HasEmbeddedStringy {
    @Id ObjectId id;
    HasString hs = new HasString();
  }

  @Entity(value = "B", noClassnameStored = true)
  private static class HasEmbeddedStringyArray {
    @Id             ObjectId    id;
    @AlsoLoad("hs") HasString[] hss;
  }

  @Entity(value = "B", noClassnameStored = true)
  private static class HasEmbeddedStringySet {
    @Id             ObjectId       id;
    @AlsoLoad("hs") Set<HasString> hss;
  }


  @Entity(value = "A", noClassnameStored = true)
  private static class HasSingleString {
    @Id ObjectId id;
    String s = "foo";
  }

  @Entity(value = "A", noClassnameStored = true)
  private static class HasManyStringsArray {
    @Id            ObjectId id;
    @AlsoLoad("s") String[] strings;
  }

  @Entity(value = "A", noClassnameStored = true)
  private static class HasManyStringsList {
    @Id            ObjectId     id;
    @AlsoLoad("s") List<String> strings;
  }

  @Test
  public void testBasicType() throws Exception {
    ds.delete(ds.createQuery(HasSingleString.class));
    ds.save(new HasSingleString());
    Assert.assertNotNull(ds.find(HasSingleString.class).get());
    Assert.assertEquals(1, ds.find(HasSingleString.class).countAll());
    final HasManyStringsArray hms = ds.find(HasManyStringsArray.class).get();
    Assert.assertNotNull(hms);
    Assert.assertNotNull(hms.strings);
    Assert.assertEquals(1, hms.strings.length);

    final HasManyStringsList hms2 = ds.find(HasManyStringsList.class).get();
    Assert.assertNotNull(hms2);
    Assert.assertNotNull(hms2.strings);
    Assert.assertEquals(1, hms2.strings.size());
  }

  @Test
  public void testEmbeddedType() throws Exception {
    ds.save(new HasEmbeddedStringy());
    Assert.assertNotNull(ds.find(HasEmbeddedStringy.class).get());
    Assert.assertEquals(1, ds.find(HasEmbeddedStringy.class).countAll());
    final HasEmbeddedStringyArray has = ds.find(HasEmbeddedStringyArray.class).get();
    Assert.assertNotNull(has);
    Assert.assertNotNull(has.hss);
    Assert.assertEquals(1, has.hss.length);

    final HasEmbeddedStringySet has2 = ds.find(HasEmbeddedStringySet.class).get();
    Assert.assertNotNull(has2);
    Assert.assertNotNull(has2.hss);
    Assert.assertEquals(1, has2.hss.size());
  }
}
