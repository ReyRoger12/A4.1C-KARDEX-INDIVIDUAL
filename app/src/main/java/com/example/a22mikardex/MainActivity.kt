package com.example.a22mikardex

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.a22mikardex.data.Materia
import com.example.a22mikardex.ui.theme.A22MIKARDEXTheme
import com.example.a22mikardex.viewmodel.KardexViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            A22MIKARDEXTheme {
                KardexApp()
            }
        }
    }
}

@Composable
fun KardexApp(vm: KardexViewModel = viewModel()) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("datos_alumno", Context.MODE_PRIVATE)
    }

    var nombreAlumno by remember { mutableStateOf(prefs.getString("nombre", "") ?: "") }
    var controlAlumno by remember { mutableStateOf(prefs.getString("control", "") ?: "") }
    var nombreEdit by remember { mutableStateOf(nombreAlumno) }
    var controlEdit by remember { mutableStateOf(controlAlumno) }
    var mostrarDatos by remember { mutableStateOf(false) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var tab by remember { mutableStateOf(0) }

    val materias by vm.materias.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("A2.2 • Mi Kardex") })
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            Text(
                "Historial académico",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                if (nombreAlumno.isBlank()) "Alumno no configurado"
                else "$nombreAlumno • $controlAlumno"
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { mostrarDatos = true }) { Text("Datos") }
                Button(onClick = { mostrarFormulario = true }) { Text("Agregar materia") }
            }

            Spacer(Modifier.height(12.dp))

            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Materias") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Promedios") })
            }

            Spacer(Modifier.height(8.dp))

            if (tab == 0) {
                if (materias.isEmpty()) {
                    Text(
                        "Aún no hay materias. Usa «Agregar materia» para registrar tu historial.",
                        modifier = Modifier.padding(top = 20.dp)
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(materias, key = { it.id }) { materia ->
                            MateriaCard(materia, onDelete = { vm.eliminarMateria(materia) })
                        }
                    }
                }
            } else {
                PromediosView(materias)
            }
        }
    }

    if (mostrarDatos) {
        AlertDialog(
            onDismissRequest = { mostrarDatos = false },
            title = { Text("Datos del alumno") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nombreEdit,
                        onValueChange = { nombreEdit = it },
                        label = { Text("Nombre completo") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = controlEdit,
                        onValueChange = { controlEdit = it },
                        label = { Text("Número de control") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nombreEdit.isBlank() || controlEdit.isBlank()) {
                            Toast.makeText(context, "Completa ambos datos", Toast.LENGTH_SHORT).show()
                        } else {
                            prefs.edit()
                                .putString("nombre", nombreEdit.trim())
                                .putString("control", controlEdit.trim())
                                .apply()
                            nombreAlumno = nombreEdit.trim()
                            controlAlumno = controlEdit.trim()
                            mostrarDatos = false
                            Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) { Text("Guardar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDatos = false }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarFormulario) {
        FormularioMateria(
            onDismiss = { mostrarFormulario = false },
            onGuardar = { n, s, normal, rep, esp ->
                vm.agregarMateria(n, s, normal, rep, esp)
                mostrarFormulario = false
                Toast.makeText(context, "Materia registrada", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun MateriaCard(materia: Materia, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text(materia.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Semestre: ${materia.semestre}")
            Spacer(Modifier.height(6.dp))
            Text("Normal: ${nota(materia.calificacionNormal)}")
            Text("Repetición: ${nota(materia.calificacionRepeticion)}")
            Text("Especial: ${nota(materia.calificacionEspecial)}")
            Divider(Modifier.padding(vertical = 8.dp))
            Text(
                "Calificación efectiva: ${nota(materia.calificacionFinal())}",
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            OutlinedButton(onClick = onDelete) { Text("Eliminar") }
        }
    }
}

@Composable
private fun FormularioMateria(
    onDismiss: () -> Unit,
    onGuardar: (String, String, Double, Double, Double) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var semestre by remember { mutableStateOf("") }
    var normal by remember { mutableStateOf("") }
    var repeticion by remember { mutableStateOf("") }
    var especial by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar materia") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre de la materia") }, singleLine = true)
                }
                item {
                    OutlinedTextField(semestre, { semestre = it }, label = { Text("Semestre") }, singleLine = true)
                }
                item {
                    OutlinedTextField(normal, { normal = it }, label = { Text("Calificación Normal") }, singleLine = true)
                }
                item {
                    OutlinedTextField(repeticion, { repeticion = it }, label = { Text("Calificación Repetición") }, singleLine = true)
                }
                item {
                    OutlinedTextField(especial, { especial = it }, label = { Text("Calificación Especial") }, singleLine = true)
                }
                if (error.isNotBlank()) item { Text(error, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            Button(onClick = {
                val n = normal.replace(',', '.').toDoubleOrNull()
                val r = repeticion.replace(',', '.').toDoubleOrNull() ?: 0.0
                val e = especial.replace(',', '.').toDoubleOrNull() ?: 0.0
                when {
                    nombre.isBlank() || semestre.isBlank() -> error = "Nombre y semestre son obligatorios."
                    n == null -> error = "La calificación Normal debe ser numérica."
                    n !in 0.0..10.0 || r !in 0.0..10.0 || e !in 0.0..10.0 ->
                        error = "Las calificaciones deben estar entre 0 y 10."
                    else -> onGuardar(nombre, semestre, n, r, e)
                }
            }) { Text("Guardar") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun PromediosView(materias: List<Materia>) {
    val general = if (materias.isEmpty()) 0.0
    else materias.map { it.calificacionFinal() }.average()

    val porSemestre = materias
        .groupBy { it.semestre }
        .toList()
        .sortedBy { it.first }

    Column {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Promedio general", style = MaterialTheme.typography.titleMedium)
                Text(
                    nota(general),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("Basado en ${materias.size} materia(s).")
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("Promedio por semestre", style = MaterialTheme.typography.titleLarge)

        if (porSemestre.isEmpty()) {
            Text("No hay materias registradas.", Modifier.padding(top = 12.dp))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(porSemestre, key = { it.first }) { (semestre, lista) ->
                    val promedio = lista.map { it.calificacionFinal() }.average()
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Semestre $semestre", fontWeight = FontWeight.Bold)
                                Text("${lista.size} materia(s)")
                            }
                            Text(nota(promedio), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private fun nota(valor: Double): String =
    String.format(Locale.US, "%.2f", valor)

