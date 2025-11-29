package net.annedawson.seismiccanada

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.annedawson.seismiccanada.data.model.EarthquakeFeature
import net.annedawson.seismiccanada.ui.theme.SeismicCanadaTheme
import net.annedawson.seismiccanada.ui.viewmodel.EarthquakeViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * MainActivity is the entry point of the Android application.
 * It inherits from ComponentActivity, which is a base class for activities that use Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge allows the app to draw behind the system bars (status bar, navigation bar)
        enableEdgeToEdge()
        
        // setContent is where we define the UI of the activity using Composable functions.
        setContent {
            // Apply the application's theme (colors, typography, shapes)
            SeismicCanadaTheme {
                // Call the main screen Composable
                EarthquakeApp()
            }
        }
    }
}

/**
 * This is the main screen of the app.
 * It uses a ViewModel to fetch and hold the data.
 * 
 * @param viewModel The source of data for this screen. It's injected automatically by default.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarthquakeApp(viewModel: EarthquakeViewModel = viewModel()) {
    // Collect the list of earthquakes from the ViewModel. 
    // 'collectAsState' converts the data flow into a State object that Compose can watch.
    // Whenever the data changes, this Composable will automatically re-draw.
    val earthquakes by viewModel.earthquakes.collectAsState()
    
    // Observe the loading state (true if data is being fetched, false otherwise)
    val isLoading by viewModel.isLoading.collectAsState()

    // Scaffold provides a standard layout structure with slots for a TopBar, FAB, etc.
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Seismic Canada - Early Warning") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        // The content of the Scaffold. 'innerPadding' ensures content isn't hidden behind the top bar.
        Column(modifier = Modifier.padding(innerPadding)) {
            // Show the static safety information card at the top
            SafetyInfoCard()
            
            // Show a loading spinner if data is being fetched, otherwise show the list
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // LazyColumn is an efficiently scrolling list (like RecyclerView in older Android views).
                // It only renders the items currently visible on screen.
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 'items' takes the list of data and a lambda to render each item
                    items(earthquakes) { earthquake ->
                        EarthquakeItem(earthquake)
                    }
                }
            }
        }
    }
}

/**
 * A simple Composable that displays static safety instructions.
 */
@Composable
fun SafetyInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = "Safety", tint = MaterialTheme.colorScheme.onTertiaryContainer)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Safety: Drop, Cover, and Hold On",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "If you feel shaking, immediately drop to the ground, cover your head and neck with your arms, and crawl to shelter under a sturdy desk or table.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

/**
 * Displays a single earthquake item in the list.
 *
 * @param feature The data object containing details about one earthquake.
 */
@Composable
fun EarthquakeItem(feature: EarthquakeFeature) {
    // Determine the color based on magnitude for visual urgency
    val magnitudeColor = if (feature.properties.mag >= 5.0) Color.Red else if (feature.properties.mag >= 3.0) Color(0xFFFFA500) else Color.Green.copy(alpha = 0.6f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display the magnitude in a colored box
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(magnitudeColor, shape = MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%.1f", feature.properties.mag),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            
            // Display the place and time details
            Column {
                Text(
                    text = feature.properties.place,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = formatTime(feature.properties.time),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                if (!feature.properties.alert.isNullOrEmpty()) {
                    Text(
                        text = "Alert Level: ${feature.properties.alert}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Helper function to format a timestamp (milliseconds) into a readable date string.
 */
fun formatTime(timeInMillis: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timeInMillis))
}
