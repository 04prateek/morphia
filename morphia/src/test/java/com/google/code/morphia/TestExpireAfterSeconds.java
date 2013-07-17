package com.google.code.morphia;


import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Indexes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class TestExpireAfterSeconds extends TestBase {

    @Entity
    public static class HasExpiryField {
        @Id
        private ObjectId id;
        @Indexed(expireAfterSeconds = 5)
        private final Date offerExpiresAt = new Date();
    }

    @Entity
    @Indexes( {
        @Index(value = "offerExpiresAt", expireAfterSeconds = 5)
    })
    public static class ClassAnnotation {
        @Id
        private ObjectId id;
        private final Date offerExpiresAt = new Date();
    }

    @Test
    public void testIndexedField() throws InterruptedException {
        morphia.map(HasExpiryField.class);
        ds.ensureIndexes();

        ds.save(new HasExpiryField());

        final DB db = ds.getDB();
        final DBCollection dbCollection = db.getCollection("HasExpiryField");
        final List<DBObject> indexes = dbCollection.getIndexInfo();

        Assert.assertNotNull(indexes);
        Assert.assertEquals(2, indexes.size());
        DBObject index = null;
        for (final DBObject candidateIndex : indexes) {
            if (candidateIndex.containsField("expireAfterSeconds")) {
                index = candidateIndex;
            }
        }
        Assert.assertNotNull(index);
        Assert.assertEquals(5, index.get("expireAfterSeconds"));
    }

    @Test
    public void testClassAnnotation() throws InterruptedException {
        morphia.map(ClassAnnotation.class);
        ds.ensureIndexes();

        ds.save(new ClassAnnotation());

        final DB db = ds.getDB();
        final DBCollection dbCollection = db.getCollection("ClassAnnotation");
        final List<DBObject> indexes = dbCollection.getIndexInfo();

        Assert.assertNotNull(indexes);
        Assert.assertEquals(2, indexes.size());
        DBObject index = null;
        for (final DBObject candidateIndex : indexes) {
            if (candidateIndex.containsField("expireAfterSeconds")) {
                index = candidateIndex;
            }
        }
        Assert.assertNotNull(index);
        Assert.assertTrue(index.containsField("expireAfterSeconds"));
        Assert.assertEquals(5, index.get("expireAfterSeconds"));
    }
}
