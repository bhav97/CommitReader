package purplevomit.commit;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bhav on 9/23/16 for the CommitReader Project.
 */
public abstract class ComicAdapter extends Adapter<ComicAdapter.ComicHolder> {

    private List<Comic> comicList;
    private Activity host;

    public ComicAdapter(Activity host, List<Comic> items) {
        this.comicList = items;
        this.host = host;

    }

    @Override
    public ComicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final ComicHolder holder = new ComicHolder(LayoutInflater.from(host)
                .inflate(R.layout.itemview_thumb, parent, false));
        holder.base.setOnClickListener((view) -> onSelect(comicList.get(holder.getAdapterPosition())));
        holder.base.setOnTouchListener((view, event) -> {
            final int action = event.getAction();
            if (!(action == MotionEvent.ACTION_DOWN
                    || action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_CANCEL)) return false;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    holder.data.setVisibility(View.VISIBLE);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    holder.data.setVisibility(View.GONE);
                    break;
            }
            return false;
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ComicHolder holder, int position) {
        final Comic c = comicList.get(holder.getAdapterPosition());
        Glide.with(host)
                .load(comicList.get(holder.getAdapterPosition()).thumbnail)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.thumb);

        Glide.with(host)
                .load(comicList.get(holder.getAdapterPosition()).logo)
                .into(holder.logo);
        holder.data.setText(comicList.get(holder.getAdapterPosition()).title);
        holder.title.setText(comicList.get(holder.getAdapterPosition()).title);
    }

    @Override
    public int getItemCount() {
        return comicList.size();
    }

    public void insert(List<Comic> data) {
        this.comicList.addAll(data);
        notifyDataSetChanged();
    }

    public int getDataItemCount() {
        return comicList.size();
    }

    public static class ComicHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumb)
        ImageView thumb;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id. logo)
        ImageView logo;
        @BindView(R.id.base)
        FrameLayout base;
        @BindView(R.id.data)
        TextView data;

        public ComicHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            data.setOnClickListener((view) -> data.setVisibility(View.GONE));
        }
    }

    public abstract void onSelect(Comic comic);
}
