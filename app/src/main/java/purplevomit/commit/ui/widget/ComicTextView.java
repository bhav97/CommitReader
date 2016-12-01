package purplevomit.commit.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * TextView with a custom font. :[
 */
public class ComicTextView extends TextView {

    public ComicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/axure.ttf"));
    }
}
