package purplevomit.commit;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * TextView with a custom preset aspect ratio and custom font.
 */
//todo: make generic ?
public class ComicTextView extends TextView {

    public ComicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/axure.ttf"));
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int fourThreeHeight = View.MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) * 492 / 940,
//                MeasureSpec.EXACTLY);
//        super.onMeasure(widthMeasureSpec, fourThreeHeight);
//    }
}
