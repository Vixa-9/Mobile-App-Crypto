package com.bvic.followmycrypto.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bvic.followmycrypto.data.CryptoRepository
import com.bvic.followmycrypto.model.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CryptoDetailViewModel(
    private val symbol: String,
    private val repository: CryptoRepository,
    initialCurrency: Currency = Currency.USD,
) : ViewModel() {

    private val _selectedCurrency = MutableStateFlow(initialCurrency)
    val selectedCurrency: StateFlow<Currency> = _selectedCurrency.asStateFlow()

    private val _uiState = MutableStateFlow<CryptoDetailUiState>(CryptoDetailUiState.Loading)
    val uiState: StateFlow<CryptoDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    fun selectCurrency(currency: Currency) {
        if (_selectedCurrency.value != currency) {
            _selectedCurrency.value = currency
            load()
        }
    }

    private fun load() {
        _uiState.value = CryptoDetailUiState.Loading
        viewModelScope.launch {
            try {
                _uiState.value = CryptoDetailUiState.Success(repository.getCrypto(symbol, _selectedCurrency.value))
            } catch (e: Exception) {
                _uiState.value = CryptoDetailUiState.Error("Impossible de charger $symbol.")
            }
        }
    }
}
