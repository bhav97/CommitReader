package bhav.commit.data.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface to interact with api for retrofit. Hail retrofit. `\(-.-)/`
 */
public interface IApp {
    String ENDPOINT = "https://cs-scraper.herokuapp.com/";

    @GET("v1/home/{language}/{page}")
    Call<List<Comic>> getComicList(@Path("language") String language,
                                   @Path("page") int page);

    @POST("v1/comic/")
    Call<Comic> getComic(@Body Comic.Request request);

    @GET("v1/search/{language}/{page}")
    Call<List<Comic>> search(@Path("language") String language,
                             @Path("page") String page,
                             @Query("query") String query);

//    @GET("v1/archives/{language}")
//    Call<List<Archive>> getArchives();
}
