package com.bvic.followmycrypto.data

import com.bvic.followmycrypto.data.remote.BinanceApi
import com.bvic.followmycrypto.data.remote.TickerDto
import com.bvic.followmycrypto.model.Crypto
import com.bvic.followmycrypto.model.Currency

// Les cryptos suivies : le symbole de base et le nom à afficher (Binance ne le fournit pas)
private val cryptoBaseNames = mapOf(
    "BTC" to "Bitcoin",
    "ETH" to "Ethereum",
    "BNB" to "BNB",
    "SOL" to "Solana",
    "XRP" to "XRP",
    "ADA" to "Cardano",
    "DOGE" to "Dogecoin",
    "SHIB" to "Shiba Inu",
)

class RemoteCryptoRepository(private val api: BinanceApi) : CryptoRepository {

    override suspend fun getCryptos(currency: Currency): List<Crypto> {
        // 1. Les paires à demander
        val pairs = cryptoBaseNames.keys.map { it + currency.code }
        // 2. L'appel à l'API
        val tickers = api.getTickers(pairs)
        // 3. La conversion de chaque DTO en modèle
        return tickers.map { ticker -> ticker.toCrypto(currency) }
    }

    override suspend fun getCrypto(symbol: String, currency: Currency): Crypto {
        val tickers = api.getTickers(listOf(symbol + currency.code))
        return tickers.first().toCrypto(currency)
    }
}

// DTO (ce que l'API envoie) → modèle (ce dont l'application a besoin)
private fun TickerDto.toCrypto(currency: Currency): Crypto {
    val baseSymbol = symbol.removeSuffix(currency.code)
    return Crypto(
        symbol = baseSymbol,
        name = cryptoBaseNames[baseSymbol] ?: baseSymbol,
        price = lastPrice.toDoubleOrNull() ?: 0.0,
        changePercent = priceChangePercent.toDoubleOrNull() ?: 0.0,
        highPrice = highPrice.toDoubleOrNull() ?: 0.0,
        lowPrice = lowPrice.toDoubleOrNull() ?: 0.0,
        currency = currency,
    )
}
