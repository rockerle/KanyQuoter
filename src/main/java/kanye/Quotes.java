package kanye;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Quotes {

    private static URL url;

    public Quotes(String url){
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getContent() throws NullPointerException {

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection == null)
                throw new NullPointerException("No connection available");

            StringBuilder answer = new StringBuilder();
            InputStream is = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            JSONObject line = new JSONObject(br.readLine());
            answer.append("\"" + line.getString("quote") + "\"\n\n");
            answer.append("§o§l- Kanye West§r\n");

            br.close();
            connection.disconnect();

            return answer.toString();
        } catch (Exception e) {
            throw new NullPointerException("No JSON Object found");
        }
    }
}