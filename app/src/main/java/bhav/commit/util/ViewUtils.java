package bhav.commit.util;

import android.content.res.Resources;

public class ViewUtils {

    private ViewUtils() { }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}
