package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import dat3.app.utility.MongoUtility;

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


    public static Long getCaseNumberAndIncrement() {
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> miscCollection = MongoUtility.getCollection(client, "misc");       
                MiscBuilder miscBuilder = new MiscBuilder();
                Long caseNumber = miscBuilder.getMisc().findOne(miscCollection, session).getCaseNumber();
                miscBuilder.setCaseNumber(caseNumber + 1).getMisc().updateOne(miscCollection, session, null);
                return caseNumber;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
