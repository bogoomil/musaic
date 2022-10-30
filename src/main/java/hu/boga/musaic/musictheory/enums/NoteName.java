package hu.boga.musaic.musictheory.enums;

public enum NoteName {

    C, Cs, D, Eb, E, F, Fs, G, Ab, A, Bb, B;// c2(12),Cs2(13),D2(14)

    public static NoteName byCode(int code) {
        if (code >= 12) {
            code = code % 12;
        }
        return values()[code];
    }
}
