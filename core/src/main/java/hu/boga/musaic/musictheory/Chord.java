package hu.boga.musaic.musictheory;



import hu.boga.musaic.musictheory.enums.ChordType;
import hu.boga.musaic.musictheory.enums.NoteName;
import hu.boga.musaic.musictheory.enums.Tone;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import java.util.*;

public class Chord {

    // **Augmented**: Caug (root, major 3rd, augmented 5th)
    private static Chord augmented(Pitch root) {
        return new Chord(ChordType.AUG, root, root.majorThird(), root.augmentedFifth());
    }

    // **Augmented Seventh**: Caug7 (root, major 3rd, augmented 5th, minor 7th)
    private static Chord augmentedSeventh(Pitch root) {
        return new Chord(ChordType.AUG7, root, root.majorThird(), root.augmentedFifth(), root.minorSeventh());
    }

    // **Diminished**: Cdim (root, minor 3rd, diminished 5th)
    private static Chord diminished(Pitch root) {
        return new Chord(ChordType.DIM, root, root.minorThird(), root.diminishedFifth());
    }

    // **Diminished Seventh**: Cdim7 (root, minor 3rd, diminished 5th,
    // diminished 7th)
    private static Chord diminished7th(Pitch root) {
        return new Chord(ChordType.DIM7, root, root.minorThird(), root.diminishedFifth(), root.diminishedSeventh());
    }

    // **Dominant Seventh**: Cdom7 (root, major 3rd, perfect 5th, minor 7th)
    private static Chord dominantSeventh(Pitch root) {
        return new Chord(ChordType.DOM7, root, root.majorThird(), root.perfectFifth(), root.minorSeventh());
    }

    public static Chord getChordInversion(Chord ch) {

        Pitch[] pitches = new Pitch[ch.getPitches().length];
        for (int i = 0; i < ch.getPitches().length; i++) {
            pitches[i] = new Pitch(ch.getPitches()[i].getMidiCode());
        }
        pitches[0] = pitches[0].shift(1);
        return new Chord(ch.getChordType(), pitches);
    }

    public static Optional<Chord> getChordCounterPart(Chord c) {
        if (c.getChordType() == ChordType.MAJ) {
            return Optional.of(getChord(new Pitch(c.pitches[0].getMidiCode() + 9), ChordType.MIN));
        } else if (c.getChordType() == ChordType.MIN) {
            return Optional.of(getChord(new Pitch(c.pitches[0].getMidiCode() - 9), ChordType.MAJ));
        }
        return Optional.empty();

    }

    public static Chord getChord(Pitch root, ChordType chordType) {
        switch (chordType) {
        case MAJ:
            return major(root);
        case MIN:
            return minor(root);
        case AUG:
            return augmented(root);
        case DIM:
            return diminished(root);
        case DIM7:
            return diminished7th(root);
        case MAJ7B5:
            return majorSeventhFlatF3e(root);
        case MIN7:
            return minorSeventh(root);
        case MINMAJ7:
            return minorMajorSeventh(root);
        case DOM7:
            return dominantSeventh(root);
        case MAJ7:
            return majorSeventh(root);
        case AUG7:
            return augmentedSeventh(root);
        case MAJ7S5:
            return majorSeventhSharpF3e(root);
        case MAJ6:
            return majorSixth(root);
        case MIN6:
            return minorSixth(root);
        case MAJ9:
            return majorNinth(root);
        case MIN9:
            return minorNinth(root);
        case DOM9:
            return dominantNinth(root);
        }

        throw new UnsupportedOperationException("pitch: " + root + ", type: " + chordType.name());
    }

    // Triads
    // ------
    //
    // Three note chords. These consists of the root, a third and a fifth.
    // The third and fifth intervals are shifted slightly to vary the chord's
    // sound.

    private static final Map<Tone, ChordType[]> CHORD_TYPES = new EnumMap<>(Tone.class);

