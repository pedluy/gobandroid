package org.ligi.gobandroid_hd.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import javax.inject.Inject;
import org.ligi.axt.listeners.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.model.GameProvider;
import org.ligi.gobandroid_hd.ui.application.GoAndroidEnvironment;

/**
 * A styled Dialog fit in the gobandroid style
 */
public class GobandroidDialog extends Dialog {

    private final LayoutInflater inflater;
    private final LinearLayout button_container;

    private Button positive_btn;
    private Button negative_btn;

    @Inject
    protected GoAndroidEnvironment settings;

    @Inject
    protected GameProvider gameProvider;

    @Inject
    protected InteractionScope interactionScope;

    public GobandroidDialog(Context context) {
        super(context, R.style.dialog_theme);

        App.component().inject(this);
        inflater = LayoutInflater.from(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(R.layout.dialog_gobandroid);

        // this sounds misleading but behaves right - we just do not want to  start with keyboard open
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        button_container = (LinearLayout) findViewById(R.id.button_container);
    }

    public void setIconResource(@DrawableRes int icon) {
        ((TextView) findViewById(R.id.dialog_title)).setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
    }

    @Override
    public void setContentView(@LayoutRes int content) {
        final LinearLayout container = (LinearLayout) findViewById(R.id.dialog_content);
        container.addView(inflater.inflate(content, container, false));
    }


    @Override
    public void setTitle(CharSequence title) {
        ((TextView) this.findViewById(R.id.dialog_title)).setText(title);
    }

    class DialogOnClickWrapper implements View.OnClickListener {

        private final DialogInterface.OnClickListener listener;

        public DialogOnClickWrapper(DialogInterface.OnClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onClick(GobandroidDialog.this, 0);
        }

    }

    public void setPositiveButton(@StringRes int text) {
        getPositiveButton().setText(text);
        getPositiveButton().setOnClickListener(new DialogOnClickWrapper(new DialogDiscardingOnClickListener()));

    }

    public void setPositiveButton(@StringRes int text, DialogInterface.OnClickListener listener) {
        getPositiveButton().setText(text);
        getPositiveButton().setOnClickListener(new DialogOnClickWrapper(listener));
    }

    public Button getPositiveButton() {
        if (positive_btn == null) {
            button_container.addView(positive_btn = createButton());
        }
        return positive_btn;
    }

    public void setNegativeButton(@StringRes int text) {
        getNegativeButton().setText(text);
        getNegativeButton().setOnClickListener(new DialogOnClickWrapper(new DialogDiscardingOnClickListener()));
    }

    public void setNegativeButton(@StringRes int text, DialogInterface.OnClickListener listener) {
        getNegativeButton().setText(text);
        getNegativeButton().setOnClickListener(new DialogOnClickWrapper(listener));
    }

    private Button getNegativeButton() {
        if (negative_btn == null) {
            button_container.addView(negative_btn = createButton());
        }
        return negative_btn;
    }

    private Button createButton() {
        final Button res = new Button(getContext());
        final LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
        res.setLayoutParams(lp);
        return res;
    }

}
