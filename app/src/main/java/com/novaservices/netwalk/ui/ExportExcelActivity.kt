package com.novaservices.netwalk.ui

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.novaservices.netwalk.adapter.CaseAdapter
import com.novaservices.netwalk.adapter.TicketsAdapter
import com.novaservices.netwalk.databinding.ActivityExportExcelBinding
import com.novaservices.netwalk.domain.Operations
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ExportExcelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExportExcelBinding
    private lateinit var adapter: TicketsAdapter

    var listaRegistros = listOf<Operations>()

    private lateinit var solicitarPermisos: ActivityResultLauncher<Array<String>>

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityExportExcelBinding.inflate(layoutInflater)
            setContentView(binding.root)

            solicitarPermisos = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                val aceptados = it.all { it.value }
                if (aceptados) {
                    // OPERACIONES PERMITIDAS
                } else {
                    Toast.makeText(this, "SE TIENE QUE ACEPTAR TODOS LOS PERMISOS", Toast.LENGTH_SHORT).show()
                }
            }

            permisos()

            setupRecyclerView()

            binding.btnAgregar.setOnClickListener {
                val lista = listaRegistros.toMutableList()
                lista.add(
                    Operations(
                    "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "sd",
                        "",
                        "",
                        "",
                        "",
                        ""
                    )
                )
                listaRegistros = lista

                binding.etEdad.setText("")

                setupRecyclerView()
            }

            binding.btnEscribir.setOnClickListener {
//                if (listaRegistros.isNotEmpty()) {
//                }

                crearExcel(listaRegistros.toMutableList())
            }

//            binding.btnLeer.setOnClickListener {
//                listaRegistros = leerExcel(listaRegistros.toMutableList())
//                setupRecyclerView()
//            }

        }

        private fun setupRecyclerView() {
            adapter = TicketsAdapter(listaRegistros) {
                onItemSelected(it)
            }
            binding.rvListaRegistros.adapter = adapter
        }

    fun onItemSelected(operations: Operations) {
        return
    }

    fun permisos() {
            solicitarPermisos.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }

//    GlobalScope

        fun crearExcel(listaRegistros: MutableList<Operations>) {
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            val fileName = "registrosssww.xlsx"

            // Crear un nuevo libro de trabajo Excel en formato .xlsx
            val workbook = XSSFWorkbook()

            // Crear una hoja de trabajo (worksheet)
            val sheet: Sheet = workbook.createSheet("Hoja 1")

            // Crear una fila en la hoja
            val headerRow = sheet.createRow(0)

            // Crear celdas en la fila
            var cell = headerRow.createCell(0)
            cell.setCellValue("Nombre")

            cell = headerRow.createCell(1)
            cell.setCellValue("Edad")

            for (index in listaRegistros.indices) {
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(listaRegistros[index].titulo)
                row.createCell(1).setCellValue(listaRegistros[index].documento_origen)
                row.createCell(2).setCellValue(listaRegistros[index].proyecto)
                row.createCell(3).setCellValue(listaRegistros[index].asignada_a)
                row.createCell(4).setCellValue(listaRegistros[index].fecha_final)
                row.createCell(5).setCellValue(listaRegistros[index].timesheet_timer_first_use)
                row.createCell(6).setCellValue(listaRegistros[index].timesheet_timer_last_use)
                row.createCell(7).setCellValue(listaRegistros[index].cluster)
                row.createCell(8).setCellValue(listaRegistros[index].fecha_inicio)
                row.createCell(9).setCellValue(listaRegistros[index].tipo_de_tarea)
                row.createCell(10).setCellValue(listaRegistros[index].etapa)
                row.createCell(11).setCellValue(listaRegistros[index].zona_de_atencion)
                row.createCell(12).setCellValue(listaRegistros[index].denominacion_comercial)
                row.createCell(13).setCellValue(listaRegistros[index].equipo)
                row.createCell(14).setCellValue(listaRegistros[index].equipo_a_instalar)
                row.createCell(15).setCellValue(listaRegistros[index].RIF)
                row.createCell(16).setCellValue(listaRegistros[index].direccion)
                row.createCell(17).setCellValue(listaRegistros[index].user_id)
                row.createCell(18).setCellValue(listaRegistros[index].region_id)
                row.createCell(19).setCellValue(listaRegistros[index].tecnico_id)
            }

            // Guardar el libro de trabajo (workbook) en almacenamiento externo
            try {
                val fileOutputStream = FileOutputStream(
                    File(path, fileName)
                )
                workbook.write(fileOutputStream)
                fileOutputStream.close()
                workbook.close()
                Toast.makeText(
                    this, "Excel Exportados Correctamente, revise sus files", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

//        fun leerExcel(listaRegistros: MutableList<Operations>): MutableList<Operations> {
//            val fileName = "registros.xlsx"
//
//            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath+"/"+fileName
//
//            Log.d("EXCEL","EXCEL LEER")
//
//            val lista = arrayListOf<String>()
//
//            try {
//                val fileInputStream = FileInputStream(path)
//                val workbook = WorkbookFactory.create(fileInputStream)
//                val sheet: Sheet = workbook.getSheetAt(0)
//
//                val rows = sheet.iterator()
//                while (rows.hasNext()) {
//                    val currentRow = rows.next()
//
//                    // Iterar sobre celdas de la fila actual
//                    val cellsInRow = currentRow.iterator()
//                    while (cellsInRow.hasNext()) {
//                        val currentCell = cellsInRow.next()
//
//                        // Obtener valor de la celda como String
//                        val cellValue: String = when (currentCell.cellType) {
//                            CellType.STRING -> currentCell.stringCellValue
//                            CellType.NUMERIC -> currentCell.numericCellValue.toString()
//                            CellType.BOOLEAN -> currentCell.booleanCellValue.toString()
//                            else -> ""
//                        }
//
//                        lista.add(cellValue)
//                    }
//                }
//
//                for (i in 2 until lista.size step 2) {
//                    listaRegistros.add(
//                        Operations(
//                            lista[i],
//                            lista[i+1]
//                        )
//                    )
//                }
//
//                workbook.close()
//                fileInputStream.close()
//
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//            return listaRegistros
//
//    }
}