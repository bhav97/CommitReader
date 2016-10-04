package purplevomit.commit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * ImageView with a custom preset aspect ratio.
 */
//todo: make generic?
public class ComicImageView extends ImageView {

    public ComicImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int fourThreeHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) * 9 / 16,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, fourThreeHeight);
    }
}
