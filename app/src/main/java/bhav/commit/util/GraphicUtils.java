package bhav.commit.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class GraphicUtils {

    private static final String TAG = GraphicUtils.class.getSimpleName();

    public static Bitmap ChangeIconColor(Bitmap b, Context context) {
        int[] pixels = new int[b.getHeight()*b.getWidth()];
        b.getPixels(pixels, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
        for (int i=0; i<b.getWidth()*5; i++) {
            if(pixels[i] != ContextCompat.getColor(context, android.R.color.white)) {
                pixels[i] = ContextCompat.getColor(context, android.R.color.holo_red_light);
            }
        }
        b.setPixels(pixels, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
        Log.d(TAG, "ChangedIconColor");
        return b;
    }

    public static Drawable rotate(int degree, Drawable originalDrawable, Context context) {
        if(originalDrawable==null) return null;
        Bitmap iconBitmap = getBitmap(((VectorDrawable)originalDrawable));

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap targetBitmap = Bitmap.createBitmap(
                iconBitmap,
                0,
                0,
                iconBitmap.getWidth(),
                iconBitmap.getHeight(),
                matrix,
                true
        );

        return new BitmapDrawable(context.getResources(), targetBitmap);
    }

    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
}
