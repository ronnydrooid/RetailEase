package com.example.retailease.ui.screens.retail.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retailease.models.Employee
import com.example.retailease.models.Product
import com.example.retailease.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(private val employeeRepository: EmployeeRepository) :
    ViewModel() {
    private val employeeList: List<Employee> = emptyList()
    private val _employeeListState = MutableStateFlow(employeeList)
    val employeeListState = _employeeListState.asStateFlow()

    init {
        observeEmployees()
    }

    private fun observeEmployees() {
        viewModelScope.launch {

            employeeRepository.getAllEmployees().collect {employeeList ->
                _employeeListState.value = employeeList
            }
        }
    }

    fun addEmployee(employee: Employee){
        viewModelScope.launch {
            employeeRepository.insertEmployee(employee)
        }
    }

    fun deleteEmployee(employee: Employee){
        viewModelScope.launch {
            employeeRepository.deleteEmployee(employee)
        }
    }
}