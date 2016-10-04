package purplevomit.commit.data;

import purplevomit.commit.data.api.IApp;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Base class for Loading data. Manages retrofit interface.
 */
abstract class LoaderBase<T, K> {

    private int loadingCount;
    private IApp dataInterface;

    LoaderBase() {
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

    IApp getDataInterface() {
        if(dataInterface ==null) createApi();
        return dataInterface;
    }
}
