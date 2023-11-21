package dat3.app.routes.companies;

import java.io.IOException;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.models.Company;
import dat3.app.models.Company.CompanyBuilder;
import dat3.app.server.Auth;
import dat3.app.server.Response;
import dat3.app.utility.ExchangeUtility;
import dat3.app.utility.MongoUtility;

public abstract class CompanyRoutes {
    public static void getCompanies(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        Document parsedURI = parseQueryString(exchange);
        if (parsedURI == null) {
            Response response = new Response();
            response.setStatusCode(1);
            response.setMsg("Invalid query string");
            try {
                response.sendResponse(exchange);
            } catch (IOException e) {}
            return;
        }

        Company filter = new Company().fromDocument(parsedURI);

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> companyCollection = MongoUtility.getCollection(client, "companies");
                List<Company> companies = MongoUtility.iterableToList(filter.findMany(companyCollection, session));
                Response response = new Response();
                response.setStatusCode(0);
                response.setMsg(companies);
                try {
                    response.sendResponse(exchange);
                } catch (IOException e1) {}
                return;
            }
        } catch (Exception e) {
            Response response = new Response();
            response.setStatusCode(1);
            response.setMsg("Something went wrong.");
            try {
                response.sendResponse(exchange);
            } catch (IOException e1) {}
            return;
        }
    }

    public static void putCompanies(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                Company company = ExchangeUtility.parseJsonBody(exchange, 1000, Company.class);
                if (company.getName() == null || company.getId() == null) {
                    Response response = new Response();
                    response.setStatusCode(1);
                    response.setMsg("Invalid company object.");
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                }
                CompanyBuilder builder = new CompanyBuilder();
                MongoCollection<Document> companyCollection = MongoUtility.getCollection(client, "companies");
                UpdateResult result = company.updateMany(companyCollection, session, builder.setId(company.getId()).getCompany());

                if (result.getModifiedCount() > 0) {
                    Response response = new Response();
                    response.setStatusCode(0);
                    response.setMsg("Updated the company.");
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                } else {
                    Response response = new Response();
                    response.setStatusCode(1);
                    response.setMsg("Updated 0 companies.");
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                }
            }
        } catch (Exception e) {
            Response response = new Response();
            response.setStatusCode(1);
            response.setMsg("Something went wrong.");
            try {
                response.sendResponse(exchange);
            } catch (IOException e1) {}
            return;
        }
    }

    public static void deleteCompanies(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                Company company = ExchangeUtility.parseJsonBody(exchange, 1000, Company.class);
                if (company.getId() == null) {
                    Response response = new Response();
                    response.setStatusCode(1);
                    response.setMsg("Invalid company object.");
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                }
                CompanyBuilder builder = new CompanyBuilder();
                MongoCollection<Document> companyCollection = MongoUtility.getCollection(client, "companies");
                DeleteResult result = builder.setId(company.getId()).getCompany().deleteMany(companyCollection, session);

                if (result.getDeletedCount() > 0) {
                    Response response = new Response();
                    response.setStatusCode(0);
                    response.setMsg("Deleted the company.");
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                } else {
                    Response response = new Response();
                    response.setStatusCode(1);
                    response.setMsg("Deleted 0 companies.");
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                }
            }
        } catch (Exception e) {
            Response response = new Response();
            response.setStatusCode(1);
            response.setMsg("Something went wrong.");
            try {
                response.sendResponse(exchange);
            } catch (IOException e1) {}
            return;
        }
    }

    public static void postCompanies(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }
        
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                Company company = ExchangeUtility.parseJsonBody(exchange, 1000, Company.class);
                if (company.getId() != null || company.getName() == null) {
                    Response response = new Response();
                    response.setStatusCode(1);
                    response.setMsg("Invalid company object.");
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                }
                
                MongoCollection<Document> companyCollection = MongoUtility.getCollection(client, "companies");
                InsertOneResult result = company.insertOne(companyCollection, session);

                if (result.wasAcknowledged()) {
                    Response response = new Response();
                    response.setStatusCode(0);
                    response.setMsg("Inserted the company.");
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                } else {
                    Response response = new Response();
                    response.setStatusCode(1);
                    response.setMsg("Company was not inserted.");
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                }
            }
        } catch (Exception e) {
            Response response = new Response();
            response.setStatusCode(1);
            response.setMsg("Something went wrong.");
            try {
                response.sendResponse(exchange);
            } catch (IOException e1) {}
            return;
        }
    }

    private static Document parseQueryString(HttpExchange exchange) {
        try {
            Document document = new Document();
            String[] pairs = exchange.getRequestURI().getQuery().split("&");
            for (String string : pairs) {
                String[] pair = string.split("=");

                if (pair[0].equals("id")) {
                    if (pair[1].equals("*")) return new Document();
                    document.put("_id", new ObjectId(pair[1]));
                    continue;
                }

                if (pair[0].equals("name")) {
                    document.put("name", pair[1]);
                    continue;
                }

                return null;
            }

            return document;
        } catch (Exception e) {
            return null;
        }
    }
}
