package com.example.kr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kr.data.Repository
import com.example.kr.model.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemViewModel(private val repository: Repository) : ViewModel() {
    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> get() = _items

    init {
        fetchItems()
    }

    fun fetchItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.getAllItems()
            _items.postValue(data)
        }
    }

    fun addItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addItem(item)
            fetchItems()
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateItem(item)
            fetchItems()
        }
    }

    fun deleteItem(itemId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(itemId)
            fetchItems()
        }
    }

    // Новий метод для фільтрованого пошуку
    fun filterItems(query: String?, storeName: String?, validUntil: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val filteredItems = repository.searchItems(query, storeName, validUntil)
            _items.postValue(filteredItems)
        }
    }
}