    static {
        CHORD_TYPES.put(Tone.MAJ, new ChordType[] { ChordType.MAJ, ChordType.MIN, ChordType.MIN, ChordType.MAJ, ChordType.MAJ, ChordType.MIN, ChordType.DIM });
        CHORD_TYPES.put(Tone.MIN, new ChordType[] { ChordType.MIN, ChordType.DIM, ChordType.MAJ, ChordType.MIN, ChordType.MIN, ChordType.MAJ, ChordType.MAJ });
        CHORD_TYPES.put(Tone.LYDIAN, new ChordType[] { ChordType.MAJ, ChordType.MAJ, ChordType.MIN, ChordType.DIM, ChordType.MAJ, ChordType.MIN, ChordType.MIN });
        CHORD_TYPES.put(Tone.MIXOLYDIAN, new ChordType[] { ChordType.MAJ, ChordType.MIN, ChordType.DIM, ChordType.MAJ, ChordType.MAJ, ChordType.MIN, ChordType.MAJ });
        CHORD_TYPES.put(Tone.DORIAN, new ChordType[] { ChordType.MIN, ChordType.MIN, ChordType.MAJ, ChordType.MAJ, ChordType.MIN, ChordType.DIM, ChordType.MAJ });
        CHORD_TYPES.put(Tone.PHRYGIAN, new ChordType[] { ChordType.MIN, ChordType.MAJ, ChordType.MAJ, ChordType.MIN, ChordType.DIM, ChordType.MAJ, ChordType.MIN });
        CHORD_TYPES.put(Tone.LOCRIAN, new ChordType[] { ChordType.DIM, ChordType.MAJ, ChordType.MIN, ChordType.MIN, ChordType.MAJ, ChordType.MAJ, ChordType.MIN });
    }

    public static Chord getChordDegree(NoteName scaleRoot, int octave, Tone tone, int degree) {
        Pitch p = new Pitch(scaleRoot.ordinal() + (octave * 12));
        return getChord(p, CHORD_TYPES.get(tone)[degree]);
    }

