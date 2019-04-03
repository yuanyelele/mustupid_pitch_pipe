package com.mustupid.pitch_pipe;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class PitchPipeActivity extends AppCompatActivity {

    private static final int MIN_PITCH = 400;
    private static final int MAX_PITCH = 470;
    private static final int DEFAULT_PITCH = 440;
    private static final int NUM_NOTES = 12;
    private static final SparseArray<Note> ID_TO_NOTE = new SparseArray<>();

    static {
        ID_TO_NOTE.put(R.id.a_button, Note.A);
        ID_TO_NOTE.put(R.id.b_flat_button, Note.Bb);
        ID_TO_NOTE.put(R.id.b_button, Note.B);
        ID_TO_NOTE.put(R.id.c_button, Note.C);
        ID_TO_NOTE.put(R.id.c_sharp_button, Note.Db);
        ID_TO_NOTE.put(R.id.d_button, Note.D);
        ID_TO_NOTE.put(R.id.e_flat_button, Note.Eb);
        ID_TO_NOTE.put(R.id.e_button, Note.E);
        ID_TO_NOTE.put(R.id.f_button, Note.F);
        ID_TO_NOTE.put(R.id.f_sharp_button, Note.Gb);
        ID_TO_NOTE.put(R.id.g_button, Note.G);
        ID_TO_NOTE.put(R.id.a_flat_button, Note.Ab);
    }

    private final Button[] mNoteButtons = new Button[NUM_NOTES];
    private int mPitch = DEFAULT_PITCH;
    private SharedPreferences mPreferences;
    private PitchPipe mPitchPipe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitch_pipe);

        // Make volume button always control just the media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Load stored persistent data
        mPreferences = getSharedPreferences("PitchPipe", AppCompatActivity.MODE_PRIVATE);
        mPitch = mPreferences.getInt("pitch", DEFAULT_PITCH);

        mPitchPipe = new PitchPipe();
        setUpNoteButtons();
        setUpPitchPicker();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("pitch", mPitch);
        editor.apply();
    }

    @SuppressLint("ClickableViewAccessibility")
    /* TODO */
    private void setUpNoteButtons() {
        for (int i = 0; i < NUM_NOTES; i++) {
            final int id = ID_TO_NOTE.keyAt(i);
            mNoteButtons[i] = findViewById(id);
            mNoteButtons[i].setOnTouchListener(new Button.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            return !mPitchPipe.play(ID_TO_NOTE.get(id).getFrequency() * mPitch);
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            mPitchPipe.stop();
                            return false;
                        default:
                            return false;
                    }
                }
            });
        }
    }

    private void setUpPitchPicker() {
        NumberPicker pitchPicker = findViewById(R.id.pitch_picker);
        pitchPicker.setMaxValue(MAX_PITCH - MIN_PITCH);
        String[] string_array = new String[MAX_PITCH - MIN_PITCH + 1];
        for (int i = 0; i <= MAX_PITCH - MIN_PITCH; i++) {
            string_array[i] = Integer.toString(MIN_PITCH + i);
        }
        pitchPicker.setDisplayedValues(string_array);
        pitchPicker.setWrapSelectorWheel(false);
        pitchPicker.setValue(mPitch - MIN_PITCH);
        pitchPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mPitch = newVal + MIN_PITCH;
            }
        });
    }
}
