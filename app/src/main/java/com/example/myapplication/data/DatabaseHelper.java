package com.example.myapplication.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.model.Deck;
import com.example.myapplication.model.Flashcard;
import com.example.myapplication.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "studycards.db";
    private static final int DATABASE_VERSION = 7;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_DECKS = "decks";
    private static final String TABLE_FLASHCARDS = "flashcards";

    // User Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_PASSWORD = "password";
    private static final String KEY_USER_PHONE = "phone_number";

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
                + KEY_USER_PASSWORD + " TEXT,"
                + KEY_USER_PHONE + " TEXT"
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
        // Add a default user
        ContentValues userValues = new ContentValues();
        userValues.put(KEY_USER_ID, 1);
        userValues.put(KEY_USER_NAME, "mikoto");
        userValues.put(KEY_USER_EMAIL, "a@gmail.com");
        userValues.put(KEY_USER_PASSWORD, "123");
        userValues.put(KEY_USER_PHONE, "0123456789");
        db.insert(TABLE_USERS, null, userValues);

        // Deck IDs are 1-6
        db.insert(TABLE_DECKS, null, createDeckValues("English Vocabulary 101", "Basic words for beginners.", 1, "ic_book", "#7C4DFF"));
        db.insert(TABLE_DECKS, null, createDeckValues("Spanish Essentials", "Common phrases for travel.", 1, "ic_translate", "#E91E63"));
        db.insert(TABLE_DECKS, null, createDeckValues("Chemistry Basics", "Periodic table elements.", 1, "ic_science", "#4CAF50"));
        db.insert(TABLE_DECKS, null, createDeckValues("World Capitals", "Test your geography knowledge.", 1, "ic_map", "#2196F3"));
        db.insert(TABLE_DECKS, null, createDeckValues("Java Programming", "Core concepts and syntax.", 1, "ic_code", "#FF9800"));
        db.insert(TABLE_DECKS, null, createDeckValues("Japanese Hiragana", "Basic Japanese characters.", 1, "ic_translate", "#9C27B0"));

        // --- English Vocabulary (Deck 1) ---
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Apple", "A fruit, often red or green.", 1, true));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("House", "A building where people live.", 1));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Book", "A set of written or printed pages, bound with a cover.", 1));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Computer", "An electronic device for storing and processing data.", 1));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Water", "A colorless, transparent, odorless liquid.", 1));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Sun", "The star at the center of the Solar System.", 1));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Moon", "The natural satellite of the Earth.", 1));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Tree", "A woody perennial plant, typically having a single stem or trunk.", 1));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Car", "A four-wheeled road vehicle that is powered by an engine.", 1));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Friend", "A person with whom one has a bond of mutual affection.", 1));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Sleep", "A condition of body and mind that typically recurs for several hours every night.", 1));

        // --- Spanish Essentials (Deck 2) ---
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Hola", "Hello", 2));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Adiós", "Goodbye", 2));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Gracias", "Thank you", 2, true));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Por favor", "Please", 2));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("¿Cómo estás?", "How are you?", 2));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Lo siento", "I'm sorry", 2));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Agua", "Water", 2));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Sí", "Yes", 2));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("No", "No", 2));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("¿Cuánto cuesta?", "How much does it cost?", 2));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("La cuenta", "The check/bill", 2));

        // --- Chemistry Basics (Deck 3) ---
        db.insert(TABLE_FLASHCARDS, null, createCardValues("H", "Hydrogen", 3));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("O", "Oxygen", 3));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Au", "Gold", 3));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Fe", "Iron", 3));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("NaCl", "Sodium Chloride (Salt)", 3, true));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("He", "Helium", 3));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("C", "Carbon", 3));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("N", "Nitrogen", 3));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Ag", "Silver", 3));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Cu", "Copper", 3));

        // --- World Capitals (Deck 4) ---
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Japan", "Tokyo", 4, true));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("France", "Paris", 4));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("USA", "Washington, D.C.", 4));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Italy", "Rome", 4));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Canada", "Ottawa", 4));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Australia", "Canberra", 4));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Germany", "Berlin", 4));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("United Kingdom", "London", 4));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("China", "Beijing", 4));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Russia", "Moscow", 4));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Brazil", "Brasília", 4));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("India", "New Delhi", 4, true));

        // --- Java Programming (Deck 5) ---
        db.insert(TABLE_FLASHCARDS, null, createCardValues("JVM", "Java Virtual Machine", 5));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("SDK", "Software Development Kit", 5));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("OOP", "Object-Oriented Programming", 5, true));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("API", "Application Programming Interface", 5));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("JRE", "Java Runtime Environment", 5));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Polymorphism", "The ability of an object to take on many forms.", 5));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Inheritance", "A mechanism wherein a new class is derived from an existing class.", 5));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("Encapsulation", "The bundling of data with the methods that operate on that data.", 5));

        // --- Japanese Hiragana (Deck 6) ---
        db.insert(TABLE_FLASHCARDS, null, createCardValues("あ", "a", 6));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("い", "i", 6));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("う", "u", 6, true));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("え", "e", 6));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("お", "o", 6));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("か", "ka", 6));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("き", "ki", 6));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("く", "ku", 6));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("け", "ke", 6));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("こ", "ko", 6));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("さ", "sa", 6));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("し", "shi", 6));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("す", "su", 6));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("せ", "se", 6));
        db.insert(TABLE_FLASHCARDS, null, createCardValues("そ", "so", 6));
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
        return createCardValues(front, back, deckId, false);
    }

    private ContentValues createCardValues(String front, String back, int deckId, boolean isFavorite) {
        ContentValues values = new ContentValues();
        values.put(KEY_CARD_FRONT, front);
        values.put(KEY_CARD_BACK, back);
        values.put(KEY_CARD_DECK_ID, deckId);
        values.put(KEY_CARD_IS_FAVORITE, isFavorite ? 1 : 0);
        return values;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 7) {
            // Drop older tables if they exist
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLASHCARDS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DECKS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            // Create tables again
            onCreate(db);
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

    public void addFlashcard(Flashcard flashcard, int deckId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CARD_FRONT, flashcard.getFrontContent());
        values.put(KEY_CARD_BACK, flashcard.getBackContent());
        values.put(KEY_CARD_IS_FAVORITE, flashcard.isFavorite() ? 1 : 0);
        values.put(KEY_CARD_DECK_ID, deckId);
        db.insert(TABLE_FLASHCARDS, null, values);
    }

    public void updateFlashcard(int flashcardId, String question, String answer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CARD_FRONT, question);
        values.put(KEY_CARD_BACK, answer);
        db.update(TABLE_FLASHCARDS, values, KEY_CARD_ID + " = ?", new String[]{String.valueOf(flashcardId)});
    }

    public void deleteFlashcard(int flashcardId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FLASHCARDS, KEY_CARD_ID + " = ?", new String[]{String.valueOf(flashcardId)});
    }

    public boolean checkUser(String email, String password) {
        User user = getUserByEmail(email);
        return user != null && user.getPassword().equals(password);
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

    public int getCardCountForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_FLASHCARDS + " f INNER JOIN " + TABLE_DECKS + " d ON f." + KEY_CARD_DECK_ID + " = d." + KEY_DECK_ID + " WHERE d." + KEY_DECK_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
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
        db.update(TABLE_DECKS, values, KEY_DECK_ID + " = ?", new String[]{String.valueOf(currentDeck.getId())});
    }

    public Deck getDeck(int deckId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DECKS, null, KEY_DECK_ID + "=?", new String[]{String.valueOf(deckId)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Deck deck = new Deck();
            deck.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DECK_ID)));
            deck.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DECK_NAME)));
            deck.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DECK_DESCRIPTION)));
            deck.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DECK_USER_ID)));
            deck.setIconKey(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DECK_ICON_KEY)));
            deck.setColor(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DECK_COLOR)));
            cursor.close();
            return deck;
        }
        return null;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, KEY_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_PASSWORD)));
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_PHONE)));
            cursor.close();
            return user;
        }
        return null;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, KEY_USER_EMAIL + "=?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_PASSWORD)));
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_PHONE)));
            cursor.close();
            return user;
        }
        return null;
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_EMAIL, user.getEmail());
        values.put(KEY_USER_PHONE, user.getPhoneNumber());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            values.put(KEY_USER_PASSWORD, user.getPassword());
        }
        db.update(TABLE_USERS, values, KEY_USER_ID + " = ?", new String[]{String.valueOf(user.getId())});
    }
}
