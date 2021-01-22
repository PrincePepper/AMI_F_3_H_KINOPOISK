package sample;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static String makeAPICall(String uri, List<NameValuePair> params, List<NameValuePair> headerParams) throws URISyntaxException, IOException {
        String response_content = "";

        URIBuilder query = new URIBuilder(uri);
        query.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        for (NameValuePair p : headerParams) {
            request.addHeader(p.getName(), p.getValue());
        }

        CloseableHttpResponse response = client.execute(request);

        try {
            HttpEntity entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return response_content;
    }

    public static JSONObject ParseByKeyWord(String keyWord) throws IOException, URISyntaxException, org.json.simple.parser.ParseException {
        var params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("keyword", keyWord));
        params.add(new BasicNameValuePair("page", "1"));
        var headerParams = new ArrayList<NameValuePair>();
        headerParams.add(new BasicNameValuePair("accept", "application/json"));
        headerParams.add(new BasicNameValuePair("X-API-KEY", "63d5c0f0-0281-4273-a619-90c73c5357b5"));
        var result = makeAPICall("https://kinopoiskapiunofficial.tech/api/v2.1/films/search-by-keyword", params, headerParams);
        Object obj = new JSONParser().parse(result); // Object obj = new JSONParser().parse(new FileReader("JSONExample.json"));
        return (JSONObject) obj;
    }

    public static JSONObject ParseById(String id) throws org.json.simple.parser.ParseException, IOException, URISyntaxException {
        var paramsById = new ArrayList<NameValuePair>();
        var headerParamsById = new ArrayList<NameValuePair>();
        headerParamsById.add(new BasicNameValuePair("accept", "application/json"));
        headerParamsById.add(new BasicNameValuePair("X-API-KEY", "63d5c0f0-0281-4273-a619-90c73c5357b5"));
        var resultById = Parser.makeAPICall("https://kinopoiskapiunofficial.tech/api/v2.1/films/" + id, paramsById, headerParamsById);
        Object objById = new JSONParser().parse(resultById); // Object obj = new JSONParser().parse(new FileReader("JSONExample.json"));
        JSONObject joById = (JSONObject) objById;

        return (JSONObject) joById.get("data");
    }

    public static JSONObject ParseByOutherId(String id, String type) throws org.json.simple.parser.ParseException, IOException, URISyntaxException {
        if (type.equals("FILM")) type = "/movies/";
        else if (type.equals("TV_SHOW")) type = "/tv-series/";
        var site = "https://api.kinopoisk.cloud" + type + id + "/token/f55d01df330ef48f2950f2d1a549d263";

        var response_content = "";
        var query = new URIBuilder(site);
        var client = HttpClients.createDefault();
        var request = new HttpGet(query.build());

        var response = client.execute(request);

        try {
            var entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Object objById = new JSONParser().parse(response_content);

        return (JSONObject) objById;
    }

}
