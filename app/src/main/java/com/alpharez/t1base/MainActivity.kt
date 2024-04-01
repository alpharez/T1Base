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
import androidx.compose.material3.Icon
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
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
            if (!showAddMedication) {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(items = meds) { med ->
                        Medication(med = med, viewModel = viewModel)
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()) {
                    Text("ADD MEDICATION")
                    TextField(
                        value = medName,
                        onValueChange = { medName = it },
                        label = { Text("Medication Name") }
                    )
                    Button(onClick = {
                        viewModel.insertMedicine(Medicine(medName))
                        showAddMedication = false
                    }) {
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
        if (expanded) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "animation padding"
    )
    Surface(color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(modifier = modifier
                .weight(1f)
                .padding(bottom = extraPadding.coerceAtLeast(0.dp))) {
                Text(med.medicineName, style = MaterialTheme.typography.headlineSmall)
                Text("10mg, take at 9pm")
                if(expanded) {
                    Column {
                        Text("Took last night at 8:45pm")
                        IconButton(onClick = {
                            viewModel.deleteMedicine(med)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_delete_24),
                                contentDescription = "delete", modifier = Modifier.size(40.dp)
                            )
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
    val meds = listOf<String>()
    T1BaseTheme {
            //Medications(meds = meds, viewModel = viewMod)
    }
}

class T1BaseViewModelFactory(val application: Application): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return T1BaseViewModel(application) as T
    }
}