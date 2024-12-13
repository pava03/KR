package com.example.kr.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.kr.model.Item

class Repository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun getAllItems(): List<Item> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null)
        val items = mutableListOf<Item>()

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
                val description = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION))
                val storeName = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_STORE_NAME))
                val originalPrice = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORIGINAL_PRICE))
                val discountedPrice = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DISCOUNTED_PRICE))
                val validUntil = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_VALID_UNTIL))

                items.add(Item(
                    id = id,
                    name = name,
                    description = description,
                    storeName = storeName,
                    originalPrice = originalPrice,
                    discountedPrice = discountedPrice,
                    validUntil = validUntil
                ))
            }
            close()
        }

        Log.d("Repository", "Fetched items: ${items.size} items found.")
        return items
    }

    fun addItem(item: Item) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, item.name)
            put(DatabaseHelper.COLUMN_DESCRIPTION, item.description)
            put(DatabaseHelper.COLUMN_STORE_NAME, item.storeName)
            put(DatabaseHelper.COLUMN_ORIGINAL_PRICE, item.originalPrice)
            put(DatabaseHelper.COLUMN_DISCOUNTED_PRICE, item.discountedPrice)
            put(DatabaseHelper.COLUMN_VALID_UNTIL, item.validUntil)
        }
        val newRowId = db.insert(DatabaseHelper.TABLE_NAME, null, values)

        if (newRowId == -1L) {
            Log.e("Repository", "Failed to insert row")
        } else {
            Log.d("Repository", "Item inserted successfully with ID: $newRowId")
        }
    }

    fun updateItem(item: Item) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, item.name)
            put(DatabaseHelper.COLUMN_DESCRIPTION, item.description)
            put(DatabaseHelper.COLUMN_STORE_NAME, item.storeName)
            put(DatabaseHelper.COLUMN_ORIGINAL_PRICE, item.originalPrice)
            put(DatabaseHelper.COLUMN_DISCOUNTED_PRICE, item.discountedPrice)
            put(DatabaseHelper.COLUMN_VALID_UNTIL, item.validUntil)
        }
        db.update(DatabaseHelper.TABLE_NAME, values, "${DatabaseHelper.COLUMN_ID} = ?", arrayOf(item.id.toString()))
    }

    fun deleteItem(itemId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_NAME, "${DatabaseHelper.COLUMN_ID} = ?", arrayOf(itemId.toString()))
    }

    // Новий метод для пошуку з фільтрами
    fun searchItems(query: String?, storeName: String?, validUntil: String?): List<Item> {
        val db = dbHelper.readableDatabase

        val selectionArgs = mutableListOf<String>()
        val selectionClauses = mutableListOf<String>()

        // Фільтр за назвою/описом (якщо query не порожня)
        if (!query.isNullOrBlank()) {
            selectionClauses.add("(${DatabaseHelper.COLUMN_NAME} LIKE ? OR ${DatabaseHelper.COLUMN_DESCRIPTION} LIKE ?)")
            selectionArgs.add("%$query%")
            selectionArgs.add("%$query%")
        }

        // Фільтр за магазином (якщо storeName не порожній)
        if (!storeName.isNullOrBlank()) {
            selectionClauses.add("${DatabaseHelper.COLUMN_STORE_NAME} = ?")
            selectionArgs.add(storeName)
        }

        // Фільтр за датою (якщо validUntil не порожній)
        // Наприклад, показувати всі товари, де valid_until <= зазначеної дати
        if (!validUntil.isNullOrBlank()) {
            selectionClauses.add("${DatabaseHelper.COLUMN_VALID_UNTIL} <= ?")
            selectionArgs.add(validUntil)
        }

        val selection = if (selectionClauses.isNotEmpty()) {
            selectionClauses.joinToString(" AND ")
        } else {
            null
        }

        val cursor = db.query(DatabaseHelper.TABLE_NAME, null, selection, selectionArgs.toTypedArray(), null, null, null)
        val items = mutableListOf<Item>()

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
                val description = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION))
                val storeNameFetched = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_STORE_NAME))
                val originalPrice = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORIGINAL_PRICE))
                val discountedPrice = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DISCOUNTED_PRICE))
                val validUntilFetched = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_VALID_UNTIL))

                items.add(
                    Item(
                        id = id,
                        name = name,
                        description = description,
                        storeName = storeNameFetched,
                        originalPrice = originalPrice,
                        discountedPrice = discountedPrice,
                        validUntil = validUntilFetched
                    )
                )
            }
            close()
        }

        Log.d("Repository", "Search fetched items: ${items.size} items found.")
        return items
    }
}
