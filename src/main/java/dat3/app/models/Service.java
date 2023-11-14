package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Service extends StandardModel<Service> {
    private ObjectId _id = null;
    private String name = null;
    private ObjectId companyId = null;

    // ---------- Getters & Setters ---------- //
    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectId getCompanyId() {
        return companyId;
    }

    public void setCompanyId(ObjectId companyId) {
        this.companyId = companyId;
    }

    // ---------- Builder subclass ---------- //
    public static class ServiceBuilder  {
        private Service service = new Service();

        public ServiceBuilder setId(ObjectId id) {
            service.setId(id);
            return this;
        }

        public ServiceBuilder setName(String name) {
            service.setName(name);
            return this;
        }

        public ServiceBuilder setCompanyId(ObjectId companyId) {
            service.setCompanyId(companyId);
            return this;
        }

        public Service getService() {
            Service temp = this.service;
            this.service = new Service();
            return temp;
        }
    }

    // ---------- Abstract method overrides ---------- //
    @Override
    public Document toDocument() {
        Document document = new Document();
        if (this._id != null) document.put("_id", this._id);
        if (this.name != null) document.put("name", this.name);
        if (this.companyId != null) document.put("companyId", this.companyId);
        return document;
    }

    @Override
    public Service fromDocument(Document document) {
        Service service = new Service();
        if (document.containsKey("_id")) service._id = document.getObjectId("_id");
        if (document.containsKey("name")) service.name = document.getString("name");
        if (document.containsKey("companyId")) service.companyId = document.getObjectId("companyId");
        return service;
    }

    // ---------- Static Methods ---------- //
    

    // ---------- Object Methods ---------- //

}
