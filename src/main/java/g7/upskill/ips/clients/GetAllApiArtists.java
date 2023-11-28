package g7.upskill.ips.clients;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import g7.upskill.ips.MyDBUtils;
import g7.upskill.ips.model.Artist;
import g7.upskill.ips.persistence.DBStorage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetAllApiArtists {


    public static void searchAllArtist_(String xappToken, String apiUrl) {

        System.out.println("apiUrl apiUrl apiUrl" + apiUrl);
    }



        public static void searchAllArtist(String xappToken, String apiUrl, int size) {

        System.out.println("apiUrl apiUrl apiUrl" + apiUrl);

        if (apiUrl.isEmpty())
        {
            apiUrl = "https://api.artsy.net/api/artists?size=100";
            System.out.println("+++++ searchAllArtist searchAllArtist searchAllArtist");
        }

        else
        {
            apiUrl= apiUrl + "&size="+ size;
            System.out.println("apiUrl not isEmpty  searchAllArtist"  + apiUrl);
        }
        OkHttpClient client = new OkHttpClient();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(apiUrl);

        Request request = new Request.Builder()
                .url(apiUrl)
                .header("X-XAPP-Token", xappToken)
                .build();

        DBStorage storage = new DBStorage();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // Processar a resposta aqui conforme necessário
                String responseBody = response.body().string();
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = (JsonObject)parser.parse(responseBody);
                JsonArray data = jsonObject.getAsJsonObject("_embedded").getAsJsonArray("artists");
                System.out.println("data " + data);
                // Deserialize a list of genes
                List<Artist>  artists = new ArrayList<>();
                Type listType = new TypeToken<ArrayList<Artist>>(){}.getType();
                artists = gson.fromJson(data, listType);


                for (Artist artist : artists) {


                    artist.setBiography(MyDBUtils.cleanString(artist.getBiography()));
                    artist.setBirthyear(MyDBUtils.cleanString(artist.getBirthyear()));
                    artist.setLocation(MyDBUtils.cleanString(artist.getLocation()));
                    artist.setHometown(MyDBUtils.cleanString(artist.getHometown()));
                    artist.setName(MyDBUtils.cleanString(artist.getName()));
                    artist.setSlug(MyDBUtils.cleanString(artist.getSlug()));
                    artist.setDeathyear(MyDBUtils.cleanString(artist.getDeathyear()));
                    artist.setThumbnail(MyDBUtils.cleanString(artist.getThumbnail()));
                    artist.setUrl(MyDBUtils.cleanString(artist.getUrl()));
                    artist.setNationality(MyDBUtils.cleanString(artist.getNationality()));


                    storage.createArtist(artist);


                }

            } else {
                System.out.println("Falha na solicitação à API. Código de resposta: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args){

    }

}