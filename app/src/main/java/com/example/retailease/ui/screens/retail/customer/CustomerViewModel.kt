package com.example.retailease.ui.screens.retail.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retailease.models.Customer
import com.example.retailease.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(private val customerRepository: CustomerRepository) : ViewModel() {
    private val customerList: List<Customer> = emptyList()
    private val _customerListState = MutableStateFlow(customerList)
    val customerListState = _customerListState.asStateFlow()

    init {
        observeCustomer()
    }

    private fun observeCustomer() {
        viewModelScope.launch {
            customerRepository.getAllCustomers().collect { customerList ->
                _customerListState.value = customerList
            }

        }
    }

    fun addCustomer(customer: Customer){
        viewModelScope.launch {
            customerRepository.insertCustomer(customer)
        }
    }

    fun deleteCustomer(customer: Customer){
        viewModelScope.launch {
            customerRepository.deleteCustomer(customer)
        }
    }
}