package spotifycharts;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SongComparatorStability {

    private static Comparator<Song>  rankingSchemeTotal, rankingSchemeDutchNational;

    Song songBYC, songKKA, songTS, songJVT, songBB;

    List<Song> songList = new ArrayList<>();

    @BeforeAll
    static void setupClass() {
        rankingSchemeTotal = Song::compareByHighestStreamsCountTotal;
        rankingSchemeDutchNational = Song::compareForDutchNationalChart;
    }

    @BeforeEach
    void setup() {
        songBYC = new Song("Beyoncé", "CUFF IT", Song.Language.EN);
        songBYC.setStreamsCountOfCountry(Song.Country.UK,100);
        songBYC.setStreamsCountOfCountry(Song.Country.NL,40);
        songBYC.setStreamsCountOfCountry(Song.Country.BE,20);
        songList.add(songBYC);
        songTS = new Song("Taylor Swift", "Anti-Hero", Song.Language.EN);
        songTS.setStreamsCountOfCountry(Song.Country.UK,100);
        songTS.setStreamsCountOfCountry(Song.Country.DE,60);
        songList.add(songTS);
        songKKA = new Song("Kris Kross Amsterdam", "Vluchtstrook", Song.Language.NL);
        songKKA.setStreamsCountOfCountry(Song.Country.NL,40);
        songKKA.setStreamsCountOfCountry(Song.Country.BE,30);
        songList.add(songKKA);
        songJVT = new Song("De Jeugd Van Tegenwoordig", "Sterrenstof", Song.Language.NL);
        songJVT.setStreamsCountOfCountry(Song.Country.NL,70);
        songList.add(songJVT);
        songBB = new Song("Bad Bunny", "La Coriente", Song.Language.SP);
        songList.add(songBB);
    }

    @Test
    public void testRankingSchemeTotalCompare(){
        //write a test that compares songs with each other and return 0

        for (Song song: songList) {
            assertEquals(0, rankingSchemeTotal.compare(song, song),
                    "Songs are not equal");
            assertEquals(rankingSchemeTotal.compare(song, songBYC), -rankingSchemeTotal.compare(songBYC, song),
                    "Songs are not equal");
        }
    }

    @Test
    public void testRankingSchemeDutchNationalCompare(){
        //write a test that compares songs with each other and return 0

        for (Song song: songList) {
            assertEquals(0, rankingSchemeDutchNational.compare(song, song),
                    "Songs are not equal");
            assertEquals(rankingSchemeDutchNational.compare(song, songBYC), -rankingSchemeDutchNational.compare(songBYC, song),
                    "Songs are not equal");
        }
    }


}
