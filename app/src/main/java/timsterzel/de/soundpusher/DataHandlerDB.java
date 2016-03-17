package timsterzel.de.soundpusher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by tim on 16.03.16.
 */
public class DataHandlerDB extends SQLiteOpenHelper {

    private static final String TAG = DataHandlerDB.class.getSimpleName();

    public final static String DATABASE_NAME = "sound_entries.db";

    private final static int DATABASE_VERSION = 1;

    public final static String SOUND_TABLE = "sounds";

    private final static String SOUND_ID = "_id";
    private final static String SOUND_SOUND_PATH = "sound_path";
    private final static String SOUND_HAS_PICTURE = "has_picture";
    private final static String SOUND_PICTURE_PATH = "picture_path";
    private final static String SOUND_NAME = "name";
    private final static String SOUND_INTERN_RECORDED = "intern_recorded";

    private final static String TABLE_SOUND_CREATE = "CREATE TABLE " + SOUND_TABLE + " (" + SOUND_ID + " INTEGER PRIMARY KEY, " +
            SOUND_SOUND_PATH + " TEXT, " +
            SOUND_HAS_PICTURE + " INTEGER, " +
            SOUND_PICTURE_PATH + " TEXT, " +
            SOUND_NAME + " TEXT, " +
            SOUND_INTERN_RECORDED + " INTEGER)";

    private final static String TABLE_SOUND_DROP = "DROP TABLE IF EXISTS " + SOUND_TABLE;

    private Context mContext;

    public DataHandlerDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_SOUND_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TABLE_SOUND_DROP);
    }

    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    public void deleteTable(final String TABLE) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, null, null);
    }

    public ContentValues getContentValues(final SoundEntry soundEntry) {
        ContentValues values = new ContentValues();
        values.put(SOUND_SOUND_PATH, soundEntry.getSoundPath());
        values.put(SOUND_HAS_PICTURE, soundEntry.hasPicture());
        values.put(SOUND_PICTURE_PATH, soundEntry.getPicturePath());
        values.put(SOUND_NAME, soundEntry.getName());
        values.put(SOUND_INTERN_RECORDED, soundEntry.isInternRecorded());
        return values;
    }

    // Add the given SoundEntry and change his id to the new row id
    public int addSoundEntry(final SoundEntry soundEntry){
        ContentValues values = getContentValues(soundEntry);
        SQLiteDatabase db = this.getWritableDatabase();
        int rowID = (int) db.insert(SOUND_TABLE, null, values);
        db.close();
        soundEntry.setID(rowID);
        return rowID;
    }

    public void addSoundEntries(final ArrayList<SoundEntry> soundEntries) {
        for (SoundEntry soundEntry: soundEntries)
            addSoundEntry(soundEntry);
    }

    public int updateSoundEntry(final SoundEntry soundEntry){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = getContentValues(soundEntry);
        int i = db.update(SOUND_TABLE, values, SOUND_ID + "= ?", new String[]{String.valueOf(soundEntry.getID())});
        db.close();
        return i;
    }

    public ArrayList<SoundEntry> getAllSoundEntries()
    {
        String selectQuery = "SELECT * FROM " + SOUND_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        long id = 0;
        String soundPath;
        boolean hasPicture;
        String picturePath;
        String name;
        boolean isInternRecorded;
        ArrayList<SoundEntry> soundEntries = new ArrayList<SoundEntry>();
        if (cursor.moveToFirst())
        {
            do
            {
                id = cursor.getInt(0);
                soundPath = cursor.getString(1);
                hasPicture = cursor.getInt(2) != 0;
                picturePath = cursor.getString(3);
                name = cursor.getString(4);
                isInternRecorded = cursor.getInt(5) != 0;
                soundEntries.add(new SoundEntry(id, soundPath, hasPicture, picturePath, name, isInternRecorded));
            } while (cursor.moveToNext());
        }
        db.close();
        return soundEntries;
    }

    public void deleteSoundEntry(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SOUND_TABLE, SOUND_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}