    public static List<Chord> getChordProgressions(ChordType chordType, int currentDegree, Pitch root) {
        List<Chord> retVal = new ArrayList<>();
        switch (chordType) {
        case MAJ: {
            switch (currentDegree) {
            case 0: {
                retVal.add(getChord(root, ChordType.MAJ));
                retVal.add(getChord(root, ChordType.MAJ6));
                retVal.add(getChord(root, ChordType.MAJ7));
                retVal.add(getChord(root, ChordType.MAJ9));
                break;
            }
            case 1: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 2), ChordType.MIN));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 2), ChordType.MIN7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 2), ChordType.MIN9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MAJ6));
                break;
            }
            case 2: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 4), ChordType.MIN));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 4), ChordType.MIN7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 4), ChordType.MIN9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 4), ChordType.MAJ6));
                break;
            }
            case 3: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MAJ));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MAJ6));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MAJ7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MAJ9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 2), ChordType.MIN7));
                break;
            }
            case 4: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 7), ChordType.MAJ));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 7), ChordType.DOM7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 7), ChordType.DOM9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 7), ChordType.MAJ7S5));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 1), ChordType.DOM7));
                break;
            }
            case 5: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 9), ChordType.MIN));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 9), ChordType.MIN7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 9), ChordType.MIN9));
                retVal.add(getChord(new Pitch(root.getMidiCode()), ChordType.MAJ6));
                break;
            }
            case 6: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 11), ChordType.DIM));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 11), ChordType.MAJ7B5));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 11), ChordType.MAJ9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 2), ChordType.MIN6));
                break;
            }
            }
            break;
        }
        case MIN: {
            switch (currentDegree) {
            case 0: {
                retVal.add(getChord(new Pitch(root.getMidiCode()), ChordType.MIN));
                retVal.add(getChord(new Pitch(root.getMidiCode()), ChordType.MIN6));
                retVal.add(getChord(new Pitch(root.getMidiCode()), ChordType.MIN7));
                retVal.add(getChord(new Pitch(root.getMidiCode()), ChordType.MIN9));
                break;
            }
            case 1: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 2), ChordType.DIM));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 2), ChordType.MAJ7B5));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MIN6));
                break;
            }
            case 2: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 3), ChordType.MAJ));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 3), ChordType.MAJ7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 3), ChordType.MAJ6));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 3), ChordType.MAJ7S5));
                break;
            }
            case 3: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MIN));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MIN7));
                break;
            }
            case 4: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 7), ChordType.MAJ));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 7), ChordType.DOM7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 7), ChordType.DOM9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 1), ChordType.DOM7));
                break;
            }
            case 5: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 8), ChordType.MAJ));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 8), ChordType.MAJ6));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 8), ChordType.MAJ7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 8), ChordType.MAJ9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 9), ChordType.DIM));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 9), ChordType.MAJ7B5));
                break;
            }
            case 6: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 10), ChordType.MAJ));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 10), ChordType.MAJ6));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 10), ChordType.MAJ7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 10), ChordType.MAJ9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 11), ChordType.DIM));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 11), ChordType.MAJ7B5));
                break;
            }
            }
        }
        }
        return retVal;

    }

    public static List<Chord> getChordsOfPitch(Pitch p) {
        List<Chord> retVal = new ArrayList<>();
        for (ChordType t : Arrays.asList(ChordType.values())) {
            retVal.add(getChord(p, t));
        }
        return retVal;
    }

    public static List<Chord> getChordsOfPitchInScale(Pitch p, NoteName scaleRoot, Tone scaleTone) {
        List<NoteName> nns = Scale.getScale(scaleRoot, scaleTone);
        List<Chord> retVal = getChordsOfPitch(p);
        retVal.removeIf(ch -> !ch.isInScale(nns));
        return retVal;
    }

    private boolean isInScale(List<NoteName> nns) {
        for (int i = 0; i < this.pitches.length; i++) {
            if (!nns.contains(this.pitches[i].getName())) {
                return false;
            }
        }
        return true;
    }

    public static List<Integer> getPossibleDegrees(Integer currentDegree, ChordType type) {
        List<Integer> retVal = new ArrayList<>();
        switch (type) {
        case MAJ: {
            switch (currentDegree) {
            case 0: {
                retVal.add(3);
                retVal.add(4);
                retVal.add(0);
                retVal.add(1);
                retVal.add(2);
                retVal.add(3);
                retVal.add(4);
                retVal.add(5);
                retVal.add(6);

                break;
            }
            case 1: {
                retVal.add(3);
                retVal.add(4);
                retVal.add(6);
                break;
            }
            case 2: {
                retVal.add(1);
                retVal.add(5);
                break;
            }
            case 3: {
                retVal.add(0);
                retVal.add(4);
                retVal.add(6);
                break;
            }
            case 4: {
                retVal.add(0);
                retVal.add(3);
                retVal.add(5);
                break;
            }
            case 5: {
                retVal.add(1);
                retVal.add(3);
                retVal.add(4);
                break;
            }
            case 6: {
                retVal.add(0);
                retVal.add(2);
                break;
            }
            }
            break;
        }
        case MIN: {
            switch (currentDegree) {
            case 0: {
                retVal.add(0);
                retVal.add(1);
                retVal.add(2);
                retVal.add(3);
                retVal.add(4);
                retVal.add(5);
                retVal.add(6);
                break;
            }
            case 1: {
                retVal.add(4);
                retVal.add(6);
                break;
            }
            case 2: {
                retVal.add(5);
                retVal.add(6);
                break;
            }
            case 3: {
                retVal.add(1);
                retVal.add(4);
                retVal.add(6);
                break;
            }
            case 4: {
                retVal.add(0);
                break;
            }
            case 5: {
                retVal.add(1);
                retVal.add(3);
                retVal.add(4);
                retVal.add(6);
                break;
            }
            case 6: {
                retVal.add(0);
                retVal.add(4);
                break;
            }
            }
            break;
        }
        }
        retVal.add(currentDegree);
        return retVal;

    }

    // **Major**: Cmaj or C (root, major 3rd, perfect 5th)
    private static Chord major(Pitch root) {
        return new Chord(ChordType.MAJ, root, root.majorThird(), root.perfectFifth());
    }

    // **Major Seventh**: Cmaj7 (root, major 3rd, perfect 5th, major 7th)
    private static Chord majorSeventh(Pitch root) {
        return new Chord(ChordType.MAJ7, root, root.majorThird(), root.perfectFifth(), root.majorSeventh());
    }

    // **Major Ninth**: Cmaj9 (root, minor 3rd, perfect 5th, major 7th, major
    // 9th)
    private static Chord majorNinth(Pitch root) {
        return new Chord(ChordType.MAJ9, root, root.majorThird(), root.perfectFifth(), root.majorSeventh(), root.majorNinth());
    }

    // **minor Ninth**: Cmaj9 (root, minor 3rd, perfect 5th, minor 7th, minor
    // 9th)
    private static Chord minorNinth(Pitch root) {// 1, ♭3, 5, ♭7, 9
        return new Chord(ChordType.MIN9, root, root.minorThird(), root.perfectFifth(), root.minorSeventh(), root.majorNinth());
    }

    // **Major Seventh Flat F3e**: Cmaj7b5 (root, minor 3rd, diminished 5th,
    // minor 7th)
    private static Chord majorSeventhFlatF3e(Pitch root) {
        return new Chord(ChordType.MAJ7B5, root, root.minorThird(), root.diminishedFifth(), root.minorSeventh());
    }

    // Sixths
    // ------
    //
    // Triads with an added fourth note that is a sixth interval
    // above the root.

    // **Augmented Major Seventh**: Cmaj7s5 (root, major 3rd, augmented 5th,
    // major 7th)
    private static Chord majorSeventhSharpF3e(Pitch root) {
        return new Chord(ChordType.MAJ7S5, root, root.majorThird(), root.augmentedFifth(), root.majorSeventh());
    }

    // **Major Sixth**: Cmaj6 (root, major 3rd, perfect 5th, major 6th)
    private static Chord majorSixth(Pitch root) {
        return new Chord(ChordType.MAJ6, root, root.majorThird(), root.perfectFifth(), root.majorSixth());
    }

    // Sevenths
    // --------
    //
    // Triads with an added fourth note that is a seventh interval
    // above the root.

    // **Minor**: Cmin (root, minor 3rd, perfect 5th)
    private static Chord minor(Pitch root) {
        return new Chord(ChordType.MIN, root, root.minorThird(), root.perfectFifth());
    }

    // **Minor Major Seventh**: Cminmaj7 (root, minor 3rd, perfect 5th, major
    // 7th)
    private static Chord minorMajorSeventh(Pitch root) {
        return new Chord(ChordType.MINMAJ7, root, root.minorThird(), root.perfectFifth(), root.majorSeventh());
    }

    // **Minor Seventh**: Cmin7 (root, minor 3rd, perfect 5th, minor 7th)
    private static Chord minorSeventh(Pitch root) {
        return new Chord(ChordType.MIN7, root, root.minorThird(), root.perfectFifth(), root.minorSeventh());
    }

    // **DOM Ninth**: Cmaj9 (root, minor 3rd, perfect 5th, minor 7th, major 9th)
    private static Chord dominantNinth(Pitch root) {
        return new Chord(ChordType.DOM9, root, root.majorThird(), root.perfectFifth(), root.minorSeventh(), root.majorNinth());
    }

    // **Minor Sixth**: Cmin6 (root, minor 3rd, perfect 5th, major 6th)
    private static Chord minorSixth(Pitch root) {
        return new Chord(ChordType.MIN6, root, root.minorThird(), root.perfectFifth(), root.majorSixth());
    }

    private ChordType chordType;

    private Pitch[] pitches;

    private Chord(ChordType chordType, Pitch... pitches) {
        this.pitches = pitches;
        this.chordType = chordType;
    }

    public ChordType getChordType() {
        return chordType;
    }

    public Pitch[] getPitches() {
        return this.pitches;
    }

    public Collection<ShortMessage> noteOff(int channel, int velocity) throws InvalidMidiDataException {
        Collection<ShortMessage> messages = new ArrayList<>(pitches.length);
        for (Pitch p : pitches) {
            messages.add(new ShortMessage(ShortMessage.NOTE_OFF, channel, p.getMidiCode(), velocity));
        }
        return messages;
    }

    public Collection<ShortMessage> noteOn(int channel, int velocity) throws InvalidMidiDataException {
        Collection<ShortMessage> messages = new ArrayList<>(pitches.length);
        for (Pitch p : pitches) {
            messages.add(new ShortMessage(ShortMessage.NOTE_ON, channel, p.getMidiCode(), velocity));
        }
        return messages;
    }

    public Chord shiftOctave(int octaveShift) {
        Pitch[] p = new Pitch[this.pitches.length];
        for (int i = 0; i < this.pitches.length; i++) {
            p[i] = this.pitches[i].shift(octaveShift);
        }
        return new Chord(this.getChordType(), p);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        try {
            NoteName nn = NoteName.byCode(this.pitches[0].getMidiCode());
            sb.append(nn.name()).append(" ").append(this.chordType.name()).append(" [");
            for (int i = 0; i < this.pitches.length; i++) {

                sb.append(NoteName.byCode(this.pitches[i].getMidiCode())).append(" (" + this.pitches[i].getMidiCode() + "), ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((chordType == null) ? 0 : chordType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Chord other = (Chord) obj;
        if (chordType != other.chordType || !this.getNoteName().equals(other.getNoteName())) {
            return false;
        }
        return true;
    }

    public NoteName getNoteName() {
        return this.pitches[0].getName();
    }

}
