package com.opsat.subscribity.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.opsat.subscribity.R

private val archivoProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val archivoFont = GoogleFont("Archivo")

val Archivo = FontFamily(
    Font(archivoFont, archivoProvider, FontWeight.Normal),
    Font(archivoFont, archivoProvider, FontWeight.Medium),
    Font(archivoFont, archivoProvider, FontWeight.SemiBold),
    Font(archivoFont, archivoProvider, FontWeight.ExtraBold),
)

private const val TabularNums = "tnum"

val PlateLabel = TextStyle(
    fontFamily = Archivo,
    fontSize = 13.sp,
    fontWeight = FontWeight.ExtraBold,
    letterSpacing = 0.20.em,
)

val MicroLabel = TextStyle(
    fontFamily = Archivo,
    fontSize = 9.5.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.20.em,
)

val FigureXL = TextStyle(
    fontFamily = Archivo,
    fontSize = 44.sp,
    fontWeight = FontWeight.ExtraBold,
    letterSpacing = (-0.03).em,
    fontFeatureSettings = TabularNums,
)

val FigureM = TextStyle(
    fontFamily = Archivo,
    fontSize = 26.sp,
    fontWeight = FontWeight.ExtraBold,
    fontFeatureSettings = TabularNums,
)

val RowName = TextStyle(
    fontFamily = Archivo,
    fontSize = 19.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = (-0.01).em,
)

val RowAmount = TextStyle(
    fontFamily = Archivo,
    fontSize = 21.sp,
    fontWeight = FontWeight.SemiBold,
    fontFeatureSettings = TabularNums,
)

val RowCaption = TextStyle(
    fontFamily = Archivo,
    fontSize = 11.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.12.em,
)

val FieldValue = TextStyle(
    fontFamily = Archivo,
    fontSize = 20.sp,
    fontWeight = FontWeight.SemiBold,
)

val FieldValueLarge = TextStyle(
    fontFamily = Archivo,
    fontSize = 22.sp,
    fontWeight = FontWeight.SemiBold,
)

val ControlLabel = TextStyle(
    fontFamily = Archivo,
    fontSize = 12.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 0.12.em,
)

val BodyRow = TextStyle(
    fontFamily = Archivo,
    fontSize = 17.sp,
    fontWeight = FontWeight.Medium,
)

val LedgerTypography = Typography(
    headlineSmall = FieldValueLarge,
    titleMedium = RowName,
    bodyLarge = BodyRow,
    bodyMedium = BodyRow,
    bodySmall = RowCaption,
    labelSmall = ControlLabel,
)
