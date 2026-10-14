package com.bvic.followmycrypto.data

import com.bvic.followmycrypto.model.Crypto
import kotlinx.coroutines.delay

private val mockCryptos = listOf(
    Crypto(symbol = "BTC", name = "Bitcoin", price = 85613.50, changePercent = 0.88, highPrice = 86200.00, lowPrice = 84500.00),
    Crypto(symbol = "ETH", name = "Ethereum", price = 3214.07, changePercent = -1.42, highPrice = 3300.00, lowPrice = 3180.00),
    Crypto(symbol = "BNB", name = "BNB", price = 642.30, changePercent = 0.15, highPrice = 650.00, lowPrice = 638.00),
    Crypto(symbol = "SOL", name = "Solana", price = 148.92, changePercent = 4.31, highPrice = 152.00, lowPrice = 142.00),
    Crypto(symbol = "XRP", name = "XRP", price = 0.5873, changePercent = -0.64, highPrice = 0.6000, lowPrice = 0.5750),
    Crypto(symbol = "ADA", name = "Cardano", price = 0.4521, changePercent = 2.07, highPrice = 0.4650, lowPrice = 0.4400),
    Crypto(symbol = "DOGE", name = "Dogecoin", price = 0.1284, changePercent = -3.95, highPrice = 0.1350, lowPrice = 0.1250),
    Crypto(symbol = "SHIB", name = "Shiba Inu", price = 0.00001834, changePercent = 1.12, highPrice = 0.00001900, lowPrice = 0.00001800),
)

// Un faux repository : il simule un serveur lent, sans réseau
class FakeCryptoRepository : CryptoRepository {

    override suspend fun getCryptos(): List<Crypto> {
        delay(1_500)
        return mockCryptos
    }

    override suspend fun getCrypto(symbol: String): Crypto {
        delay(500)
        return mockCryptos.first { it.symbol == symbol }
    }
}
