package purplevomit.commit;

import android.animation.Animator;
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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;

import static android.support.design.widget.BottomSheetBehavior.BottomSheetCallback;
import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;
import static android.support.design.widget.BottomSheetBehavior.from;

public class FeedActivity extends AppCompatActivity {
    private final static String TAG = FeedActivity.class.getSimpleName();

    @BindView(R.id.feed)
    RecyclerView feed;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;
    @BindView(R.id.title)
    TextView title;
    @BindView(android.R.id.empty)
    ProgressBar progress;
    @BindView(R.id.comic)
    PhotoView comicHolder;
    @BindView(R.id.dlprogress)
    ProgressBar dlprogress;
    @BindView(R.id.no_connection)
    FrameLayout noConnection;
    @BindInt(R.integer.columns)
    int columns;
    @BindDimen(R.dimen.bottom_sheet_height)
    int sheetHeight;

    private GridLayoutManager gridLayoutManager;
    private ComicAdapter comicAdapter;
    private BottomSheetBehavior sheetBehavior;

    private List<Comic> comics;
    private Loader loader;

    private boolean comicLoaded = false;
    private Comic loadedComic;

    private boolean connected = true;
    private boolean networkMonitored = false;

    private BottomSheetCallback sheetCallback = new BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if(newState == STATE_COLLAPSED) {
                changeRecyclerViewBottomPadding(sheetHeight);
            } else if (newState == STATE_HIDDEN) {
                comicLoaded = false;
                changeRecyclerViewBottomPadding(feed.getPaddingTop());
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            //animate something
        }
    };

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
                if(gridLayoutManager.findLastCompletelyVisibleItemPosition() == comics.size()-1) {
                    //todo: recyclerview needs to be pushed up here
                    feed.smoothScrollToPosition(comics.size()-1);
                }
                if(loadedComic != comic) {
                    loader.getComic(comic);
                }
            }
        };

        gridLayoutManager = new GridLayoutManager(this, columns);
        feed.setAdapter(comicAdapter);
        feed.setLayoutManager(gridLayoutManager);

        title.setOnClickListener(view -> {
            if(comicLoaded) {
                sheetBehavior.setState(STATE_EXPANDED);
            } else {
                sheetBehavior.setState(STATE_HIDDEN);
            }
        });

        loader = new Loader(this) {
            @Override
            protected void loadStarted(String s) {
                dlprogress.setVisibility(View.VISIBLE);
            }

            @Override
            protected void loadFinished(String s) {
                dlprogress.setVisibility(View.GONE);
            }

            @Override
            protected void loadFailed(String s) {
                dlprogress.setVisibility(View.GONE);
            }

            @Override
            public void onComicListLoaded(List<Comic> data) {
                progress.setVisibility(View.GONE);
                comicAdapter.insert(data);
            }

            @Override
            public void onComicLoaded(Comic data) {
                Glide.with(FeedActivity.this)
                        .load(data.image)
                        .into(comicHolder);
                loadedComic = data;
            }
        };

        feed.addOnScrollListener(new InfiniteScrollListener(gridLayoutManager, loader) {
            @Override
            public void onLoadMore() {
                loader.getComicList();
            }
        });

        checkConnectivity();
        if(connected) loader.getComicList();
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
        if(networkMonitored) {
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

    private ConnectivityManager.NetworkCallback connectivityCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            connected = true;
            if (comicAdapter.getDataItemCount() != 0) return;
            runOnUiThread(() -> {
                noConnection.setVisibility(View.GONE);
//                fadeOut(noConnection);
                progress.setVisibility(View.VISIBLE);
                loader.getComicList();
                getWindow().setStatusBarColor(ContextCompat.getColor(FeedActivity.this,
                        R.color.colorPrimaryDark));
            });
        }

        @Override
        public void onLost(Network network) {
            if(comicAdapter.getDataItemCount() == 0) {
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

    private void checkConnectivity() {
        final ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        //todo: more connectivity checks
        if (!connected) {
            sheetBehavior.setState(STATE_HIDDEN);
            noConnection.setVisibility(View.VISIBLE);
            getWindow().setStatusBarColor(ContextCompat.getColor(FeedActivity.this,
                    android.R.color.holo_red_light));
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
                    connectivityCallback);
            networkMonitored = true;
        }
    }

    private void fadeOut(View v) {
        v.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).setDuration(200L).alpha(0).start();
    }

}
