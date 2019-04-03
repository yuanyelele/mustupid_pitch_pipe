package com.mustupid.pitch_pipe;

/**
 * Keeps track of the the 12 chromatic notes and their respective frequencies.
 */
public enum Note {
    C(-9),
    Db(-8),
    D(-7),
    Eb(-6),
    E(-5),
    F(-4),
    Gb(-3),
    G(-2),
    Ab(-1),
    A(0),
    Bb(1),
    B(2);

    private final double frequency;

    Note(int halfStepsAwayFromA) {
        frequency = Math.pow(2.0, halfStepsAwayFromA / 12.0);
    }

    public double getFrequency() {
        return frequency;
    }
}
