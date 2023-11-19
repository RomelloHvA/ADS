package spotifycharts;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;

public class Song implements Cloneable {

    public enum Language {
        EN, // English
        NL, // Dutch
        DE, // German
        FR, // French
        SP, // Spanish
        IT, // Italian
    }

    public enum Country {
        UK, // United Kingdom
        NL, // Netherlands
        DE, // Germany
        BE, // Belgium
        FR, // France
        SP, // Spain
        IT  // Italy
    }

    private final String artist;
    private final String title;
    private final Language language;

    private final EnumMap<Country, Integer> streamsCountOfCountry;

    /**
     * Constructs a new instance of Song based on given attribute values
     */
    public Song(String artist, String title, Language language) {
        this.artist = artist;
        this.title = title;
        this.language = language;

        streamsCountOfCountry = new EnumMap<>(Country.class);
        for (Country country : Country.values()) {
            streamsCountOfCountry.put(country, 0);
        }
    }

    /**
     * Sets the given streams count for the given country on this song
     * @param country
     * @param streamsCount
     */
    public void setStreamsCountOfCountry(Country country, int streamsCount) {
        streamsCountOfCountry.put(country, streamsCount);
    }

    /**
     * retrieves the streams count of a given country from this song
     * @param country
     * @return
     */
    public int getStreamsCountOfCountry(Country country) {
        return streamsCountOfCountry.get(country);
    }
    /**
     * Calculates/retrieves the total of all streams counts across all countries from this song
     * @return
     */
    public int getStreamsCountTotal() {
        int totalStreams = 0;

        for (Country country: Country.values()) {
            totalStreams += getStreamsCountOfCountry(country);
        }

        return totalStreams;
    }


    /**
     * compares this song with the other song
     * ordening songs with the highest total number of streams upfront
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator convention
     */
    public int compareByHighestStreamsCountTotal(Song other) {
        return Integer.compare(other.getStreamsCountTotal(), getStreamsCountTotal());
    }

    /**
     * compares this song with the other song
     * ordening all Dutch songs upfront and then by decreasing total number of streams
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator conventions
     */

    public int compareForDutchNationalChart(Song other) {
        if (getLanguage() == Language.NL && other.getLanguage() == Language.NL) {
            return compareByHighestStreamsCountTotal(other);
        }

        if (getLanguage() == Language.NL) {
            return -1;
        }

        if (other.getLanguage() == Language.NL) {
            return 1;
        }
        return compareByHighestStreamsCountTotal(other);
    }


    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public Language getLanguage() {
        return language;
    }

    @Override
    /**
     * returns a clone of this song
     */
    public Song clone() {
        try {
            return (Song) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String toString() {
        return String.format("%s/%s{%s}(%d)", artist, title, language, getStreamsCountTotal());
    }
}
