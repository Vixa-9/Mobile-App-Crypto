package com.bvic.followmycrypto.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import followmycrypto.shared.generated.resources.Res
import followmycrypto.shared.generated.resources.ABeeZee_Regular
import followmycrypto.shared.generated.resources.ADLaMDisplay_Regular
import org.jetbrains.compose.resources.Font

// Police des titres et police du texte, chargées depuis composeResources/font
@Composable
fun appTypography(): Typography {
    val displayFont = FontFamily(Font(Res.font.ADLaMDisplay_Regular))
    val bodyFont = FontFamily(Font(Res.font.ABeeZee_Regular))
    val base = Typography()
    return Typography(
        displayLarge = base.displayLarge.copy(fontFamily = displayFont),
        displayMedium = base.displayMedium.copy(fontFamily = displayFont),
        displaySmall = base.displaySmall.copy(fontFamily = displayFont),
        headlineLarge = base.headlineLarge.copy(fontFamily = displayFont),
        headlineMedium = base.headlineMedium.copy(fontFamily = displayFont),
        headlineSmall = base.headlineSmall.copy(fontFamily = displayFont),
        titleLarge = base.titleLarge.copy(fontFamily = displayFont),
        titleMedium = base.titleMedium.copy(fontFamily = displayFont),
        titleSmall = base.titleSmall.copy(fontFamily = displayFont),
        bodyLarge = base.bodyLarge.copy(fontFamily = bodyFont),
        bodyMedium = base.bodyMedium.copy(fontFamily = bodyFont),
        bodySmall = base.bodySmall.copy(fontFamily = bodyFont),
        labelLarge = base.labelLarge.copy(fontFamily = bodyFont),
        labelMedium = base.labelMedium.copy(fontFamily = bodyFont),
        labelSmall = base.labelSmall.copy(fontFamily = bodyFont),
    )
}