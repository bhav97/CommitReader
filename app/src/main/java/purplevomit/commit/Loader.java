package purplevomit.commit;

import android.os.Build;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bhav on 9/23/16 for the CommitReader Project.
 */
public abstract class Loader extends LoaderBase<List<Comic>, Comic> {

    private final static String TAG = Loader.class.getSimpleName();

    private final static String CALL_TYPE_LIST = TAG + ".list";
    private final static String CALL_TYPE_COMIC = TAG + ".comic";

    private Map<String, Call> callMap;
    private String language;
    private int page;

    public Loader(String language) {
        callMap = new HashMap<>();
        this.language = language;
        page = 1;
    }

    public void getComicList() {
        loadStarted(CALL_TYPE_LIST);
        Call<List<Comic>> listCall = getDataInterface().getComicList(language, page);
        listCall.enqueue(new Callback<List<Comic>>() {
            @Override
            public void onResponse(Call<List<Comic>> call, Response<List<Comic>> response) {
                if (response.isSuccessful()) {
                    loadedComicList(response.body());
                } else {
                    loadFailed(call, CALL_TYPE_LIST);
                    Log.e(TAG, "Response Error: " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Comic>> call, Throwable t) {
                loadFailed(call, CALL_TYPE_LIST);
                Log.e(TAG, "Call Failed: ", t);
            }
        });
        callMap.put(CALL_TYPE_LIST, listCall);
        page += 1;
    }

    public void getComic(Comic comic) {
        loadStarted(CALL_TYPE_COMIC);
        Call<Comic> comicCall = getDataInterface().getComic(new Comic.ComicRequest(comic.url));
        comicCall.enqueue(new Callback<Comic>() {
            @Override
            public void onResponse(Call<Comic> call, Response<Comic> response) {
                if (response.isSuccessful()) {
                    loadedComic(comic, response.body());
                } else {
                    loadFailed(call, CALL_TYPE_COMIC);
                    Log.e(TAG, "Response Error: " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Comic> call, Throwable t) {
                loadFailed(call, CALL_TYPE_COMIC);
                Log.e(TAG, "Call Failed: ", t);
            }
        });
        callMap.put(CALL_TYPE_COMIC, comicCall);
    }

    private void loadFailed(Call call, String type) {
        loadFailed(type);
        if(callMap.containsValue(call)) {
            callMap.remove(call);
        }
    }


    private void loadedComicList(List<Comic> comics) {
        onComicListLoaded(comics);
        loadFinished(CALL_TYPE_LIST);
    }

    private void loadedComic(Comic base, Comic comic) {
        base.height = comic.height;
        base.image = comic.image;
        base.width = comic.width;
        base.id = comic.id;
        onComicLoaded(base);
        loadFinished(CALL_TYPE_COMIC);
    }


    @Override
    public void cancelLoading() {
        if (callMap.size()>0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                callMap.values().forEach(Call::cancel);
            } else {
                for(Call call: callMap.values()) {
                    call.cancel();
                }
            }
        }
    }
}
