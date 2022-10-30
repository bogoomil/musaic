package hu.boga.musaic.musictheory.enums;

import java.util.Optional;

public enum NoteLength {
    NEGYSZERES(128), HAROMSZOROS(96), DUPLA(64), EGESZ(32), HAROMNEGYED(24), FEL(16), NEGYED(8), NYOLCAD(4), TIZENHATOD(2), HARMICKETTED(1);

    int ertek;

    private NoteLength(int ertek) {
        this.ertek = ertek;
    }

    public int getErtek() {
        return this.ertek;
    }

    public static Optional<NoteLength> ofErtek(int ertek) {
        for (NoteLength nl : NoteLength.values()) {
            if (nl.getErtek() == ertek) {
                return Optional.of(nl);
            }
        }
        return Optional.empty();
    }

    public NoteLength next() {
        return (ordinal() > 0) ? NoteLength.values()[ordinal() - 1] : NoteLength.HARMICKETTED;
    }

    public NoteLength prev() {
        return (ordinal() < values().length - 1) ? NoteLength.values()[ordinal() + 1] : NoteLength.values()[0];
    }

}
