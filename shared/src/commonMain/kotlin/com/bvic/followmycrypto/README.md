# Evaluations choisies

1. Le plus bas et le plus haut sur 24H
2. Trier la liste
3. Un convertisseur 
4. Changer la devise
5. Garder les favoris après la fermeture

## 1 - Le plus bas et le plus haut sur 24H
### Couche donées
Ajout des champs hightPrice et lowerPrice reçus de l'API Biance sous forme de String.
- TickerDto :

```
val highPrice: String = "0.0",
val lowPrice: String = "0.0",
```

### Couche Modèle
Ajout des propriétés val hightPrice: Double et val lowPrice au modèle métier pur
- Crypto.tk :

```
val highPrice: Double,
val lowPrice: Double,
```
### Couche Repository
Conversion des champs hightPrice et lowerPrice du TicketDto vers Double dans la fonction TickerDto.ToCrypto()
- RemoteCryptoRepository.kt

```
private fun TickerDto.toCrypto(): Crypto {
    return Crypto(
        symbol = symbol.removeSuffix("USDT"),
        name = cryptoNames[symbol] ?: symbol,
        price = lastPrice.toDoubleOrNull() ?: 0.0,
        changePercent = priceChangePercent.toDoubleOrNull() ?: 0.0,
        highPrice = highPrice.toDoubleOrNull() ?: 0.0,
        lowPrice = lowPrice.toDoubleOrNull() ?: 0.0,
    )
}
```
Mise à jour des objets fictifs mockCryptos pour inclure des valeurs réalistes de prix plus haut et plus bas.
- FakeCryptoRepository.kt 

```
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
```

### Couche Utilisateur
Affichage côte à côte du prix le plus haut et du prix le plus bas sur 24H juste en dessous de la variation, avec le formatage de prix formatPrice.

- CryptoDetailScreen.kt
```
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
                    text = formatPrice(crypto.highPrice),
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
                    text = formatPrice(crypto.lowPrice),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
```

Mise à jour des mocks pour les aperçus preview
- CryptoListScreen.kt

```
private val previewCryptos = listOf(
    Crypto(symbol = "BTC", name = "Bitcoin", price = 85613.50, changePercent = 0.88, highPrice = 86200.00, lowPrice = 84500.00),
    Crypto(symbol = "ETH", name = "Ethereum", price = 3214.07, changePercent = -1.42, highPrice = 3300.00, lowPrice = 3180.00),
)
```