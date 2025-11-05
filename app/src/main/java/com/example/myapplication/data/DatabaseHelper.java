package com.example.myapplication.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.model.Deck;
import com.example.myapplication.model.Flashcard;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "studycards.db";
    // Bump the database version to 3 to trigger onUpgrade
    private static final int DATABASE_VERSION = 3;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_DECKS = "decks";
    private static final String TABLE_FLASHCARDS = "flashcards";

    // User Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_PASSWORD = "password";

    // Deck Table Columns
    private static final String KEY_DECK_ID = "id";
    private static final String KEY_DECK_NAME = "name";
    private static final String KEY_DECK_DESCRIPTION = "description";
    private static final String KEY_DECK_USER_ID = "user_id";
    private static final String KEY_DECK_ICON_KEY = "icon_key";
    private static final String KEY_DECK_COLOR = "color";

    // Flashcard Table Columns
    private static final String KEY_CARD_ID = "id";
    private static final String KEY_CARD_FRONT = "front_content";
    private static final String KEY_CARD_BACK = "back_content";
    private static final String KEY_CARD_DECK_ID = "deck_id";
    private static final String KEY_CARD_IS_FAVORITE = "is_favorite";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_NAME + " TEXT,"
                + KEY_USER_EMAIL + " TEXT UNIQUE,"
                + KEY_USER_PASSWORD + " TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_DECKS_TABLE = "CREATE TABLE " + TABLE_DECKS + "("
                + KEY_DECK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DECK_NAME + " TEXT,"
                + KEY_DECK_DESCRIPTION + " TEXT,"
                + KEY_DECK_USER_ID + " INTEGER, "
                + KEY_DECK_ICON_KEY + " TEXT, "
                + KEY_DECK_COLOR + " TEXT, "
                + "FOREIGN KEY(" + KEY_DECK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_DECKS_TABLE);

        String CREATE_FLASHCARDS_TABLE = "CREATE TABLE " + TABLE_FLASHCARDS + "("
                + KEY_CARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CARD_FRONT + " TEXT,"
                + KEY_CARD_BACK + " TEXT,"
                + KEY_CARD_DECK_ID + " INTEGER, "
                + KEY_CARD_IS_FAVORITE + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY(" + KEY_CARD_DECK_ID + ") REFERENCES " + TABLE_DECKS + "(" + KEY_DECK_ID + ")"
                + ")";
        db.execSQL(CREATE_FLASHCARDS_TABLE);

        addInitialData(db);
    }

    private void addInitialData(SQLiteDatabase db) {
        ContentValues userValues = new ContentValues();
        userValues.put(KEY_USER_ID, 1);
        userValues.put(KEY_USER_NAME, "ngoc");
        userValues.put(KEY_USER_EMAIL, "a@gmail.com");
        userValues.put(KEY_USER_PASSWORD, "28061977");
        db.insert(TABLE_USERS, null, userValues);

        db.insert(TABLE_DECKS, null, createDeckValues("English Vocabulary 101", "Basic words for beginners.", 1, "ic_book", "#7C4DFF"));
        db.insert(TABLE_DECKS, null, createDeckValues("Spanish Essentials", "Common phrases for travel.", 1, "ic_translate", "#E91E63"));
        db.insert(TABLE_DECKS, null, createDeckValues("Chemistry Basics", "Periodic table elements.", 1, "ic_science", "#4CAF50"));
        db.insert(TABLE_DECKS, null, createDeckValues("World Capitals", "Test your geography knowledge.", 1, "ic_map", "#2196F3"));
        db.insert(TABLE_DECKS, null, createDeckValues("Java Programming", "Core concepts and syntax.", 1, "ic_code", "#FF9800"));
        db.insert(TABLE_DECKS, null, createDeckValues("Japanese Hiragana", "Basic Japanese characters.", 1, "ic_translate", "#9C27B0"));

        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= (i * 5 + 10); j++) {
                db.insert(TABLE_FLASHCARDS, null, createCardValues("Front " + j, "Back " + j, i));
            }
        }

        // Add some sample favorite cards
        ContentValues favoriteValues = new ContentValues();
        favoriteValues.put(KEY_CARD_IS_FAVORITE, 1);

        // Mark a few cards from different decks as favorites
        db.update(TABLE_FLASHCARDS, favoriteValues, KEY_CARD_ID + " IN (2, 8, 16, 25, 33)", null);
    }

    private ContentValues createDeckValues(String name, String desc, int userId, String iconKey, String color) {
        ContentValues values = new ContentValues();
        values.put(KEY_DECK_NAME, name);
        values.put(KEY_DECK_DESCRIPTION, desc);
        values.put(KEY_DECK_USER_ID, userId);
        values.put(KEY_DECK_ICON_KEY, iconKey);
        values.put(KEY_DECK_COLOR, color);
        return values;
    }

    private ContentValues createCardValues(String front, String back, int deckId) {
        ContentValues values = new ContentValues();
        values.put(KEY_CARD_FRONT, front);
        values.put(KEY_CARD_BACK, back);
        values.put(KEY_CARD_DECK_ID, deckId);
        return values;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_FLASHCARDS + " ADD COLUMN " + KEY_CARD_IS_FAVORITE + " INTEGER DEFAULT 0");
        }
    }

    public List<Flashcard> getFavoriteFlashcards(int userId) {
        List<Flashcard> flashcardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT f.* FROM " + TABLE_FLASHCARDS + " f INNER JOIN " + TABLE_DECKS + " d ON f." + KEY_CARD_DECK_ID + " = d." + KEY_DECK_ID + " WHERE d." + KEY_DECK_USER_ID + " = ? AND f." + KEY_CARD_IS_FAVORITE + " = 1";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Flashcard card = new Flashcard();
                card.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CARD_ID)));
                card.setFrontContent(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CARD_FRONT)));
                card.setBackContent(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CARD_BACK)));
                card.setDeckId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CARD_DECK_ID)));
                card.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CARD_IS_FAVORITE)) == 1);
                flashcardList.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return flashcardList;
    }

    public List<Flashcard> getFlashcardsForDeck(int deckId) {
        List<Flashcard> flashcardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FLASHCARDS, null, KEY_CARD_DECK_ID + " = ?", new String[]{String.valueOf(deckId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Flashcard card = new Flashcard();
                card.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CARD_ID)));
                card.setFrontContent(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CARD_FRONT)));
                card.setBackContent(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CARD_BACK)));
                card.setDeckId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CARD_DECK_ID)));
                card.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CARD_IS_FAVORITE)) == 1);
                flashcardList.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return flashcardList;
    }

    public void setFlashcardFavoriteStatus(int cardId, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CARD_IS_FAVORITE, isFavorite ? 1 : 0);
        db.update(TABLE_FLASHCARDS, values, KEY_CARD_ID + " = ?", new String[]{String.valueOf(cardId)});
    }

    public void addUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, name);
        values.put(KEY_USER_EMAIL, email);
        values.put(KEY_USER_PASSWORD, password);
        db.insert(TABLE_USERS, null, values);
    }

    public void addDeck(String name, String description, int userId, String iconKey, String color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DECK_NAME, name);
        values.put(KEY_DECK_DESCRIPTION, description);
        values.put(KEY_DECK_USER_ID, userId);
        values.put(KEY_DECK_ICON_KEY, iconKey);
        values.put(KEY_DECK_COLOR, color);
        db.insert(TABLE_DECKS, null, values);
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USER_ID}, KEY_USER_EMAIL + "=? AND " + KEY_USER_PASSWORD + "=?", new String[]{email, password}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public List<Deck> getAllDecks(int userId) {
        List<Deck> deckList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DECKS, null, KEY_DECK_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Deck deck = new Deck();
                deck.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DECK_ID)));
                deck.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DECK_NAME)));
                deck.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DECK_DESCRIPTION)));
                deck.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DECK_USER_ID)));
                deck.setIconKey(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DECK_ICON_KEY)));
                deck.setColor(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DECK_COLOR)));
                deckList.add(deck);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return deckList;
    }

    public int getCardCountForDeck(int deckId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_FLASHCARDS + " WHERE " + KEY_CARD_DECK_ID + " = ?", new String[]{String.valueOf(deckId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public void deleteDeck(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FLASHCARDS, KEY_CARD_DECK_ID + " = ?", new String[]{String.valueOf(id)});
        db.delete(TABLE_DECKS, KEY_DECK_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void updateDeck(Deck currentDeck) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DECK_NAME, currentDeck.getName());
        values.put(KEY_DECK_DESCRIPTION, currentDeck.getDescription());
        values.put(KEY_DECK_ICON_KEY, currentDeck.getIconKey());
        values.put(KEY_DECK_COLOR, currentDeck.getColor());

        db.update(TABLE_DECKS, values, KEY_DECK_ID + " = ?",
                new String[]{String.valueOf(currentDeck.getId())});
    }

    public Deck getDeck(int deckId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DECKS, null, KEY_DECK_ID + " = ?", new String[]{String.valueOf(deckId)}, null, null, null);

        Deck deck = null;
        if (cursor != null && cursor.moveToFirst()) {
            deck = new Deck();
            deck.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DECK_ID)));
            deck.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DECK_NAME)));
            deck.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DECK_DESCRIPTION)));
            deck.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DECK_USER_ID)));
            deck.setIconKey(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DECK_ICON_KEY)));
            deck.setColor(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DECK_COLOR)));
        }
        if (cursor != null) {
            cursor.close();
        }
        return deck;
    }
}
