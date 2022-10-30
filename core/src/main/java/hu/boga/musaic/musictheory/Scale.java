package hu.boga.musaic.musictheory;


import hu.boga.musaic.musictheory.enums.NoteName;
import hu.boga.musaic.musictheory.enums.Tone;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Scale {

    private Scale() {

    }

    private static final Map<Tone, NoteName[]> codes = new EnumMap<>(Tone.class);

    static {
        codes.put(Tone.MAJ, new NoteName[] { NoteName.C,
                NoteName.D,
                NoteName.E,
                NoteName.F,
                NoteName.G,
                NoteName.A,
                NoteName.B
        });
        codes.put(Tone.MIN, new NoteName[] { NoteName.C,
                NoteName.D,
                NoteName.Eb,
                NoteName.F,
                NoteName.G,
                NoteName.Ab,
                NoteName.Bb
        });
        codes.put(Tone.LYDIAN, new NoteName[] { NoteName.C,
                NoteName.D,
                NoteName.E,
                NoteName.Fs,
                NoteName.G,
                NoteName.A,
                NoteName.B
        });
        codes.put(Tone.MIXOLYDIAN, new NoteName[] { NoteName.C,
                NoteName.D,
                NoteName.E,
                NoteName.F,
                NoteName.G,
                NoteName.A,
                NoteName.Bb
        });
        codes.put(Tone.DORIAN, new NoteName[] { NoteName.C,
                NoteName.D,
                NoteName.Eb,
                NoteName.F,
                NoteName.G,
                NoteName.A,
                NoteName.Bb
        });
        codes.put(Tone.PHRYGIAN, new NoteName[] { NoteName.C,
                NoteName.Cs,
                NoteName.Eb,
                NoteName.F,
                NoteName.G,
                NoteName.Ab,
                NoteName.Bb
        });
        codes.put(Tone.LOCRIAN, new NoteName[] { NoteName.C,
                NoteName.Cs,
                NoteName.Eb,
                NoteName.F,
                NoteName.Fs,
                NoteName.Ab,
                NoteName.Bb
        });
    }

    public static List<NoteName> getScale(NoteName root, Tone hangnem) {
        NoteName[] nns = codes.get(hangnem);
        List<NoteName> retVal = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            retVal.add(NoteName.byCode(nns[i].ordinal() + root.ordinal()));
        }
        return retVal;

    }

}
