package com.example.kr.ui.fragments

import android.animation.LayoutTransition
import android.content.res.ColorStateList
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kr.R
import com.example.kr.data.Repository
import com.example.kr.ui.adapters.ItemAdapter
import com.example.kr.viewmodel.ItemViewModel
import com.example.kr.viewmodel.ItemViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.text.SimpleDateFormat
import java.util.*

class ItemListFragment : Fragment() {
    private lateinit var viewModel: ItemViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddItem: FloatingActionButton
    private lateinit var adapter: ItemAdapter

    // Поля для фільтрів
    private lateinit var editTextQuery: EditText
    private lateinit var editTextStoreName: EditText
    private lateinit var editTextValidUntil: EditText
    private lateinit var buttonFilter: Button
    private lateinit var buttonSelectDateFilter: Button
    private lateinit var textNoItems: TextView

    // ChipGroup для відображення вибраних фільтрів
    private lateinit var chipGroupFilters: ChipGroup

    // Кнопка для показу/приховування фільтрів
    private lateinit var buttonToggleFilters: Button
    private lateinit var layoutFilters: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_item_list, container, false)

        // Ініціалізація ViewModel
        val factory = ItemViewModelFactory(Repository(requireContext()))
        viewModel = ViewModelProvider(this, factory).get(ItemViewModel::class.java)

        // Знаходження елементів за ID
        recyclerView = rootView.findViewById(R.id.recyclerView)
        fabAddItem = rootView.findViewById(R.id.fabAddItem)
        editTextQuery = rootView.findViewById(R.id.editTextQuery)
        editTextStoreName = rootView.findViewById(R.id.editTextStoreName)
        editTextValidUntil = rootView.findViewById(R.id.editTextValidUntil)
        buttonFilter = rootView.findViewById(R.id.buttonFilter)
        buttonSelectDateFilter = rootView.findViewById(R.id.buttonSelectDateFilter)
        textNoItems = rootView.findViewById(R.id.textNoItems)
        buttonToggleFilters = rootView.findViewById(R.id.buttonToggleFilters)
        layoutFilters = rootView.findViewById(R.id.layoutFilters)
        chipGroupFilters = rootView.findViewById(R.id.chipGroupFilters)

        setupRecyclerView()
        setupListeners()

        adapter = ItemAdapter(emptyList()) { selectedItem ->
            val bundle = Bundle().apply {
                putInt("itemId", selectedItem.id)
            }
            findNavController().navigate(R.id.action_itemListFragment_to_itemDetailsFragment, bundle)
        }
        recyclerView.adapter = adapter

        // Додаємо анімацію до контейнера фільтрів
        val parentViewGroup = layoutFilters.parent as? ViewGroup
        parentViewGroup?.layoutTransition = LayoutTransition().apply {
            enableTransitionType(LayoutTransition.CHANGING)
            enableTransitionType(LayoutTransition.APPEARING)
            enableTransitionType(LayoutTransition.DISAPPEARING)
        }

        // Спостерігаємо за списком елементів
        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
            if (items.isEmpty()) {
                recyclerView.visibility = View.GONE
                textNoItems.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                textNoItems.visibility = View.GONE
            }
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchItems()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupListeners() {
        fabAddItem.setOnClickListener {
            findNavController().navigate(R.id.action_itemListFragment_to_addEditItemFragment)
        }

        buttonSelectDateFilter.setOnClickListener {
            showDatePickerDialog()
        }

        buttonFilter.setOnClickListener {
            applyFilters()
        }

        buttonToggleFilters.setOnClickListener {
            toggleFilters()
        }
    }

    private fun toggleFilters() {
        if (layoutFilters.visibility == View.GONE) {
            layoutFilters.visibility = View.VISIBLE
            buttonToggleFilters.text = getString(R.string.hide_filters)
        } else {
            layoutFilters.visibility = View.GONE
            buttonToggleFilters.text = getString(R.string.show_filters)
        }
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

    private fun applyFilters() {
        val query = editTextQuery.text.toString().takeIf { it.isNotBlank() }
        val store = editTextStoreName.text.toString().takeIf { it.isNotBlank() }
        val until = editTextValidUntil.text.toString().takeIf { it.isNotBlank() }

        viewModel.filterItems(query, store, until)

        // Відображення Chips
        chipGroupFilters.removeAllViews()
        query?.let {
            val chip = Chip(requireContext()).apply {
                text = "Пошук: $it"
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    editTextQuery.setText("")
                    chipGroupFilters.removeView(this)
                    applyFilters()
                }
                chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            chipGroupFilters.addView(chip)
        }
        store?.let {
            val chip = Chip(requireContext()).apply {
                text = "Магазин: $it"
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    editTextStoreName.setText("")
                    chipGroupFilters.removeView(this)
                    applyFilters()
                }
                chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primaryColor))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            chipGroupFilters.addView(chip)
        }
        until?.let {
            val chip = Chip(requireContext()).apply {
                text = "Діє до: $it"
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    editTextValidUntil.setText("")
                    chipGroupFilters.removeView(this)
                    applyFilters()
                }
                chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            chipGroupFilters.addView(chip)
        }

        // Показати ChipGroup, якщо є Chips
        chipGroupFilters.visibility = if (chipGroupFilters.childCount > 0) View.VISIBLE else View.GONE

        // Перевірка на наявність результатів
        viewModel.items.observe(viewLifecycleOwner) { items ->
            if (items.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.no_items_found), Toast.LENGTH_SHORT).show()
            }
        }
    }
}

