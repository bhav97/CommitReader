package bhav.commit.ui.recyclerview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import bhav.commit.util.ViewUtils;

public abstract class SwipeCallback extends ItemTouchHelper.SimpleCallback {


    private Paint p;
    Bitmap icon;

    public SwipeCallback(int dragDirs, int swipeDirs, Typeface typeface) {
        super(dragDirs, swipeDirs);
        p = new Paint();
        p.setTextSize(ViewUtils.dpToPx(28));
        p.setTypeface(typeface);
//        p.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/axure.ttf"));
//        this.icon = icon;
    }

    @Override
    public boolean onMove(
            RecyclerView rv,
            RecyclerView.ViewHolder vh,
            RecyclerView.ViewHolder tg
    ) {
        return false;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        if (dX > 0) {
            /* Set your color for positive displacement */
            p.setColor(Color.GRAY);

//            c.drawBitmap(icon,
//                    (float) itemView.getLeft() + ViewUtils.dpToPx(16),
//                    (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
//                    p);
            Rect bounds = new Rect();
            p.getTextBounds("Swipe Card to Download", 0, 21, bounds);
            int height = bounds.height();
            int width = bounds.width();
            c.drawText("Swipe Card to Download",
                    (float) (itemView.getLeft() + itemView.getRight() - width) / 2,
                    (float) (itemView.getTop() + itemView.getBottom() + height) / 2,
                    p);
            // Draw Rect with varying right side, equal to displacement dX
//            c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
//                    (float) itemView.getBottom(), p);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}