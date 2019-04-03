package com.mustupid.pitch_pipe;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class PitchPipe {

    private double mFrequency;
    private boolean mTouching;
    final private ExecutorService mExecutor;
    private boolean mRunning;

    PitchPipe() {
        mExecutor = Executors.newSingleThreadExecutor();
    }

    boolean play(double frequency) {
        if (mRunning)
            return false;
        mFrequency = frequency;
        mTouching = true;
        PitchGenerator pitchGenerator = new PitchGenerator();
        mExecutor.execute(pitchGenerator);
        return true;
    }

    void stop() {
        mTouching = false;
    }

    private class PitchGenerator implements Runnable {

        private static final int SAMPLE_RATE = 48000;
        private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
        private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
        private static final double VOLUME = 0.8;
        private final int BUFFER_SIZE;
        private final AudioTrack mTrack;
        private double mAngle;

        PitchGenerator() {
            BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, ENCODING);
            mTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL_CONFIG,
                    ENCODING, BUFFER_SIZE, AudioTrack.MODE_STREAM);
        }

        private void writeSamples(short[] samples, double fromVolume, double toVolume)
        {
            double increment = 2 * Math.PI * mFrequency / SAMPLE_RATE;
            for (int i = 0; i < samples.length; i++) {
                samples[i] = (short) (Math.sin(mAngle) * (fromVolume + (toVolume - fromVolume) * i / samples.length) * Short.MAX_VALUE);
                mAngle += increment;
            }
            mTrack.write(samples, 0, samples.length);
        }

        public void run() {
            mRunning = true;
            mTrack.play();

            short[] samples = new short[BUFFER_SIZE];

            // fade in
            writeSamples(samples, 0, VOLUME);

            while (mTouching) {
                writeSamples(samples, VOLUME, VOLUME);
            }

            // fade out
            writeSamples(samples, VOLUME, 0);
            writeSamples(samples, 0, 0);

            mTrack.stop();
            mTrack.release();
            mRunning = false;
        }
    }
}
