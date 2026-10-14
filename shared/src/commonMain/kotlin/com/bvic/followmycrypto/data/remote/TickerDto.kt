package com.bvic.followmycrypto.data.remote

import kotlinx.serialization.Serializable

// Binance renvoie tous les nombres sous forme de String : "85613.50000000"
@Serializable
data class TickerDto(
    val symbol: String = "",
    val lastPrice: String = "0.0",
    val priceChangePercent: String = "0.0",
    val highPrice: String = "0.0",
    val lowPrice: String = "0.0",
)
