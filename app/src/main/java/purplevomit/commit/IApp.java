package purplevomit.commit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by bhav on 9/23/16 for the CommitReader Project.
 */
public interface IApp {
    String ENDPOINT = "https://cs-scraper.herokuapp.com/";

    @GET("v1/home/{language}/{page}")
    Call<List<Comic>> getComicList(@Path("language") String language,
                                   @Path("page") int page);

    @POST("v1/comic/")
    Call<Comic> getComic(@Body Comic.ComicRequest cr);
}
