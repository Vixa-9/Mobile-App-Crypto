package com.bvic.followmycrypto.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bvic.followmycrypto.data.AppContainer
import com.bvic.followmycrypto.model.Crypto
import com.bvic.followmycrypto.model.Currency
import com.bvic.followmycrypto.theme.FollowMyCryptoTheme
import com.bvic.followmycrypto.theme.PriceDown
import com.bvic.followmycrypto.theme.PriceUp
import com.bvic.followmycrypto.ui.components.ErrorContent
import com.bvic.followmycrypto.ui.components.LoadingContent
import com.bvic.followmycrypto.ui.components.ThemeToggleButton
import com.bvic.followmycrypto.ui.format.formatPercent
import com.bvic.followmycrypto.ui.format.formatPrice
import com.bvic.followmycrypto.ui.list.CurrencySelector

@Composable
fun CryptoDetailScreen(
    symbol: String,
    onBack: () -> Unit,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CryptoDetailViewModel = viewModel { CryptoDetailViewModel(symbol, AppContainer.cryptoRepository) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    CryptoDetailContent(
        symbol = symbol,
        uiState = uiState,
        selectedCurrency = selectedCurrency,
        onCurrencySelected = viewModel::selectCurrency,
        onBack = onBack,
        onRetry = viewModel::retry,
        darkTheme = darkTheme,
        onToggleTheme = onToggleTheme,
        modifier = modifier,
    )
}

@Composable
fun CryptoDetailContent(
    symbol: String,
    uiState: CryptoDetailUiState,
    selectedCurrency: Currency = Currency.USD,
    onCurrencySelected: (Currency) -> Unit = {},
    onBack: () -> Unit = {},
    onRetry: () -> Unit = {},
    darkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(symbol, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    CurrencySelector(
                        selectedCurrency = selectedCurrency,
                        onCurrencySelected = onCurrencySelected,
                    )
                    ThemeToggleButton(darkTheme = darkTheme, onToggle = onToggleTheme)
                },
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding).fillMaxSize()
        when (uiState) {
            CryptoDetailUiState.Loading -> LoadingContent(modifier = contentModifier)
            is CryptoDetailUiState.Error -> ErrorContent(
                message = uiState.message,
                onRetry = onRetry,
                modifier = contentModifier,
            )
            is CryptoDetailUiState.Success -> CryptoDetail(crypto = uiState.crypto, modifier = contentModifier)
        }
    }
}

@Composable
fun CryptoDetail(crypto: Crypto, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = crypto.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = formatPrice(crypto.price, crypto.currency),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "${formatPercent(crypto.changePercent)} sur 24h",
            style = MaterialTheme.typography.titleMedium,
            color = if (crypto.changePercent >= 0) PriceUp else PriceDown,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Plus haut (24h)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = formatPrice(crypto.highPrice, crypto.currency),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Plus bas (24h)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = formatPrice(crypto.lowPrice, crypto.currency),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CryptoDetailContentPreview() {
    FollowMyCryptoTheme {
        CryptoDetailContent(
            symbol = "BTC",
            uiState = CryptoDetailUiState.Success(
                Crypto(
                    symbol = "BTC",
                    name = "Bitcoin",
                    price = 85613.50,
                    changePercent = 0.88,
                    highPrice = 86200.00,
                    lowPrice = 84500.00,
                    currency = Currency.USD,
                ),
            ),
            onBack = {},
            onRetry = {},
        )
    }
}
