package com.alpharez.t1base

import android.app.Application
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alpharez.t1base.ui.theme.T1BaseTheme
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.alpharez.t1base.data.Medicine
import java.util.Calendar
import java.util.Date


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            T1BaseTheme {
                val owner = LocalViewModelStoreOwner.current

                owner?.let {
                    val viewMod: T1BaseViewModel = viewModel(it, "medicine_database", T1BaseViewModelFactory(
                        LocalContext.current.applicationContext as Application)
                    )
                    val allMedicines by viewMod.allMedicines.observeAsState(listOf())
                    Medications(meds = allMedicines, viewModel = viewMod)
                }
            }
        }
    }
}

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Medications(modifier: Modifier = Modifier, meds: List<Medicine>, viewModel: T1BaseViewModel) {
    var showAddMedication by rememberSaveable { mutableStateOf(false) }
    var medName by remember { mutableStateOf("Name") }
    var medDosageForm by remember { mutableStateOf("Form") }
    var medFrequency by remember { mutableStateOf("1") }

    Surface(modifier) {
        Scaffold(
            floatingActionButton = {
                AddMedFloatingActionButton(onClick = { showAddMedication = true })
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    if (showAddMedication) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center, text = "SHOW NEW MED"
                        )
                    } else {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center, text = "~~~ NAV BAR ~~~"
                        )
                    }
                }
            },
        ) { innerPadding ->
            if (!showAddMedication) { /* SHOW MEDICATION LIST */
                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(items = meds) { med ->
                        Medication(med = med, viewModel = viewModel)
                    }
                }
            } else { /* ADD NEW MEDICATION */
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()) {
                    Text("ADD MEDICATION")
                    TextField(
                        value = medName,
                        onValueChange = { medName = it },
                        label = { Text("Medication Name") }
                    )
                    TextField(
                        value = medDosageForm,
                        onValueChange = { medDosageForm = it },
                        label = { Text("DosageForm") }
                    )
                    TextField(
                        value = medFrequency,
                        onValueChange = { medFrequency = it },
                        label = { Text("Take How Often: Daily, Every 3 Days, Weekly") }
                    )
                    Button(onClick = {
                        viewModel.insertMedicine(Medicine(medName, medDosageForm, medFrequency.toInt(), Date(0)))
                        showAddMedication = false
                    }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
fun Medication(med: Medicine, viewModel: T1BaseViewModel, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val extraPadding by animateDpAsState(
        if (expanded) 8.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "animation padding"
    )
    var medFrequency = ""
    var dayOffset = 1
    if (med.frequency == 3) {
        medFrequency = "Weekly"
        dayOffset = 7

    } else if (med.frequency == 2) {
        medFrequency = "Every 3 Days"
        dayOffset = 3
    } else {
        medFrequency = "Daily"
    }
    var dt = med.lastTaken
    var c = Calendar.getInstance()
    c.setTime(dt)
    c.add(Calendar.DATE, dayOffset)
    dt = c.time
    Card(colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(modifier = modifier
                .weight(1f)
                .padding(bottom = extraPadding.coerceAtLeast(0.dp))) {
                Text(med.medicineName, style = MaterialTheme.typography.headlineSmall)
                Text(med.dosageForm + ", Take " + medFrequency)
                if(expanded) {
                    Column {
                        Text("Last Taken: " + med.lastTaken)
                        Text("Take Next: $dt")
                        Row {
                            IconButton(onClick = {
                                viewModel.deleteMedicine(med)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_delete_24),
                                    contentDescription = "delete", modifier = Modifier.size(40.dp)
                                )
                            }
                            IconButton(onClick = {
                                /* EDIT */
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_edit_note_24),
                                    contentDescription = "edit", modifier = Modifier.size(40.dp)
                                )
                            }
                            IconButton(onClick = {
                                /* Postpone */
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_access_time_24),
                                    contentDescription = "postpone", modifier = Modifier.size(40.dp)
                                )
                            }
                            IconButton(onClick = {
                                med.lastTaken = Date()
                                viewModel.takeMedicine(med)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_medication_24),
                                    contentDescription = "Take Medicine", modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                }
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if(expanded) Filled.ExpandLess else Filled.ExpandMore,
                    contentDescription = if(expanded) {
                        "show less"
                    } else {
                        "show more"
                    }
                )
            }
        }
    }
}

@Composable
fun AddMedFloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer, // Customize the icon color
    ) {
        //Icon(Icons.Default.Add, contentDescription = "Add Button")
        Icon(Icons.Filled.Add, "Floating action button.") // Add an icon
    }
}

@Preview(
    showBackground = true,
    //widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "T1BasePreviewDark"
)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun T1BasePreview() {
    T1BaseTheme {
        val owner = LocalViewModelStoreOwner.current

        owner?.let {
            val viewMod: T1BaseViewModel = viewModel(it, "medicine_database", T1BaseViewModelFactory(
                LocalContext.current.applicationContext as Application)
            )
            val allMedicines by viewMod.allMedicines.observeAsState(listOf())
            Medications(meds = allMedicines, viewModel = viewMod)
        }
    }
}

class T1BaseViewModelFactory(val application: Application): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return T1BaseViewModel(application) as T
    }
}