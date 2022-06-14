package kanye;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Quotes {

    private static URL url;

    private List<String> quotes = new ArrayList<>();
    public boolean readyToRead, readyToFake, stopBookUse;

    public Quotes(String url){
        readyToRead = readyToFake = stopBookUse = false;
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getPages(){
        return quotes;
    }

    public void emptyPages(){
        quotes.clear();
    }

    public void getQuotes(int n){
        new Thread(() -> {
            for(int i=0;i<n;i++) {
                String s = getContent();
                if(!quotes.contains(s))
                    quotes.add(s);
                else
                    i--;
            }
            readyToRead = true;
        }).start();
    }

    public String getContent() {

        try {
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            if (conn == null)
                throw new NullPointerException("Kanye.Rest having a little rest for now or is unavailable");

            StringBuilder answer = new StringBuilder();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            JSONObject line = new JSONObject(br.readLine());
            answer.append("\"" + line.getString("quote") + "\"\n\n");
            answer.append("§o§l- Kanye West§r\n");

            br.close();
            conn.disconnect();

            return answer.toString();
        } catch (Exception e) {
            throw new NullPointerException("No JSON Object found");
        }
    }
}