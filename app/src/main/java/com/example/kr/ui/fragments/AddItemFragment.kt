package com.example.kr.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.kr.R
import com.example.kr.data.Repository
import com.example.kr.model.Item
import com.example.kr.viewmodel.ItemViewModel
import com.example.kr.viewmodel.ItemViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddItemFragment : Fragment() {
    private lateinit var viewModel: ItemViewModel
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextDescription: TextInputEditText
    private lateinit var editTextStoreName: TextInputEditText
    private lateinit var editTextOriginalPrice: TextInputEditText
    private lateinit var editTextDiscountedPrice: TextInputEditText
    private lateinit var editTextValidUntil: TextInputEditText
    private lateinit var buttonSave: MaterialButton
    private lateinit var buttonSelectDate: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val factory = ItemViewModelFactory(Repository(requireContext()))
        viewModel = ViewModelProvider(this, factory).get(ItemViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_add_item, container, false)

        editTextName = rootView.findViewById(R.id.editTextName)
        editTextDescription = rootView.findViewById(R.id.editTextDescription)
        editTextStoreName = rootView.findViewById(R.id.editTextStoreName)
        editTextOriginalPrice = rootView.findViewById(R.id.editTextOriginalPrice)
        editTextDiscountedPrice = rootView.findViewById(R.id.editTextDiscountedPrice)
        editTextValidUntil = rootView.findViewById(R.id.editTextValidUntil)
        buttonSave = rootView.findViewById(R.id.buttonSave)
        buttonSelectDate = rootView.findViewById(R.id.buttonSelectDate)

        // Обробка натискання кнопки вибору дати
        buttonSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Обробка натискання кнопки збереження
        buttonSave.setOnClickListener {
            val name = editTextName.text.toString()
            val description = editTextDescription.text.toString()
            val storeName = editTextStoreName.text.toString()
            val originalPriceText = editTextOriginalPrice.text.toString()
            val discountedPriceText = editTextDiscountedPrice.text.toString()
            val validUntil = editTextValidUntil.text.toString()

            if (name.isNotBlank() && description.isNotBlank() && storeName.isNotBlank() &&
                originalPriceText.isNotBlank() && discountedPriceText.isNotBlank() && validUntil.isNotBlank()) {

                val originalPrice = originalPriceText.toDoubleOrNull() ?: 0.0
                val discountedPrice = discountedPriceText.toDoubleOrNull() ?: 0.0

                val item = Item(
                    name = name,
                    description = description,
                    storeName = storeName,
                    originalPrice = originalPrice,
                    discountedPrice = discountedPrice,
                    validUntil = validUntil
                )
                viewModel.addItem(item)

                Toast.makeText(requireContext(), getString(R.string.item_added_successfully), Toast.LENGTH_SHORT).show()

                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Відображаємо вибрану дату у EditText
                val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                editTextValidUntil.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
}
