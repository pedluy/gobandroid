package org.ligi.gobandroid_hd.ui.game_setup;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.GoBoardViewHD;
import org.ligi.gobandroid_hd.ui.GoPrefs;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment;

public class GameSetupFragment extends GobandroidFragment implements OnSeekBarChangeListener {

    public int act_size = 9;
    private int wanted_size;

    public int act_handicap = 0;

    private final static int size_offset = 2;

    @BindView(R.id.size_seek)
    SeekBar size_seek;

    @BindView(R.id.handicap_seek)
    SeekBar handicap_seek;

    @BindView(R.id.game_size_label)
    TextView size_text;

    @BindView(R.id.handicap_label)
    TextView handicap_text;

    @OnClick(R.id.size_button9x9)
    void setSize9x9() {
        setSize(9);
    }

    @OnClick(R.id.size_button13x13)
    void setSize13x13() {
        setSize(13);
    }

    @OnClick(R.id.size_button19x19)
    void setSize19x19() {
        setSize(19);
    }

    private void setSize(int size) {
        wanted_size = size;

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (act_size != wanted_size) {
                    act_size += (act_size > wanted_size) ? -1 : 1;
                    uiHandler.postDelayed(this, 16);
                }

                if (!getActivity().isFinishing()) {
                    refresh_ui();
                }
            }
        });

    }

    private Handler uiHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        uiHandler = new Handler();

        final View view = inflater.inflate(R.layout.game_setup_inner, container, false);

        ButterKnife.bind(this, view);

        size_seek.setOnSeekBarChangeListener(this);
        handicap_seek.setOnSeekBarChangeListener(this);

        // set defaults
        act_size = GoPrefs.INSTANCE.getLastBoardSize();
        act_handicap = GoPrefs.INSTANCE.getLastHandicap();

        refresh_ui();
        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if ((seekBar == size_seek) && (act_size != (byte) (progress + size_offset))) setSize(progress + size_offset);
        else if ((seekBar == handicap_seek) && (act_handicap != (byte) progress)) act_handicap = (byte) progress;

        refresh_ui();
    }

    private boolean isAnimating() {
        return act_size != wanted_size;
    }

    /**
     * refresh the ui elements with values from act_size / act_handicap
     */
    public void refresh_ui() {

        size_text.setText(getString(R.string.size) + " " + act_size + "x" + act_size);

        if (!isAnimating()) {
            // only enable handicap seeker when the size is 9x9 or 13x13 or 19x19
            handicap_seek.setEnabled((act_size == 9) || (act_size == 13) || (act_size == 19));

            if (handicap_seek.isEnabled()) {
                handicap_text.setText(getString(R.string.handicap) + " " + act_handicap);
            } else {
                handicap_text.setText(getString(R.string.handicap_only_for));
            }
        }

        // the checks for change here are important - otherwise samsung moment
        // will die here with stack overflow
        if ((act_size - size_offset) != size_seek.getProgress()) size_seek.setProgress(act_size - size_offset);

        if (act_handicap != handicap_seek.getProgress()) handicap_seek.setProgress(act_handicap);

        if (interactionScope.getMode() == InteractionScope.Mode.GNUGO)
            size_seek.setMax(19 - size_offset);


        GoPrefs.INSTANCE.setLastBoardSize(act_size);
        GoPrefs.INSTANCE.setLastHandicap(act_handicap);

        if (gameProvider.get().getSize() != act_size || gameProvider.get().getHandicap() != act_handicap) {
            gameProvider.set(new GoGame(act_size, act_handicap));
        }

        if (getActivity() instanceof GoActivity) {
            final GoBoardViewHD board = ((GoActivity) getActivity()).getBoard();

            if (board != null) {
                board.regenerateStoneImagesWithNewSize();
                board.invalidate();
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

}
