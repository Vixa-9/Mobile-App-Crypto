# Evaluations choisies

~~1. Le plus bas et le plus haut sur 24H~~
2. Trier la liste
3. Un convertisseur 
~~4. Changer la devise~~
5. Garder les favoris après la fermeture
~~6. Créer notre propre thème~~
~~7. Choisir le thème clair ou sombre~~

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

## 4 - Changer la devise
### Couche Modèle
Création d'une énumération Currency et ajout du champ currency au modèle Crypto.
- Currency.kt :

```
enum class Currency(val code: String, val symbol: String) {
    USD(code = "USDT", symbol = "$"),
    EUR(code = "EUR", symbol = "€"),
}
```

- Crypto.kt :

```
data class Crypto(
    val symbol: String,
    val name: String,
    val price: Double,
    val changePercent: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val currency: Currency = Currency.USD,
)
```

### Couche Repository
Mise à jour de l'interface CryptoRepository pour accepter la devise, et construction dynamique des paires Binance dans RemoteCryptoRepository.
- CryptoRepository.kt :

```
interface CryptoRepository {
    suspend fun getCryptos(currency: Currency = Currency.USD): List<Crypto>

    suspend fun getCrypto(symbol: String, currency: Currency = Currency.USD): Crypto
}
```

- RemoteCryptoRepository.kt :

```
class RemoteCryptoRepository(private val api: BinanceApi) : CryptoRepository {

    override suspend fun getCryptos(currency: Currency): List<Crypto> {
        val pairs = cryptoBaseNames.keys.map { it + currency.code }
        val tickers = api.getTickers(pairs)
        return tickers.map { ticker -> ticker.toCrypto(currency) }
    }

    override suspend fun getCrypto(symbol: String, currency: Currency): Crypto {
        val tickers = api.getTickers(listOf(symbol + currency.code))
        return tickers.first().toCrypto(currency)
    }
}
```

Ajustement des prix simulés selon la devise sélectionnée.
- FakeCryptoRepository.kt :

```
override suspend fun getCryptos(currency: Currency): List<Crypto> {
    delay(1_500)
    val rate = if (currency == Currency.EUR) 0.92 else 1.0
    return mockCryptos.map { crypto ->
        crypto.copy(
            price = crypto.price * rate,
            highPrice = crypto.highPrice * rate,
            lowPrice = crypto.lowPrice * rate,
            currency = currency,
        )
    }
}
```

### Couche Formateur
Prise en compte de la devise passée en paramètre pour afficher le bon symbole ($ ou €).
- Format.kt :

```
fun formatPrice(value: Double, currency: Currency = Currency.USD): String {
    val decimals = when {
        value >= 1.0 -> 2
        value >= 0.01 -> 4
        else -> 8
    }
    return "${formatNumber(value, decimals)} ${currency.symbol}"
}
```

### Couche ViewModel
Ajout de l'état selectedCurrency et de la méthode selectCurrency pour recharger les cours selon la devise choisie.
- CryptoListViewModel.kt :

```
private val _selectedCurrency = MutableStateFlow(Currency.USD)
val selectedCurrency: StateFlow<Currency> = _selectedCurrency.asStateFlow()

fun selectCurrency(currency: Currency) {
    if (_selectedCurrency.value != currency) {
        _selectedCurrency.value = currency
        load()
    }
}
```

- CryptoDetailViewModel.kt :

```
private val _selectedCurrency = MutableStateFlow(initialCurrency)
val selectedCurrency: StateFlow<Currency> = _selectedCurrency.asStateFlow()

fun selectCurrency(currency: Currency) {
    if (_selectedCurrency.value != currency) {
        _selectedCurrency.value = currency
        load()
    }
}
```

### Couche Utilisateur
Ajout d'un composant CurrencySelector (Chips $ / €) dans la barre supérieure TopAppBar des écrans de liste et de détail.
- CryptoListScreen.kt :

```
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
```

- CryptoDetailScreen.kt :

```
actions = {
    CurrencySelector(
        selectedCurrency = selectedCurrency,
        onCurrencySelected = onCurrencySelected,
    )
}
```

## 6 - Créer notre propre thème
Les écrans utilisaient déjà les couleurs et les styles du thème (MaterialTheme.colorScheme, MaterialTheme.typography) : seul le dossier theme/ et les ressources ont été modifiés, aucun écran n'a été touché.

### Couche Thème - Couleurs
Palette générée avec le Material Theme Builder (couleur principale bleu canard), en version claire et sombre. Seules les couleurs Light et Dark ont été gardées (les variantes MediumContrast et HighContrast ont été retirées). Les couleurs des variations PriceUp et PriceDown sont conservées à la fin du fichier.
- Color.kt :

