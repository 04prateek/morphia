package com.google.code.morphia.mapping.primitives;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import com.google.code.morphia.TestBase;
import com.google.code.morphia.annotations.Id;


public class ByteMappingTest extends TestBase {
  private static class Bytes {
    @Id
    ObjectId id;
    final List<Byte[]> listWrapperArray = new ArrayList<Byte[]>();
    final List<byte[]> listPrimitiveArray = new ArrayList<byte[]>();
    List<Byte> listWrapper = new ArrayList<Byte>();
    byte singlePrimitive;
    Byte singleWrapper;
    byte[] primitiveArray;
    Byte[] wrapperArray;
    byte[][] nestedPrimitiveArray;
    Byte[][] nestedWrapperArray;
  }


  @Test
  public void testMapping() throws Exception {
    morphia.map(Bytes.class);
    final Bytes ent = new Bytes();
    ent.listWrapperArray.add(new Byte[] {1, 2});
    ent.listPrimitiveArray.add(new byte[] {2, 3, 12});
    ent.listWrapper.addAll(Arrays.asList((byte)148, (byte)6, (byte)255));
    ent.singlePrimitive = 100;
    ent.singleWrapper = 47;
    ent.primitiveArray = new byte[] {5, 93};
    ent.wrapperArray = new Byte[] { 55, 16, 99 };
    ent.nestedPrimitiveArray = new byte[][] {{1, 2}, {3, 4}};
    ent.nestedWrapperArray = new Byte[][] {{1, 2}, {3, 4}};
    ds.save(ent);
    final Bytes loaded = ds.get(ent);

    Assert.assertNotNull(loaded.id);

    Assert.assertArrayEquals(ent.listWrapperArray.get(0), loaded.listWrapperArray.get(0));
    Assert.assertArrayEquals(ent.listPrimitiveArray.get(0), loaded.listPrimitiveArray.get(0));
    Assert.assertEquals(ent.listWrapper, loaded.listWrapper);

    Assert.assertEquals(ent.singlePrimitive, loaded.singlePrimitive, 0);
    Assert.assertEquals(ent.singleWrapper, loaded.singleWrapper, 0);

    Assert.assertArrayEquals(ent.primitiveArray, loaded.primitiveArray);
    Assert.assertArrayEquals(ent.wrapperArray, loaded.wrapperArray);
    Assert.assertArrayEquals(ent.nestedPrimitiveArray, loaded.nestedPrimitiveArray);
    Assert.assertArrayEquals(ent.nestedWrapperArray, loaded.nestedWrapperArray);
  }
}
