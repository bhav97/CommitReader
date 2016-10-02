package purplevomit.commit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bhav on 9/23/16 for the CommitReader Project.
 */
public abstract class LoaderBase<T, K> {

    private int loadingCount;
    private IApp dataInterface;

    public LoaderBase() {
        loadingCount = 0;
    }

    public abstract void onComicListLoaded(T data);

    public abstract void onComicLoaded(K data);

    public abstract void cancelLoading();


    public boolean isLoading() {
        return loadingCount > 0;
    }

    protected void loadStarted(String s) {
        loadingCount++;
    }

    protected void loadFinished(String s) {
        loadingCount--;
    }

    protected void loadFailed(String s) {
        loadingCount--;
    }

    protected void resetLoadingCount() {
        loadingCount = 0;
    }


    private void createApi() {
        dataInterface = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(IApp.ENDPOINT)
                .build()
                .create(IApp.class);
    }

    public IApp getDataInterface() {
        if(dataInterface ==null) createApi();
        return dataInterface;
    }
}
