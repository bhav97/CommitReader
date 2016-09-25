package purplevomit.commit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * FrameLayout with a custom preset aspect ratio
 */
public class CRFrameLayout extends FrameLayout {

    public CRFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int fourThreeHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) * 492 / 940,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, fourThreeHeight);
    }
}
