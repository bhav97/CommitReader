package bhav.commit.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * ImageView with a custom preset aspect ratio (16:9).
 */
public class ComicImageView extends ForegroundImageView {

    public ComicImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int fourThreeHeight = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec) * 9 / 16,
                View.MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, fourThreeHeight);
    }
}
