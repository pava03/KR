package com.example.kr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.kr.R
import com.example.kr.data.Repository
import com.example.kr.model.Item
import com.example.kr.viewmodel.ItemViewModel
import com.example.kr.viewmodel.ItemViewModelFactory
import com.google.android.material.button.MaterialButton

class ItemDetailFragment : Fragment() {

    private lateinit var viewModel: ItemViewModel
    private lateinit var textName: TextView
    private lateinit var textDescription: TextView
    private lateinit var textStoreName: TextView
    private lateinit var textOriginalPrice: TextView
    private lateinit var textDiscountedPrice: TextView
    private lateinit var textValidUntil: TextView
    private lateinit var buttonDelete: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_item_detail, container, false)

        // Ініціалізація Repository і ViewModel через фабрику
        val repository = Repository(requireContext())
        val factory = ItemViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ItemViewModel::class.java]

        // Знаходимо компоненти
        textName = rootView.findViewById(R.id.textName)
        textDescription = rootView.findViewById(R.id.textDescription)
        textStoreName = rootView.findViewById(R.id.textStoreName)
        textOriginalPrice = rootView.findViewById(R.id.textOriginalPrice)
        textDiscountedPrice = rootView.findViewById(R.id.textDiscountedPrice)
        textValidUntil = rootView.findViewById(R.id.textValidUntil)
        buttonDelete = rootView.findViewById(R.id.buttonDelete)

        // Отримуємо ID елемента з аргументів
        val itemId = arguments?.getInt("itemId") ?: 0
        viewModel.fetchItems()

        // Спостерігаємо за змінами в списку елементів
        viewModel.items.observe(viewLifecycleOwner) { items ->
            val selectedItem = items.find { it.id == itemId }
            if (selectedItem != null) {
                textName.text = selectedItem.name
                textDescription.text = selectedItem.description
                textStoreName.text = "Магазин: ${selectedItem.storeName}"
                textOriginalPrice.text = "Початкова ціна: ${selectedItem.originalPrice} грн"
                textDiscountedPrice.text = "Ціна зі знижкою: ${selectedItem.discountedPrice} грн"
                textValidUntil.text = "Діє до: ${selectedItem.validUntil}"
            }
        }

        // Обробка кнопки Delete для видалення елемента
        buttonDelete.setOnClickListener {
            val selectedItem = viewModel.items.value?.find { it.id == itemId }
            selectedItem?.let {
                viewModel.deleteItem(it.id)
                findNavController().navigateUp()  // Повертаємось до попереднього фрагмента
            }
        }

        return rootView
    }
}
