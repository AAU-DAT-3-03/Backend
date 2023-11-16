package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Company extends StandardModel<Company> {
    private String id = null;
    private String name = null;

    // ---------- Getters & Setters ---------- //
    public String getId() {
        return id;
    }

    public void setId(String _id) {
        this.id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ---------- Builder subclass ---------- //
    public static class CompanyBuilder {
        private Company company = new Company();

        public CompanyBuilder setName(String name) {
            company.setName(name);
            return this;
        }

        public CompanyBuilder setId(String id) {
            company.setId(id);
            return this;
        }

        public Company getCompany() {
            Company temp = this.company;
            this.company = new Company();
            return temp;
        }
    }

    // ---------- Abstract method overrides ---------- //
    @Override
    public Document toDocument() {
        Document document = new Document();
        if (this.id != null) document.put("_id", new ObjectId(this.id));
        if (this.name != null) document.put("name", this.name);
        return document;
    }

    @Override
    public Company fromDocument(Document document) {
        Company company = new Company();
        if (document.containsKey("_id")) company.id = document.getObjectId("_id").toHexString();
        if (document.containsKey("name")) company.name = document.getString("name");
        return company;
    }    

    // ---------- Static Methods ---------- //

    // ---------- Object Methods ---------- //
}
