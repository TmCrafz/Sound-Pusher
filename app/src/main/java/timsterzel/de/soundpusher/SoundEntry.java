package timsterzel.de.soundpusher;

/**
 * Created by tim on 13.03.16.
 */
public class SoundEntry {

    private String m_soundPath;

    private String m_picturePath;

    private String m_name;
    // True if the sound is recorded by this application
    private boolean m_internRecorded;


    public SoundEntry(String soundPath, String picturePath, String name, boolean internRecorded) {
        m_soundPath = soundPath;
        m_picturePath = picturePath;
        m_name = name;
        m_internRecorded = internRecorded;
    }

    public String getSoundPath() { return m_soundPath; }

    public String getPicturePath() { return m_picturePath; }

    public String getName() { return m_name; }

    public boolean isInternRecorded() { return m_internRecorded; }

    public void setSoundPath(String soundPath) { m_soundPath = soundPath; }

    public void setPicturePath(String picturePath) { m_picturePath = picturePath; }

    public void setName(String name) { m_name = name; }

    public void setIsInternRecorded(boolean internRecorded) { m_internRecorded = internRecorded; }



}
