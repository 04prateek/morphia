package com.google.code.morphia;


import java.io.Serializable;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.PostLoad;
import com.google.code.morphia.annotations.Property;
import com.google.code.morphia.annotations.Reference;
import com.google.code.morphia.mapping.lazy.LazyFeatureDependencies;


/**
 * Tests mapper functions; this is tied to some of the internals.
 *
 * @author scotthernandez
 */
public class TestMapper extends TestBase {
  public static class A {
    static int loadCount;
    @Id ObjectId id;

    String getId() {
      return id.toString();
    }

    @PostLoad
    protected void postConstruct() {
      if (loadCount > 1) {
        throw new RuntimeException();
      }

      loadCount++;
    }
  }

  @Entity("holders")
  public static class HoldsMultipleA {
    @Id        ObjectId id;
    @Reference A        a1;
    @Reference A        a2;
  }

  @Entity("holders")
  public static class HoldsMultipleALazily {
    @Id                     ObjectId id;
    @Reference(lazy = true) A        a1;
    @Reference              A        a2;
    @Reference(lazy = true) A        a3;
  }

  public static class CustomId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Property("v") ObjectId id;
    @Property("t") String   type;

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof CustomId)) {
        return false;
      }
      final CustomId other = (CustomId) obj;
      if (id == null) {
        if (other.id != null) {
          return false;
        }
      } else if (!id.equals(other.id)) {
        return false;
      }
      if (type == null) {
        if (other.type != null) {
          return false;
        }
      } else if (!type.equals(other.type)) {
        return false;
      }
      return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      final StringBuilder builder = new StringBuilder();
      builder.append("CustomId [");
      if (id != null) {
        builder.append("id=").append(id).append(", ");
      }
      if (type != null) {
        builder.append("type=").append(type);
      }
      builder.append("]");
      return builder.toString();
    }
  }

  public static class UsesCustomIdObject {
    @Id CustomId id;
    String text;
  }

  @Test @Ignore("Test looks wonky. disabling until after 0.101")
  public void SingleLookup() throws Exception {
    final A a = new A();
    HoldsMultipleA holder = new HoldsMultipleA();
    holder.a1 = a;
    holder.a2 = a;
    ds.save(a, holder);
    holder = ds.get(HoldsMultipleA.class, holder.id);
    Assert.assertEquals(1, A.loadCount);
    Assert.assertTrue(holder.a1 == holder.a2);
  }

  @Test
  public void SingleProxy() throws Exception {
    // TODO us: exclusion does not work properly with maven + junit4
    if (!LazyFeatureDependencies.testDependencyFullFilled()) {
      return;
    }

    A.loadCount = 0;
    final A a = new A();
    HoldsMultipleALazily holder = new HoldsMultipleALazily();
    holder.a1 = a;
    holder.a2 = a;
    holder.a3 = a;
    ds.save(a, holder);
    Assert.assertEquals(0, A.loadCount);
    holder = ds.get(HoldsMultipleALazily.class, holder.id);
    Assert.assertNotNull(holder.a2);
    Assert.assertEquals(1, A.loadCount);
    Assert.assertFalse(holder.a1 == holder.a2);
    // FIXME currently not guaranteed:
    // Assert.assertTrue(holder.a1 == holder.a3);

    // A.loadCount=0;
    // Assert.assertEquals(holder.a1.getId(), holder.a2.getId());

  }

  @Test
  public void SerializableId() throws Exception {
    final CustomId cId = new CustomId();
    cId.id = new ObjectId();
    cId.type = "banker";

    final UsesCustomIdObject object = new UsesCustomIdObject();
    object.id = cId;
    object.text = "hllo";
    ds.save(object);
  }

}
