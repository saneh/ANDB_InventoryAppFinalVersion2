package in.lemonco.products;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class InventoryTable {

    // Database table
    public static final String TABLE_NAME = "product";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_SALES = "sales";
    public static final String COLUMN_SUPPLIER = "supplier";
    public static final String COLUMN_IMAGE = "image";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_PRICE + " integer not null,"
            + COLUMN_QUANTITY + " integer not null,"
            + COLUMN_SALES + " integer,"
            + COLUMN_SUPPLIER + " text not null, "
            + COLUMN_IMAGE + " BLOB"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(InventoryTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}