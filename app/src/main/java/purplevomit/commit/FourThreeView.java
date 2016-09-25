package purplevomit.commit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by bhav on 9/24/16 for the CommitReader Project.
 */
public class FourThreeView extends View {

    public FourThreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int fourThreeHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) * 3 / 4,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, fourThreeHeight);
    }
}