```
val primaryLight = Color(0xFF006879)
val onPrimaryLight = Color(0xFFFFFFFF)
val backgroundLight = Color(0xFFF5FAFC)
...
val primaryDark = Color(0xFF84D2E6)
val onPrimaryDark = Color(0xFF003640)
val backgroundDark = Color(0xFF0F1416)
...

// Couleurs des variations de prix
val PriceUp = Color(0xFF0ECB81)
val PriceDown = Color(0xFFF6465D)
```

Association de chaque couleur à son rôle Material (primary, background, surface...) dans les deux palettes lightScheme et darkScheme. Le code spécifique à Android de l'export (couleurs dynamiques, LocalContext) n'a pas été repris car il ne compile pas sur Desktop.
- Theme.kt :

```
private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    background = backgroundLight,
    ...
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    background = backgroundDark,
    ...
)
```

### Couche Ressources - Polices
Ajout de deux polices Google Fonts dans les ressources Compose du projet : ADLaM Display pour les titres et ABeeZee pour le texte. Elles sont embarquées dans l'application (pas de téléchargement au lancement), ce qui fonctionne sur Android comme sur Desktop.
- composeResources/font/ :

```
ABeeZee-Regular.ttf
ADLaMDisplay-Regular.ttf
```

### Couche Thème - Typographie
Création de la typographie : on garde les tailles par défaut de Material 3 et on change uniquement la police, ADLaM Display pour les styles display, headline et title, ABeeZee pour les styles body et label.
- Type.kt :

```
@Composable
fun appTypography(): Typography {
    val displayFont = FontFamily(Font(Res.font.ADLaMDisplay_Regular))
    val bodyFont = FontFamily(Font(Res.font.ABeeZee_Regular))
    val base = Typography()
    return Typography(
        displayLarge = base.displayLarge.copy(fontFamily = displayFont),
        ...
        titleSmall = base.titleSmall.copy(fontFamily = displayFont),
        bodyLarge = base.bodyLarge.copy(fontFamily = bodyFont),
        ...
        labelSmall = base.labelSmall.copy(fontFamily = bodyFont),
    )
}
```

Branchement des palettes et de la typographie dans le thème de l'application.
- Theme.kt :

```
@Composable
fun FollowMyCryptoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkScheme else lightScheme,
        typography = appTypography(),
        content = content,
    )
}
```

## 7 - Choisir le thème clair ou sombre
### Hissage de l'état
L'état « thème sombre » est placé tout en haut, dans App(), pour que le thème et les deux écrans le voient. Au premier lancement il vaut le réglage du système. rememberSaveable garde le choix quand l'écran est recréé (rotation). L'état descend vers les écrans, le clic remonte avec toggleTheme.
- App.kt :

```
val systemDark = isSystemInDarkTheme()
var darkTheme by rememberSaveable { mutableStateOf(systemDark) }
val toggleTheme = { darkTheme = !darkTheme }

FollowMyCryptoTheme(darkTheme = darkTheme) {
    ...
    CryptoListScreen(
        onCryptoClick = { symbol -> navController.navigate(DetailRoute(symbol)) },
        darkTheme = darkTheme,
        onToggleTheme = toggleTheme,
    )
    ...
    CryptoDetailScreen(
        symbol = route.symbol,
        onBack = { navController.popBackStack() },
        darkTheme = darkTheme,
        onToggleTheme = toggleTheme,
    )
}
```

### Couche Utilisateur
Création d'un composant réutilisable ThemeToggleButton qui affiche le thème vers lequel on va basculer. Des emojis sont utilisés car le jeu d'icônes material-icons-core du projet ne contient ni soleil ni lune.
- ThemeToggleButton.kt :

```
@Composable
fun ThemeToggleButton(darkTheme: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val label = if (darkTheme) "Passer au thème clair" else "Passer au thème sombre"
    IconButton(onClick = onToggle, modifier = modifier.semantics { contentDescription = label }) {
        Text(if (darkTheme) "☀️" else "🌙")
    }
}
```

Ajout des paramètres darkTheme et onToggleTheme aux écrans, transmis de Screen à Content, puis du bouton dans la barre supérieure TopAppBar. Dans les composables Content, ces paramètres ont une valeur par défaut pour que les aperçus preview n'aient rien à préciser.
- CryptoListScreen.kt :

```
fun CryptoListContent(
    ...
    darkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    modifier: Modifier = Modifier,
)

actions = {
    CurrencySelector(...)
    ThemeToggleButton(darkTheme = darkTheme, onToggle = onToggleTheme)
    IconButton(onClick = onRefresh) { ... }
}
```

- CryptoDetailScreen.kt :

```
actions = {
    CurrencySelector(...)
    ThemeToggleButton(darkTheme = darkTheme, onToggle = onToggleTheme)
}
```

Le choix n'est pas conservé après la fermeture de l'application : au lancement suivant, elle suit de nouveau le réglage du système.
