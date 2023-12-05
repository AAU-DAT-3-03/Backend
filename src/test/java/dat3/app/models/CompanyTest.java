package dat3.app.models;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import dat3.app.utility.MongoUtility;

public class CompanyTest {
    @Test
    void TestCompanyInsertion() {
        Company company = new Company();
        company.setId("656dd24dd86c1a107a567ed4");
        company.setName("Trekant");

        Company recCompany = null;
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession clientSession = client.startSession()) {
                MongoCollection<Document> companyCollection = MongoUtility.getCollection(client, "companies");
                company.insertOne(companyCollection, clientSession);

                Company filter = new Company();
                filter.setId("656dd24dd86c1a107a567ed4");

                recCompany = filter.findOne(companyCollection, clientSession);

                companyCollection.drop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(Company.CompanyEquals(company, recCompany));
    }
}
