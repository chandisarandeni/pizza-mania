package com.pizzamania.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pizzamania.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pizzamania_cart.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE = "cart_items";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_SIZE = "size";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_PRICE = "price";
    public static final String COL_IMAGE = "image_res";

    public CartDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_SIZE + " TEXT, " +
                COL_QUANTITY + " INTEGER, " +
                COL_PRICE + " REAL, " +
                COL_IMAGE + " INTEGER" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long addCartItem(CartItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, item.getName());
        cv.put(COL_SIZE, item.getSize());
        cv.put(COL_QUANTITY, item.getQuantity());
        cv.put(COL_PRICE, item.getPrice());
        cv.put(COL_IMAGE, item.getImageRes());
        long id = db.insert(TABLE, null, cv);
        db.close();
        return id;
    }

    public List<CartItem> getAllCartItems() {
        List<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE, null, null, null, null, null, COL_ID + " DESC");
        if (c != null) {
            while (c.moveToNext()) {
                CartItem item = new CartItem();
                item.setId(c.getLong(c.getColumnIndexOrThrow(COL_ID)));
                item.setName(c.getString(c.getColumnIndexOrThrow(COL_NAME)));
                item.setSize(c.getString(c.getColumnIndexOrThrow(COL_SIZE)));
                item.setQuantity(c.getInt(c.getColumnIndexOrThrow(COL_QUANTITY)));
                item.setPrice(c.getDouble(c.getColumnIndexOrThrow(COL_PRICE)));
                item.setImageRes(c.getInt(c.getColumnIndexOrThrow(COL_IMAGE)));
                list.add(item);
            }
            c.close();
        }
        db.close();
        return list;
    }

    public void updateCartItemQuantity(long id, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_QUANTITY, quantity);
        db.update(TABLE, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void removeCartItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void clearCart() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, null, null);
        db.close();
    }

    public double getTotalPrice() {
        double total = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE, new String[]{COL_PRICE, COL_QUANTITY}, null, null, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                double price = c.getDouble(c.getColumnIndexOrThrow(COL_PRICE));
                int quantity = c.getInt(c.getColumnIndexOrThrow(COL_QUANTITY));
                total += price * quantity;
            }
            c.close();
        }
        db.close();
        return total;
    }
}