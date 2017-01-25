package bhav.commit.util;

import android.animation.Animator;
import android.view.animation.Animation;

public class EndListener {
    public static abstract class AnimatorEndListener implements Animator.AnimatorListener {
        @Override
        public final void onAnimationStart(Animator animation) {
        } //no-op

        @Override
        public final void onAnimationCancel(Animator animation) {
        } //no-op

        @Override
        public final void onAnimationRepeat(Animator animation) {
        } //no-op
    }

    public static abstract class AnimationEndListener implements Animation.AnimationListener {
        @Override
        public final void onAnimationStart(Animation animation) {} //no-op

        @Override
        public final void onAnimationRepeat(Animation animation) {} //no-op
    }
}
