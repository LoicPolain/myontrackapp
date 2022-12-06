package be.ontrack;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Map;

import com.codename1.io.JSONParser;
import com.codename1.io.URL;


public class NMBSData {
    private String urlString = "https://api.irail.be";

    private AcceptedLanguages language = AcceptedLanguages.nl;

    public void setLanguage(AcceptedLanguages language) {
        this.language = language;
    }

    public AcceptedLanguages getLanguage() {
        return language;
    }

    public Map<String, Object> getStations(){
        String urlsations = urlString.concat("/stations/?format=json&lang=" + language);
        return stream(generateUrl(urlsations));
    }

    public URL getStations2(){
        String urlsations = urlString.concat("/stations/?format=json&lang=" + language);
        return generateUrl(urlsations);
    }

    public String test(){
        String liveboardUrl = urlString.concat("/liveboard/");
        String s = "/liveboard/?station=Gent-Sint-Pieters&date=300917&time=1230&arrdep=departure&lang=en&format=json&alerts=false";

        return urlString;
    }

    private Map<String, Object> stream(URL url) {
        try (InputStream input = url.openStream()) {
            JSONParser jsonParser = new JSONParser();

            return jsonParser.parseJSON(new InputStreamReader(input));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URL generateUrl(String url){
        try {
            return new URL(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
