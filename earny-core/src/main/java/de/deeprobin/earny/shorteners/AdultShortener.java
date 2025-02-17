package de.deeprobin.earny.shorteners;

import de.deeprobin.earny.exception.ShorteningException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public final class AdultShortener implements IShortener {

    public final static int USER_ID = 21904803;
    public final static String API_KEY = "badfcfdc811234775de014e538f6a4c3";

    private final int userId;
    private final String apiKey;

    @Override
    public String shortUrl(String url) throws ShorteningException {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            URIBuilder builder = new URIBuilder("http://earny.deeprobin.de/short-url/adult");
            builder.setScheme("https").setHost("earny.deeprobin.de").setPath("/short-url/adult")
                    .setParameter("id", String.valueOf(this.userId))
                    .setParameter("key", this.apiKey)
                    .setParameter("url", url);
            builder.setCharset(Charsets.UTF_8);
            HttpGet httpGet = new HttpGet(builder.build());

            HttpResponse response;
            try {
                response = httpclient.execute(httpGet);
            } catch (IOException e) {
                throw new ShorteningException(e);
            }
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

                StringBuilder result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                String res = result.toString();
                return res;
            }
            throw new ShorteningException("no response.");

        } catch (URISyntaxException | IOException ex) {
            throw new ShorteningException(ex);
        }
    }

    @Override
    public String[] getIdentifiers() {
        return new String[] { "adult", "adult.xyz" };
    }

}
