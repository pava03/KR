package com.example.kr.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_STORE_NAME TEXT NOT NULL,
                $COLUMN_ORIGINAL_PRICE REAL NOT NULL,
                $COLUMN_DISCOUNTED_PRICE REAL NOT NULL,
                $COLUMN_VALID_UNTIL TEXT NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // У спрощеному випадку видаляємо та створюємо таблицю наново
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "items.db"
        const val DATABASE_VERSION = 2

        const val TABLE_NAME = "items"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_STORE_NAME = "store_name"
        const val COLUMN_ORIGINAL_PRICE = "original_price"
        const val COLUMN_DISCOUNTED_PRICE = "discounted_price"
        const val COLUMN_VALID_UNTIL = "valid_until"
    }
}
