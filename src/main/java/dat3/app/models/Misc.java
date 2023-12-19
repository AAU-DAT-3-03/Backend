package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import dat3.app.utility.MongoUtility;

/**
 * This class exists such that incidents can have case numbers. There is a single record on the database containing a counting value, which is updated each time a new caseNumber is generated (by simply incrementing). 
 */
public class Misc extends StandardModel<Misc> {
    private String id = null;
    private Long caseNumber = null;

    public Long getCaseNumber() {
        return caseNumber;
    }
    public void setCaseNumber(Long caseNumber) {
        this.caseNumber = caseNumber;
    }

    // Builder:
    public static class MiscBuilder {
        private Misc misc = new Misc();
        public MiscBuilder setCaseNumber(Long caseNumber) {
            misc.setCaseNumber(caseNumber);
            return this;
        }
        public MiscBuilder setId(String id) {
            misc.setId(id);
            return this;
        }
        public Misc getMisc() {
            Misc tmp = this.misc;
            this.misc = new Misc();
            return tmp;
        }
    }
    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Document toDocument() {
        Document document = new Document();
        if (this.id != null) document.put("_id", new ObjectId(id));
        if (this.caseNumber != null) document.put("caseNumber", this.caseNumber);
        return document;
    }

    @Override
    public Misc fromDocument(Document document) {
        MiscBuilder builder = new MiscBuilder();
        if (document.containsKey("caseNumber")) builder.setCaseNumber(document.getLong("caseNumber"));
        if (document.containsKey("_id")) builder.setId(document.getObjectId("_id").toHexString());
        return builder.getMisc();
    }

    /**
     * Gets the caseNumber from the database, increments the number, and returns the previous number. If no record is found, it creates one (and wipes the collection beforehand to make sure).
     * @return A brand new case number
     */
    public static Long getCaseNumberAndIncrement() {
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> miscCollection = MongoUtility.getCollection(client, "misc");       
                MiscBuilder miscBuilder = new MiscBuilder();
                // Find the very first record available by using an empty filter (should be only one record available)
                Misc misc = miscBuilder.getMisc().findOne(miscCollection, session);

                // If there is no record, create one! Otherwise extract the number.
                Long caseNumber = null;
                if (misc == null || misc.getId() == null || misc.getCaseNumber() == null) {
                    // Drop the collection for good measure.
                    miscCollection.drop();
                    misc = new Misc();
                    misc.setCaseNumber(1l);
                    misc.insertOne(miscCollection, session);
                    caseNumber = 1l;
                }
                else {
                    caseNumber = misc.getCaseNumber();
                }
                
                // increment the case number by one, such that a unique number is generated each time around.  
                miscBuilder.setCaseNumber(caseNumber + 1).getMisc().updateOne(miscCollection, session, null);
                return caseNumber;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
