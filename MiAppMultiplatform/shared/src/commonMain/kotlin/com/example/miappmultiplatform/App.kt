package com.example.miappmultiplatform

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.IllegalTimeZoneException
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import miappmultiplatform.shared.generated.resources.Res
import miappmultiplatform.shared.generated.resources.eg
import miappmultiplatform.shared.generated.resources.fr
import miappmultiplatform.shared.generated.resources.id
import miappmultiplatform.shared.generated.resources.jp
import miappmultiplatform.shared.generated.resources.mx
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

data class Country(
    val name: String,
    val utcOffset: Int,
    val image: DrawableResource
)

fun currentTimeAt(location: String, offset: Int): String {

    fun Int.pad() = toString().padStart(2, '0')

    val now = Clock.System.now()

    val utc = now.toLocalDateTime(TimeZone.UTC)

    var hour = utc.hour + offset

    if (hour >= 24) hour -= 24
    if (hour < 0) hour += 24

    return "The time in $location is " +
            "${hour.pad()}:${utc.minute.pad()}:${utc.second.pad()}"
}

val defaultCountries = listOf(
    Country("Japan", 9, Res.drawable.jp),
    Country("France", 1, Res.drawable.fr),
    Country("Mexico", -6, Res.drawable.mx),
    Country("Indonesia", 7, Res.drawable.id),
    Country("Egypt", 2, Res.drawable.eg)
)

@Composable
@Preview
fun App(countries: List<Country> = defaultCountries) {
    MaterialTheme {
        var showCountries by remember { mutableStateOf(false) }
        var timeAtLocation by remember { mutableStateOf("No location selected") }

        Column(
            modifier = Modifier
                .padding(20.dp)
                .safeContentPadding()
                .fillMaxSize(),
        ) {
            Text(
                timeAtLocation,
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
            )
            Row(modifier = Modifier.padding(start = 20.dp, top = 10.dp)) {
                DropdownMenu(
                    expanded = showCountries,
                    onDismissRequest = { showCountries = false }
                ) {
                    countries.forEach { (name, offset, image) ->
                        DropdownMenuItem(
                            text = { Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painterResource(image),
                                    modifier = Modifier.size(50.dp).padding(end = 10.dp),
                                    contentDescription = "$name flag"
                                )
                                Text(name)
                            } },
                            onClick = {
                                timeAtLocation = currentTimeAt(name, offset)
                                showCountries = false
                            }
                        )
                    }
                }
            }

            Button(modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                onClick = { showCountries = !showCountries }) {
                Text("Select Location")
            }
        }
    }
}