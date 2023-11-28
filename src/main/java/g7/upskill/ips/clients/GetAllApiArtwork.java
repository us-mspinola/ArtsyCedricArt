package g7.upskill.ips.clients;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import g7.upskill.ips.LigacaoArtsy;
import g7.upskill.ips.MyDBUtils;
import g7.upskill.ips.adapters.LocalDateAdapter;
import g7.upskill.ips.model.Artwork;
import g7.upskill.ips.model.Gene;
import g7.upskill.ips.persistence.DBStorage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GetAllApiArtwork {


    public static void searchAllArtworks_(String xappToken, String apiUrl,String idGene) {

        System.out.println("searchAllArtworks searchAllArtworks " + apiUrl +"  dfdf" + idGene );

    }

    public static void searchAllArtworks(String xappToken, String apiUrl,String idGene, int size) {


        if (apiUrl.isEmpty())
        {
            apiUrl = "https://api.artsy.net/api/artworks?size=20";
            System.out.println("nnnnnn searchAllArtworks searchAllArtworks searchAllArtworks");
        }
        else
        {
            apiUrl= apiUrl + "&size="+ size;
            System.out.println("apiUrl not isEmpty "  + apiUrl);
        }

        OkHttpClient client = new OkHttpClient();

       Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter()).setPrettyPrinting().create();

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
                JsonArray data = jsonObject.getAsJsonObject("_embedded").getAsJsonArray("artworks");

                System.out.println("data " + data);
                // Deserialize a list of genes
                List<Artwork>  artworks = new ArrayList<>();
                Type listType = new TypeToken<ArrayList<Artwork>>(){}.getType();
                artworks = gson.fromJson(data, listType);


                for (Artwork artwork : artworks) {

                    ;


                        artwork.setTitle(MyDBUtils.cleanString(artwork.getTitle()));
                        artwork.setThumbnail(MyDBUtils.cleanString(artwork.getThumbnail()));
                        artwork.setUrl(MyDBUtils.cleanString(artwork.getUrl()));
                        artwork.setDate(MyDBUtils.cleanString(artwork.getDate()));
                        artwork.setId_Gene(idGene);

                        storage.createArtwork(artwork);

                        // para cada artwork ir buscar o link da galeria


                        GetAllApiPartners.searchPartner(xappToken, artwork.getPartnersLink(), 1, 2);
                        // GetAllApiPartners.searchAllApiPartners(xappToken,  artwork.getPartnersLink(),1, 2, size);

                    }


            } else {
                System.out.println("Falha na solicitação à API. Código de resposta: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static void main(String[] args){

   //     GetAllApiArtwork.searchAllArtworks();
    }


}
