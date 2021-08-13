package kanye;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Quotes {

    private static URL url;

    private List<String> quotes = new ArrayList<>();
    public boolean readyToRead;

    public Quotes(String url){
        readyToRead = false;
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
            System.out.println("ready to grab quotes");
            for(int i=0;i<n;i++)
                quotes.add(getContent());

            readyToRead = true;
            System.out.println("Quotes are grabbed");
        }).start();
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