package purplevomit.commit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bhav on 9/23/16 for the CommitReader Project.
 */
public abstract class LoaderBase<T, K, V> implements ILoadListener{

    private final AtomicInteger loadingCount;
    private IApp dataInterface;
    private List<ILoadListener.LoadingCallbacks> loadingCallbacks;

    public LoaderBase() {
        loadingCount = new AtomicInteger(0);
    }

    public abstract void onComicListLoaded(T data);

    public abstract void onComicLoaded(K data);

//    public abstract void onContentLoaded(V data);

    public abstract void cancelLoading();

    @Override
    public boolean isLoading() {
        return loadingCount.get() > 0;
    }

    @Override
    public void registerCallbacks(LoadingCallbacks callback) {
        if (loadingCallbacks == null) {
            loadingCallbacks = new ArrayList<>(1);
        }
        loadingCallbacks.add(callback);
    }

    @Override
    public void unregisterCallbacks(LoadingCallbacks callback) {
        if (loadingCallbacks != null && loadingCallbacks.contains(callback)) {
            loadingCallbacks.remove(callback);
        }
    }

    protected void loadStarted(String s) {
        if (0 == loadingCount.getAndIncrement()) {
            dispatchLoadingStartedCallbacks();
        }
    }

    protected void loadFinished(String s) {
        if (0 == loadingCount.decrementAndGet()) {
            dispatchLoadingFinishedCallbacks();
        }
    }

    protected void loadFailed(String s) {
        if (0 == loadingCount.decrementAndGet()) {
            dispatchLoadingInterruptedCallbacks();
        }
    }

    protected void resetLoadingCount() {
        loadingCount.set(0);
    }

    protected void dispatchLoadingStartedCallbacks() {
        if (loadingCallbacks == null || loadingCallbacks.isEmpty()) return;
        for (LoadingCallbacks loadingCallback : loadingCallbacks) {
            loadingCallback.loadStarted();
        }
    }

    protected void dispatchLoadingFinishedCallbacks() {
        if (loadingCallbacks == null || loadingCallbacks.isEmpty()) return;
        for (LoadingCallbacks loadingCallback : loadingCallbacks) {
            loadingCallback.loadFinished();
        }
    }

    protected void dispatchLoadingInterruptedCallbacks() {
        if (loadingCallbacks == null || loadingCallbacks.isEmpty()) return;
        for (LoadingCallbacks loadingCallback : loadingCallbacks) {
            loadingCallback.loadInterrupted();
        }
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
