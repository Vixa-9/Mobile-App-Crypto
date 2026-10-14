package com.bvic.followmycrypto.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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

@Composable
fun CryptoListScreen(
    onCryptoClick: (String) -> Unit,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CryptoListViewModel = viewModel { CryptoListViewModel(AppContainer.cryptoRepository) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    CryptoListContent(
        uiState = uiState,
        selectedCurrency = selectedCurrency,
        onCurrencySelected = viewModel::selectCurrency,
        onRefresh = viewModel::refresh,
        onCryptoClick = onCryptoClick,
        darkTheme = darkTheme,
        onToggleTheme = onToggleTheme,
        modifier = modifier,
    )
}

// Version sans état : elle ne connaît pas le ViewModel, on peut donc l'afficher dans un aperçu
@Composable
fun CryptoListContent(
    uiState: CryptoListUiState,
    selectedCurrency: Currency = Currency.USD,
    onCurrencySelected: (Currency) -> Unit = {},
    onRefresh: () -> Unit = {},
    onCryptoClick: (String) -> Unit = {},
    darkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("FollowMyCrypto", fontWeight = FontWeight.Bold) },
                    actions = {
                        ThemeToggleButton(
                            darkTheme = darkTheme,
                            onToggle = onToggleTheme,
                        )
                        IconButton(onClick = onRefresh) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Rafraîchir")
                        }
                    },
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CurrencySelector(
                        selectedCurrency = selectedCurrency,
                        onCurrencySelected = onCurrencySelected,
                    )
                }
            }
        },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding).fillMaxSize()
        when (uiState) {
            CryptoListUiState.Loading -> LoadingContent(modifier = contentModifier)
            is CryptoListUiState.Error -> ErrorContent(
                message = uiState.message,
                onRetry = onRefresh,
                modifier = contentModifier,
            )
            is CryptoListUiState.Success -> CryptoList(
                cryptos = uiState.cryptos,
                onCryptoClick = onCryptoClick,
                modifier = contentModifier,
            )
        }
    }
}

@Composable
fun CurrencySelector(
    selectedCurrency: Currency,
    onCurrencySelected: (Currency) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Currency.entries.forEach { currency ->
            FilterChip(
                selected = selectedCurrency == currency,
                onClick = { onCurrencySelected(currency) },
                label = { Text(currency.symbol) },
            )
        }
    }
}

@Composable
fun CryptoList(
    cryptos: List<Crypto>,
    onCryptoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = cryptos, key = { it.symbol }) { crypto ->
            CryptoRow(crypto = crypto, onClick = { onCryptoClick(crypto.symbol) })
            HorizontalDivider()
        }
    }
}

@Composable
fun CryptoRow(crypto: Crypto, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = crypto.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = crypto.symbol,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatPrice(crypto.price, crypto.currency),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = formatPercent(crypto.changePercent),
                style = MaterialTheme.typography.bodySmall,
                color = if (crypto.changePercent >= 0) PriceUp else PriceDown,
            )
        }
    }
}

private val previewCryptos = listOf(
    Crypto(symbol = "BTC", name = "Bitcoin", price = 85613.50, changePercent = 0.88, highPrice = 86200.00, lowPrice = 84500.00, currency = Currency.USD),
    Crypto(symbol = "ETH", name = "Ethereum", price = 3214.07, changePercent = -1.42, highPrice = 3300.00, lowPrice = 3180.00, currency = Currency.USD),
)

@Preview(showBackground = true)
@Composable
fun CryptoRowPreview() {
    FollowMyCryptoTheme {
        CryptoRow(crypto = previewCryptos.first(), onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun CryptoListContentPreview() {
    FollowMyCryptoTheme {
        CryptoListContent(uiState = CryptoListUiState.Success(previewCryptos), onRefresh = {}, onCryptoClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun CryptoListErrorPreview() {
    FollowMyCryptoTheme {
        CryptoListContent(uiState = CryptoListUiState.Error("Impossible de charger les cours."), onRefresh = {}, onCryptoClick = {})
    }
}
