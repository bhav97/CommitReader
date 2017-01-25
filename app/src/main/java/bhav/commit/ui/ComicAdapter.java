package bhav.commit.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import bhav.commit.R;
import bhav.commit.data.api.Comic;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter class for managing recyclerViews.
 */
public abstract class ComicAdapter extends Adapter<RecyclerView.ViewHolder>{

    private final static String TAG = ComicAdapter.class.getSimpleName();

    private final static int VIEW_TYPE_IMAGE = 0;
    private final static int VIEW_TYPE_COLOR = 1;

    private final static long HOLD_MIN_DURATION = 200;

    private List<Comic> comicList;
    private Activity host;

    private boolean holding = false;
    private ComicThumbHolder Gli_holder;

    private Handler longTouchHandler = new Handler();
    private Runnable longTouchRunner = () -> {
        if (holding) {
            Gli_holder.data.setVisibility(View.VISIBLE);
        }
    };

    ComicAdapter(Activity host, List<Comic> items) {
        this.comicList = items;
        this.host = host;
    }

    @Override
    public int getItemViewType(int position) {
        if (URLUtil.isValidUrl(comicList.get(position).thumbnail)) {
            return VIEW_TYPE_IMAGE;
        } else {
            return VIEW_TYPE_COLOR;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_COLOR:
                return getNewColorHolder(parent);
            case VIEW_TYPE_IMAGE:
                return getNewThumbHolder(parent);
        }
        return null;
    }

    private ComicColorHolder getNewColorHolder(ViewGroup parent) {
        final ComicColorHolder holder = new ComicColorHolder(LayoutInflater.from(host)
                .inflate(R.layout.itemview_color, parent, false));
        holder.bg.setOnClickListener((view) -> onSelect(comicList.get(holder.getAdapterPosition())));
        return holder;
    }

    private ComicThumbHolder getNewThumbHolder(ViewGroup parent) {
        final ComicThumbHolder holder = new ComicThumbHolder(LayoutInflater.from(host)
                .inflate(R.layout.itemview_thumb, parent, false));
        holder.base.setOnClickListener((view) -> onSelect(comicList.get(holder.getAdapterPosition())));
        holder.base.setOnTouchListener((view, event) -> {
            final int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                longTouchHandler.postDelayed(longTouchRunner, HOLD_MIN_DURATION);
                Gli_holder = holder;
                holding = true;
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                longTouchHandler.removeCallbacks(longTouchRunner);
                holding = false;
                if (holder.data.getVisibility() == View.VISIBLE) {
                    holder.data.setVisibility(View.GONE);
                    return true;
                }
            }
            return false;
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_COLOR:
                bindColorHolder((ComicColorHolder) holder, getItem(position));
                break;
            case VIEW_TYPE_IMAGE:
                bindThumbHolder((ComicThumbHolder) holder, getItem(position));
                break;
        }
    }

    private Comic getItem(int position) {
        return comicList.get(position);
    }

    private void bindThumbHolder(ComicThumbHolder holder, Comic comic) {
        Glide.with(host)
                .load(comicList.get(holder.getAdapterPosition()).thumbnail)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.thumb);

        Glide.with(host)
                .load(comic.logo)
                .into(holder.logo);
        holder.data.setText(comicList.get(holder.getAdapterPosition()).title);
        holder.title.setText(comicList.get(holder.getAdapterPosition()).title);
    }

    private void bindColorHolder(ComicColorHolder holder, Comic comic) {
        holder.bg.setBackgroundColor(
                Color.parseColor(comic.thumbnail));
        holder.bg.setText(comic.title);
    }

    @Override
    public int getItemCount() {
        return comicList.size();
    }

    int getDataItemCount() {
        return comicList.size();
    }

    protected abstract void onSelect(Comic comic);

    /**
     * Holder for Comics with thumbnail
     */
    public static class ComicThumbHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumb)
        ImageView thumb;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.logo)
        ImageView logo;
        @BindView(R.id.base)
        FrameLayout base;
        @BindView(R.id.data)
        TextView data;

        ComicThumbHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            data.setOnClickListener((view) -> data.setVisibility(View.GONE));
        }
    }

    /**
     * Holder for comics without thumbnail
     */
    public class ComicColorHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.bg)
        TextView bg;

        ComicColorHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
