package purplevomit.commit;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;

/**
 * Created by bhav on 9/24/16 for the CommitReader Project.
 */
public class ComicSheet extends BottomSheetDialogFragment {

    public ComicSheet() {

    }

    private BottomSheetBehavior.BottomSheetCallback sheetCallback =
            new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        dismiss();
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    //animate arrow icon
                }
            };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View content = View.inflate(getContext(), R.layout.sheet_comic, null);

        dialog.setContentView(content);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) content.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(sheetCallback);
        }

    }
}
