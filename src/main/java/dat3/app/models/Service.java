package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Service extends StandardModel<Service> {
    private String id = null;
    private String name = null;
    private String companyId = null;

    // ---------- Getters & Setters ---------- //
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    // ---------- Builder subclass ---------- //
    public static class ServiceBuilder  {
        private Service service = new Service();

        public ServiceBuilder setId(String id) {
            service.setId(id);
            return this;
        }

        public ServiceBuilder setName(String name) {
            service.setName(name);
            return this;
        }

        public ServiceBuilder setCompanyId(String companyId) {
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
        if (this.id != null) document.put("_id", new ObjectId(this.id));
        if (this.name != null) document.put("name", this.name);
        if (this.companyId != null) document.put("companyId", new ObjectId(this.companyId));
        return document;
    }

    @Override
    public Service fromDocument(Document document) {
        Service service = new Service();
        if (document.containsKey("_id")) service.id = document.getObjectId("_id").toHexString();
        if (document.containsKey("name")) service.name = document.getString("name");
        if (document.containsKey("companyId")) service.companyId = document.getObjectId("companyId").toHexString();
        return service;
    }

    // ---------- Static Methods ---------- //
    

    // ---------- Object Methods ---------- //

}
