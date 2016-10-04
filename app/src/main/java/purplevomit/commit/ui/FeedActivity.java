package purplevomit.commit.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import purplevomit.commit.data.api.Comic;
import purplevomit.commit.data.Loader;
import purplevomit.commit.R;
import uk.co.senab.photoview.PhotoView;

import static android.support.design.widget.BottomSheetBehavior.BottomSheetCallback;
import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;
import static android.support.design.widget.BottomSheetBehavior.from;
import static android.view.View.*;

public class FeedActivity extends AppCompatActivity {
    private final static String TAG = FeedActivity.class.getSimpleName();

    @BindView(R.id.feed)
    RecyclerView feed;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;
    @BindView(R.id.title)
    TextView title;
    @BindView(android.R.id.empty)
    LoadingGrid progress;
    @BindView(R.id.comic)
    PhotoView comicHolder;
    @BindView(R.id.dlprogress)
    ProgressBar dlprogress;
    @BindView(R.id.no_connection)
    FrameLayout noConnection;
    @BindView(R.id.empty_bg)
    FrameLayout emptyBg;
    @BindInt(R.integer.columns)
    int columns;
    @BindDimen(R.dimen.bottom_sheet_height)
    int sheetHeight;

    private GridLayoutManager gridLayoutManager;
    private ComicAdapter comicAdapter;
    private BottomSheetBehavior sheetBehavior;

    private ArrayList<Comic> comics;
    private Loader loader;

    private boolean comicLoaded = false;
    private Comic loadedComic;

    private boolean connected = true;
    private boolean networkMonitored = false;
    private int previousSheetState = 9;

    private BottomSheetCallback sheetCallback = new BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == STATE_COLLAPSED) {
                changeRecyclerViewBottomPadding(sheetHeight);
                if (gridLayoutManager.findLastVisibleItemPosition() == comics.size() - 1) {
                    Log.d(TAG, "onStateChanged: ");
                    feed.smoothScrollBy(0, sheetHeight);
                    title.setCompoundDrawables(null, null, getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp), null);
                }
            } else if (newState == STATE_HIDDEN) {
                comicLoaded = false;
                changeRecyclerViewBottomPadding(feed.getPaddingTop());
            } else if (newState == STATE_EXPANDED) {
                title.setCompoundDrawables(null, null, getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp), null);
            }
            previousSheetState = newState;
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            //animate something
        }
    };
    private ConnectivityManager.NetworkCallback connectivityCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            connected = true;
            if (comicAdapter.getDataItemCount() != 0) return;
            runOnUiThread(() -> {
                noConnection.setVisibility(GONE);
//                fadeOut(noConnection);
                progress.setVisibility(VISIBLE);
                loader.getComicList();
                getWindow().setStatusBarColor(ContextCompat.getColor(FeedActivity.this,
                        R.color.colorPrimaryDark));
            });
        }

        @Override
        public void onLost(Network network) {
            if (comicAdapter.getDataItemCount() == 0) {
                runOnUiThread(() -> {
                    checkConnectivity();
                });
            } else {
                getWindow().setStatusBarColor(ContextCompat.getColor(FeedActivity.this,
                        android.R.color.holo_red_light));
            }
            connected = false;
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (!comics.isEmpty()) {
            outState.putParcelableArrayList(Comic.BUNDLE_EXTRA, comics);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ButterKnife.bind(this);

        sheetBehavior = from(bottomSheet);
        sheetBehavior.setHideable(true);
        sheetBehavior.setPeekHeight(sheetHeight);
        sheetBehavior.setBottomSheetCallback(sheetCallback);
        sheetBehavior.setState(STATE_HIDDEN);

        comics = new ArrayList<>();
        comicAdapter = new ComicAdapter(this, comics) {
            @Override
            public void onSelect(Comic comic) {
                title.setText(comic.title);
                sheetBehavior.setState(STATE_COLLAPSED);
                if (loadedComic != comic) {
                    loader.getComic(comic);
                }
            }
        };

        gridLayoutManager = new GridLayoutManager(this, columns);
        feed.setAdapter(comicAdapter);
        feed.setLayoutManager(gridLayoutManager);

        title.setOnClickListener(view -> {
            if (comicLoaded) {
                sheetBehavior.setState(STATE_EXPANDED);
            } else {
                sheetBehavior.setState(STATE_HIDDEN);
            }
        });

        loader = new Loader(getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .getString("language", "en")) {
            @Override
            protected void loadStarted(String s) {
                dlprogress.setVisibility(VISIBLE);
            }

            @Override
            protected void loadFinished(String s) {
                if (progress.getVisibility() == VISIBLE) {
                    finishLoad();
                }
            }

            @Override
            protected void loadFailed(String s) {
                dlprogress.setProgress(0);
            }

            @Override
            public void onComicListLoaded(List<Comic> data) {
                comics.addAll(data);
                comicAdapter.notifyDataSetChanged();
            }

            @Override
            public void onComicLoaded(Comic data) {
                Glide.with(FeedActivity.this)
                        .load(data.image)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e,
                                                       String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource,
                                                           String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFromMemoryCache,
                                                           boolean isFirstResource) {
                                dlprogress.setVisibility(INVISIBLE);
                                if (sheetBehavior.getState() == STATE_COLLAPSED) {
                                    sheetBehavior.setState(STATE_EXPANDED);
                                }
                                return false;
                            }
                        })
                        .into(comicHolder);
                loadedComic = data;
            }
        };

        checkConnectivity();
        //note: vaadaa paav
        if (savedInstanceState != null && savedInstanceState.containsKey(Comic.BUNDLE_EXTRA)) {
            comics.addAll(savedInstanceState.getParcelableArrayList(Comic.BUNDLE_EXTRA));
            comicAdapter.notifyDataSetChanged();
            finishLoad();
        } else {
            if (connected) loader.getComicList();
        }
    }

    @Override
    protected void onDestroy() {
        loader.cancelLoading();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        checkConnectivity();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (networkMonitored) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            networkMonitored = false;
        }
        super.onPause();
    }

    private void changeRecyclerViewBottomPadding(int finalPadding) {
        ValueAnimator animator = ValueAnimator.ofInt(feed.getPaddingBottom(), finalPadding);
        animator.addUpdateListener(valueAnimator ->
                feed.setPadding(
                        feed.getPaddingLeft(),
                        feed.getPaddingTop(),
                        feed.getPaddingRight(),
                        (Integer) valueAnimator.getAnimatedValue())
        );
        animator.setDuration(200);
        animator.start();
    }

    private void checkConnectivity() {
        final ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        //todo: more connectivity checks
        if (!connected) {
            sheetBehavior.setState(STATE_HIDDEN);
            noConnection.setVisibility(VISIBLE);
            getWindow().setStatusBarColor(ContextCompat.getColor(FeedActivity.this,
                    android.R.color.holo_red_light));
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
                    connectivityCallback);
            networkMonitored = true;
        }
    }

    private void finishLoad() {
        Animation fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        progress.setAnimation(fadeOut);
        emptyBg.setAnimation(fadeOut);
        fadeOut.start();
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            } //no-op

            @Override
            public void onAnimationRepeat(Animation animation) {
            } //no-op

            @Override
            public void onAnimationEnd(Animation animation) {
                emptyBg.setVisibility(GONE);
                progress.setVisibility(GONE);
            }
        });
    }
}
