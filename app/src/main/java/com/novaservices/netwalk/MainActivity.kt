package com.novaservices.netwalk

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.RemoteException
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.newland.me.ConnUtils
import com.newland.me.DeviceManager
import com.newland.mtype.ConnectionCloseEvent
import com.newland.mtype.ModuleType
import com.newland.mtype.conn.DeviceConnParams
import com.newland.mtype.event.DeviceEventListener
import com.newland.mtype.module.common.printer.Alignment
import com.newland.mtype.module.common.printer.EnFontSize
import com.newland.mtype.module.common.printer.ErrorCode
import com.newland.mtype.module.common.printer.FontScale
import com.newland.mtype.module.common.printer.ImageFormat
import com.newland.mtype.module.common.printer.PrintListener
import com.newland.mtype.module.common.printer.Printer
import com.newland.mtype.module.common.printer.PrinterStatus
import com.newland.mtype.module.common.printer.TextFormat
import com.newland.mtype.module.common.scanner.CameraType
import com.newland.mtype.module.common.security.K21SecurityModule
import com.newland.mtypex.nseries3.NS3ConnParams
import com.novaservices.netwalk.adapter.CaseAdapter
import com.novaservices.netwalk.databinding.ActivityMainBinding
import com.novaservices.netwalk.domain.CaseById
import com.novaservices.netwalk.domain.FinishedTicket
import com.novaservices.netwalk.domain.MerchantData
import com.novaservices.netwalk.domain.Operations
import com.novaservices.netwalk.ui.auth.LoginActivity
import com.novaservices.nova.utils.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val K21_DRIVER_NAME: String = "com.newland.me.K21Driver"
    private var deviceConnParams: DeviceConnParams? = null
    private lateinit var deviceManager: DeviceManager
    private lateinit var securityModule: K21SecurityModule
    private lateinit var printerModule: Printer
    private lateinit var cameraModule: CameraType
    private lateinit var context: Context
    private lateinit var resultTicketNumber: String
    private lateinit var resultTicketSubscripted: String
    private lateinit var resultTicketStatusFinal: String
    private lateinit var resultTicketStatusId: String
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private lateinit var imageBase64: Base64
    private lateinit var dd: CheckBox
    private lateinit var sImage: String
    private lateinit var FileToSend: File
    var vFilename: String = ""
    private lateinit var FileToSendBASE64: String

    private val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.ENGLISH)





//    private fun getDateString(time: Long) : String = simpleDateFormat.format(time * 1000L)
//
//    private fun getDateString(time: Int) : String = simpleDateFormat.format(time * 1000L)
//
//    val dt = Instant.ofEpochSecond(45603.67519675926.toLong() * 1000L)
//        .atZone(ZoneId.systemDefault())
//        .toLocalDateTime()




    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val getIntent = intent
//call a TextView object to set the string to
        binding.usernametech.text = getIntent.getStringExtra("username")
//        binding.regionName.text = getIntent.getStringExtra("region_id")

//        Toast.makeText(this@MainActivity, dt.toString(), Toast.LENGTH_LONG).show()
        when (getIntent.getStringExtra("region_id")) {
            "1" -> binding.regionName.text = "Caracas"
            "2" -> binding.regionName.text = "Centro Llanos"
            "3" -> binding.regionName.text = "Valencia"
            "4" -> binding.regionName.text = "Oriente Norte"
            "5" -> binding.regionName.text = "Merida"
            else -> {
                binding.regionName.text = "Region Por Asignar"
            }
        }
// s
//        binding.regionName.text = getIntent.getStringExtra("region_id")
        binding.xcs.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                openCamera()
            } else {
                Toast.makeText(this@MainActivity, "Sorry you're version android is not support, Min Android 6.0 (Marsmallow)", Toast.LENGTH_LONG).show()
            }
        }

//        var raw:double = 44233.8647553819;
//        long days = (long) raw;
//        double fraction = raw - days;
//
//        LocalDate epoch = LocalDate.of(1899, 12, 30);
//        LocalDate date = epoch.plusDays(days);
//        System.out.println(date);
//        > 2021-02-06 - so far, so good

//        binding.updateTicketChange.setOnClickListener {
//            Toast.makeText(this@MainActivity, "Datos del ticket actualizados", Toast.LENGTH_LONG).show()
//        }
        binding.closeTicketCierre2.setOnClickListener {
            binding.ticketCierre.visibility = View.GONE
        }

        binding.update.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val response = try {
                    val cases = CaseById(
                        getIntent.getIntExtra("id", 0).toString(),
                    )
//                RetrofitInstance.api.getAllTickets()
                    RetrofitInstance.api.getAllTicketsByUser(cases)
                } catch (error: IOException) {
                    Toast.makeText(this@MainActivity, "app error $error", Toast.LENGTH_LONG).show()
                    return@launch
                } catch (e: HttpException) {
                    Toast.makeText(this@MainActivity, "http error $e", Toast.LENGTH_LONG).show()
                    return@launch
                }
                if(response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main){
//                    if(response.body()!!.message.contains("No Existen")) {
//                        binding.noRecharges.visibility = View.VISIBLE
//                        Toast.makeText(context, "No existen Recargas", Toast.LENGTH_SHORT).show()
//                    }
                        val recyclerView = binding.classes
                        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                        recyclerView.adapter = CaseAdapter(response.body()!!.data!!) {
                            onItemSelected(it)
                        }
                    }
                }
            }
        }
        binding.closeTicketCierre5.setOnClickListener {
            binding.afiliacionModal.visibility = View.GONE
        }

        binding.searchAfiliado.setOnClickListener {
            val afNumber = binding.afiliacionNumberc.text
            Toast.makeText(this@MainActivity, afNumber, Toast.LENGTH_LONG).show()
            GlobalScope.launch(Dispatchers.IO) {
                val response = try {
                    val cases = CaseById(
                        afNumber.toString(),
                    )
//                RetrofitInstance.api.getAllTickets()
                    RetrofitInstance.api.getAllTicketsByAfiliation(cases)
                } catch (error: IOException) {
                    Log.i("afiliated", error.toString())
                    this@MainActivity.runOnUiThread(Runnable {
                        Toast.makeText(this@MainActivity, "app error $error", Toast.LENGTH_LONG).show()
                    })
                    return@launch
                } catch (e: HttpException) {
                    Log.i("afiliated", e.toString())
                    this@MainActivity.runOnUiThread(Runnable {
                        Toast.makeText(this@MainActivity, "http error $e", Toast.LENGTH_LONG).show()
                    })
                    return@launch
                }
                if(response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main){
                        Log.i("afiliated", response.body().toString())
                        binding.searcher.visibility = View.GONE
                        binding.toUpdate.visibility = View.VISIBLE
                        val fieldsToChange = response.body()!!.data!![0]
                        binding.aad.setText(response.body()!!.data!![0].direccion.toString())
                            binding.cel.setText(response.body()!!.data!![0].telefono_2.toString())
                        binding.tcv.setText(response.body()!!.data!![0].telefono_2.toString())
//                        binding.nc.setText()
                        binding.se.setText(response.body()!!.data!![0].equipo.toString())

                        binding.registerAfiliated.setOnClickListener {
                            this@MainActivity.runOnUiThread(Runnable {
                                Toast.makeText(this@MainActivity, "Actualizado con exito", Toast.LENGTH_LONG).show()
                            })

                            GlobalScope.launch(Dispatchers.IO) {
                                val response = try {
                                    val cases = MerchantData(
                                        null,
                                        binding.numerosafiliado.text.toString(),
                                        binding.cel.text.toString(),
                                        binding.pr.text.toString(),
                                        binding.aad.text.toString(),
                                        binding.tcv.text.toString(),
                                        binding.numerosafiliado.text.toString(),
                                        binding.se.text.toString(),
                                        binding.me.text.toString()
                                    )
                                    RetrofitInstance.api.insertUpdatedMerchantData(cases)
                                } catch (error: IOException) {
                                    this@MainActivity.runOnUiThread(Runnable {
                                        Toast.makeText(this@MainActivity, "app error $error", Toast.LENGTH_LONG).show()
                                    })
                                    return@launch
                                } catch (e: HttpException) {
                                    this@MainActivity.runOnUiThread(Runnable {
                                        Toast.makeText(this@MainActivity, "http error $e", Toast.LENGTH_LONG).show()
                                    })
                                    return@launch
                                }
                                if(response.isSuccessful && response.body() != null) {
                                    binding.toUpdate.visibility = View.GONE
                                    binding.afiliacionModal.visibility = View.GONE
                                }
                            }
                        }
                    }
                } else {
                    binding.toUpdate.visibility = View.VISIBLE
                }
            }
        }
        binding.afiliacionChange.setOnClickListener {
            binding.afiliacionModal.visibility = View.VISIBLE
        }
        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                val cases = CaseById(
                    getIntent.getIntExtra("id", 0).toString(),
                )
                RetrofitInstance.api.getAllTicketsByUser(cases)
            } catch (error: IOException) {
                Toast.makeText(this@MainActivity, "app error $error", Toast.LENGTH_LONG).show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(this@MainActivity, "http error $e", Toast.LENGTH_LONG).show()
                return@launch
            }
            if(response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main){
                    val recyclerView = binding.classes
                    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                    recyclerView.adapter = CaseAdapter(response.body()!!.data!!) {
                        onItemSelected(it)
                    }
                }
            }
        }

        binding.logout.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.vt.setOnClickListener {
            binding.ticketCierre3.visibility = View.VISIBLE
            binding.ticketModal.visibility = View.GONE

        }
        binding.exitoso1.setOnClickListener {
            binding.conditionals.visibility = View.GONE
            binding.observations.setText(binding.exitoso1tv.text)
        }
        binding.spx.setOnClickListener {
            binding.ticketCierre3.visibility = View.VISIBLE
            binding.ticketModal.visibility = View.GONE
            binding.exitoso1.setOnClickListener {
                binding.conditionals.visibility = View.GONE
                binding.observations.setText(binding.exitoso1tv.text)
            }
        }

        binding.ip.setOnClickListener {
            binding.ticketCierre3.visibility = View.VISIBLE
            binding.ticketModal.visibility = View.GONE
            binding.exitoso1.setOnClickListener {
                binding.conditionals.visibility = View.GONE
                binding.observations.setText(binding.exitoso1tv.text)
            }
        }
//
        binding.retiro.setOnClickListener {
            binding.ticketCierre3.visibility = View.VISIBLE
            binding.ticketModal.visibility = View.GONE
            binding.exitoso1.setOnClickListener {
                binding.conditionals.visibility = View.GONE
                binding.observations.setText(binding.exitoso1tv.text)
            }
        }

        binding.rp.setOnClickListener {
            binding.ticketCierre3.visibility = View.VISIBLE
            binding.ticketModal.visibility = View.GONE

        }
//
        binding.exitoso1.setOnClickListener {
            binding.conditionals.visibility = View.GONE
            binding.observations.setText(binding.exitoso1tv.text)
            binding.senDticket.visibility = View.VISIBLE
        }

        binding.fallido2.setOnClickListener {
            binding.conditionals.visibility = View.GONE
            binding.observations.setText(binding.fallido2tv.text)
            binding.senDticket.visibility = View.VISIBLE
        }

        binding.fallido3.setOnClickListener {
            binding.conditionals.visibility = View.GONE
            binding.observations.setText(binding.fallido3tv.text)
            binding.senDticket.visibility = View.VISIBLE
        }

        binding.fallido5.setOnClickListener {
            binding.conditionals.visibility = View.GONE
            binding.observations.setText(binding.fallido5tv.text)
            binding.senDticket.visibility = View.VISIBLE
        }

        binding.operativo.setOnClickListener {
            binding.conditionals.visibility = View.GONE
            binding.observations.setText(binding.exitoso1tv.text)
            binding.senDticket.visibility = View.VISIBLE
        }


        binding.vt.setOnClickListener {
            binding.ticketModal.visibility = View.GONE
            binding.ticketCierre3.visibility = View.VISIBLE

        }
        binding.prv.setOnClickListener {
            binding.ticketCierre.visibility = View.VISIBLE
            binding.ticketCierre3.visibility = View.VISIBLE

        }
        binding.exitoso1.setOnClickListener {
            binding.conditionals.visibility = View.GONE
            binding.observations.setText(binding.exitoso1tv.text)
        }
        binding.prv.setOnClickListener {
//            binding.ticketCierre.visibility = View.VISIBLE
            binding.ticketModal.visibility = View.GONE
            binding.ticketCierre3.visibility = View.VISIBLE

        }
        binding.senDticket.setOnClickListener {
            binding.conditionals.visibility = View.GONE
            if(binding.fallido.isChecked == true) {
                GlobalScope.launch(Dispatchers.IO) {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")
                    val current = LocalDateTime.now().format(formatter)
                    val response = try {
                        val ticketResult = FinishedTicket(
                            binding.tcid.text.toString().toInt(),
                            resultTicketStatusFinal,
                            "${binding.observations.text}",
                            "N/A",
                            current,
                            resultTicketStatusId.toString()
                        )
                        RetrofitInstance.api.postFinishedTicket(ticketResult)
                    } catch (error: IOException) {
                        Toast.makeText(this@MainActivity, "app error $error", Toast.LENGTH_LONG).show()
                        return@launch
                    } catch (e: HttpException) {
                        Toast.makeText(this@MainActivity, "http error $e", Toast.LENGTH_LONG).show()
                        return@launch
                    }
                    if(response.isSuccessful && response.body() != null) {
                        withContext(Dispatchers.Main){
                            Toast.makeText( this@MainActivity, "Insertado Con Exito", Toast.LENGTH_LONG).show()
                            printReciboFinal(resultTicketNumber, resultTicketStatusFinal,
                                binding.observations.text.toString()
                            )

                            binding.resultadoGestion.visibility = View.GONE
//                        binding.closeTicketCierre.visibility = View.GONE
//                        binding.ticketCierre.visibility = View.GONE
                            GlobalScope.launch(Dispatchers.IO) {
                                val response = try {
                                    val cases = CaseById(
                                        getIntent.getIntExtra("id", 0).toString(),
                                    )
//                RetrofitInstance.api.getAllTickets()
                                    RetrofitInstance.api.getAllTicketsByUser(cases)
                                } catch (error: IOException) {
                                    Toast.makeText(this@MainActivity, "app error $error", Toast.LENGTH_LONG).show()
                                    return@launch
                                } catch (e: HttpException) {
                                    Toast.makeText(this@MainActivity, "http error $e", Toast.LENGTH_LONG).show()
                                    return@launch
                                }
                                if(response.isSuccessful && response.body() != null) {
                                    withContext(Dispatchers.Main){
//                    if(response.body()!!.message.contains("No Existen")) {
//                        binding.noRecharges.visibility = View.VISIBLE
//                        Toast.makeText(context, "No existen Recargas", Toast.LENGTH_SHORT).show()
//                    }
                                        val recyclerView = binding.classes
                                        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                                        recyclerView.adapter = CaseAdapter(response.body()!!.data!!) {
                                            onItemSelected(it)
                                        }
                                    }
                                    binding.ticketCierre.visibility = View.GONE
                                    binding.ticketCierreFinal.visibility = View.VISIBLE
                                    val scrollViewM: ScrollView = binding.main
                                    val top = binding.ticketCierreFinal.getTop()
                                    scrollViewM.scrollTo(0, top - 390);
                                }
                            }
                        }
                    }
                }
            }
                GlobalScope.launch(Dispatchers.IO) {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")
                    val current = LocalDateTime.now().format(formatter)
                    val response = try {
                        val ticketResult = FinishedTicket(
                            binding.tcid.text.toString().toInt(),
                            resultTicketStatusFinal,
                            
                            "${binding.observations.text}",
                            "N/A",
                            current,
                            resultTicketNumber.toString()
                        )
                        RetrofitInstance.api.postFinishedTicket(ticketResult)
                    } catch (error: IOException) {
                        Toast.makeText(this@MainActivity, "app error $error", Toast.LENGTH_LONG).show()
                        return@launch
                    } catch (e: HttpException) {
                        Toast.makeText(this@MainActivity, "http error $e", Toast.LENGTH_LONG).show()
                        return@launch
                    }
                    if(response.isSuccessful && response.body() != null) {
                        withContext(Dispatchers.Main){
                            Toast.makeText( this@MainActivity, "Insertado Con Exito", Toast.LENGTH_LONG).show()
                            printReciboFinal(resultTicketNumber, resultTicketStatusFinal,
                                binding.observations.text.toString()
                            )

                            binding.resultadoGestion.visibility = View.GONE
//                        binding.closeTicketCierre.visibility = View.GONE
//                        binding.ticketCierre.visibility = View.GONE
                            GlobalScope.launch(Dispatchers.IO) {
                                val response = try {
                                    val cases = CaseById(
                                        getIntent.getIntExtra("id", 0).toString(),
                                    )
//                RetrofitInstance.api.getAllTickets()
                                    RetrofitInstance.api.getAllTicketsByUser(cases)
                                } catch (error: IOException) {
                                    Toast.makeText(this@MainActivity, "app error $error", Toast.LENGTH_LONG).show()
                                    return@launch
                                } catch (e: HttpException) {
                                    Toast.makeText(this@MainActivity, "http error $e", Toast.LENGTH_LONG).show()
                                    return@launch
                                }
                                if(response.isSuccessful && response.body() != null) {
                                    withContext(Dispatchers.Main){
//                    if(response.body()!!.message.contains("No Existen")) {
//                        binding.noRecharges.visibility = View.VISIBLE
//                        Toast.makeText(context, "No existen Recargas", Toast.LENGTH_SHORT).show()
//                    }
                                        val recyclerView = binding.classes
                                        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                                        recyclerView.adapter = CaseAdapter(response.body()!!.data!!) {
                                            onItemSelected(it)
                                        }
                                    }
                                    val scrollViewM: ScrollView = binding.main
                                    val top = binding.ticketCierreFinal.getTop()
                                    scrollViewM.scrollTo(0, top - 410);
                                    binding.ticketCierre.visibility = View.GONE
                                    binding.ticketCierreFinal.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
        }
        binding.exitoso.setOnCheckedChangeListener { _, isChecked ->
            binding.observations.setText(binding.exitoso1tv.text)
            if (isChecked) {
                resultTicketStatusFinal = "exitoso"
                binding.fallido.isChecked = false
                binding.operativo.isChecked = false
                binding.danado.isChecked = false
                binding.rollout.isChecked = false
            } else {
                binding.senDticket.isClickable = true
            }
        }
        binding.fallido.setOnCheckedChangeListener { _, isChecked ->
            binding.exitoso1.visibility = View.GONE
            binding.fallido2.visibility = View.VISIBLE
            binding.fallido3.visibility = View.VISIBLE
            binding.fallido5.visibility = View.VISIBLE
            binding.conditionals.visibility = View.VISIBLE
            if (isChecked) {
                resultTicketStatusFinal = "fallido"
                binding.exitoso.isChecked = false
                binding.operativo.isChecked = false
                binding.danado.isChecked = false
                binding.rollout.isChecked = false
            }
        }
        binding.operativo.setOnCheckedChangeListener { _, isChecked ->
            binding.conditionals.visibility = View.VISIBLE
            binding.exitoso1.visibility = View.VISIBLE
            binding.fallido2.visibility = View.GONE
            binding.fallido3.visibility = View.GONE
            binding.fallido5.visibility = View.GONE
            if (isChecked) {
                resultTicketStatusFinal = "operativo"
                binding.exitoso.isChecked = false
                binding.fallido.isChecked = false
                binding.danado.isChecked = false
                binding.rollout.isChecked = false
            }
        }
        binding.danado.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.conditionals.visibility = View.VISIBLE
            binding.fallido2.visibility = View.VISIBLE
            binding.fallido3.visibility = View.VISIBLE
            binding.fallido5.visibility = View.VISIBLE
            binding.exitoso1.visibility = View.GONE
            if (isChecked) {
                resultTicketStatusFinal = "dañado"
                binding.exitoso.isChecked = false
                binding.fallido.isChecked = false
                binding.operativo.isChecked = false
                binding.rollout.isChecked = false
            }
        }
        binding.rollout.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.conditionals.visibility = View.VISIBLE
            binding.fallido2.visibility = View.VISIBLE
            binding.fallido3.visibility = View.VISIBLE
            binding.fallido5.visibility = View.VISIBLE
            binding.exitoso1.visibility = View.GONE
            if (isChecked) {
                resultTicketStatusFinal = "rollout"
                binding.exitoso.isChecked = false
                binding.fallido.isChecked = false
                binding.operativo.isChecked = false
                binding.danado.isChecked = false
            }
        }


        binding.closeTicketCierreFinal.setOnClickListener {
            binding.ticketCierreFinal.visibility = View.GONE
        }


//        binding.closeTicketCierre.setOnClickListener {
//            binding.ticketCierre.visibility = View.GONE
//        }
        binding.closeDetailsModal.setOnClickListener {
            binding.ticketModal.visibility = View.GONE
        }

        binding.endTicket.setOnClickListener {
            try {
                if(FileToSend.length() > 0) {
                    Log.i("photodamn", FileToSend.toString())
                    GlobalScope.launch(Dispatchers.IO) {
                        BitmapFactory.decodeFile(binding.mylogo.drawable.toString())
                        val file = binding.mylogo.drawable as File

                        Log.i("photodamn", FileToSend.toString())
                        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
                        val fileToSend = MultipartBody.Part.createFormData("file",file.name, requestBody)
                        val filename = RequestBody.create(MediaType.parse("text/plain"), file.name)
                        val id = resultTicketStatusId
                        Log.i("photodamn", id.toString())
//                        val map = HashMap<String, RequestBody>()
//                        map.put("file\"; filename=\"" + file.name + "\"", requestBody)
                        val response = try {
                            RetrofitInstance.api.uploadFile(fileToSend, filename, id.toString(), FileToSendBASE64.toString())
                        } catch (error: IOException) {
                            this@MainActivity.runOnUiThread(Runnable {
                                Toast.makeText(this@MainActivity, "app error $error", Toast.LENGTH_LONG).show()
                            })
                            return@launch
                        } catch (e: HttpException) {
                            this@MainActivity.runOnUiThread(Runnable {
                                Toast.makeText(this@MainActivity, "http error $e", Toast.LENGTH_LONG).show()
                            })
                            return@launch
                        }
                        if(response!!.isSuccessful && response.body() != null) {
                            this@MainActivity.runOnUiThread(Runnable {
                                Toast.makeText(this@MainActivity, "Exitoso", Toast.LENGTH_LONG).show()
                                Toast.makeText(this@MainActivity, response.body().toString(), Toast.LENGTH_LONG).show()

                                binding.ticketCierreFinal.visibility = View.GONE
                            })
                        }
                        this@MainActivity.runOnUiThread(Runnable {

                            binding.ticketCierreFinal.visibility = View.GONE
                        })
                    }


                } else {


//                    binding.ticketCierre3.visibility = View.GONE
//                    binding.ticketCierre.visibility = View.GONE
//                    binding.ticketCierreFinal.visibility = View.GONE
//                    printReciboFinal2(
//                        binding.resultTicketNumber.text.toString().replace("ticket_#", "Numero de ticket "),
//                        "Numero de afiliado ${binding.numeroAfiliado.text.toString()}",
//                        "Ejecutivo ${binding.klxcklxc.text.toString()}",
//                        "Fecha de cierre ${binding.resultTicketDate.text.toString()}",
//                        binding.cbvcvc.text.toString(),
//                        binding.bvbvb.text.toString(), binding.zxczxc.text.toString(), binding.sdsdss.text.toString()
//                    )


                }
            } catch (e: UninitializedPropertyAccessException) {
                Toast.makeText(this, "No se puede finalizar el ticket si no se adjunta la evidencia", Toast.LENGTH_LONG).show()
            }
        }
        binding.procced.setOnClickListener {
            binding.ticketCierre.visibility = View.VISIBLE
            binding.ticketModal.visibility = View.GONE
            val scrollViewM: ScrollView = binding.main
            val top = binding.ticketCierre.getTop()
            scrollViewM.scrollTo(0, top - 390);
        }

        binding.vt.setOnClickListener {
            binding.ticketCierre3.visibility = View.VISIBLE
            binding.ticketModal.visibility = View.GONE
        }
//
        binding.spx.setOnClickListener {
            binding.ticketCierre3.visibility = View.VISIBLE
            binding.ticketModal.visibility = View.GONE
        }

        binding.ip.setOnClickListener {
            binding.ticketCierre3.visibility = View.VISIBLE
            binding.ticketModal.visibility = View.GONE
        }
//
        binding.retiro.setOnClickListener {
            binding.ticketCierre3.visibility = View.VISIBLE
            binding.ticketModal.visibility = View.GONE
        }

        binding.rp.setOnClickListener {
            binding.ticketCierre3.visibility = View.VISIBLE
            binding.ticketModal.visibility = View.GONE
        }
//
        binding.vt.setOnClickListener {
            binding.ticketModal.visibility = View.GONE
            binding.ticketCierre3.visibility = View.VISIBLE
        }

        binding.prv.setOnClickListener {
            binding.ticketCierre.visibility = View.VISIBLE
            binding.ticketCierre3.visibility = View.VISIBLE
        }

        binding.prv.setOnClickListener {
//            binding.ticketCierre.visibility = View.VISIBLE
            binding.ticketModal.visibility = View.GONE
            binding.ticketCierre3.visibility = View.VISIBLE
        }
//

//close_ticket_cierre2



        binding.vt.setOnClickListener {
            binding.ticketCierre3.visibility = View.VISIBLE
            binding.ticketModal.visibility = View.GONE
        }


//        binding.printButton.setOnClickListener {
//
//            printEncabezado()
//            printRecibo(
//                binding.ticketTitle.text.toString(),
//                binding.ticketStart.text.toString(),
//                binding.ticketDocumentOrigen.text.toString(),
//                binding.ticketInicial.text.toString(),
//                binding.ticketFinal.text.toString(),
//                binding.ticketAssigned.text.toString(),
//                binding.ticketProyect.text.toString(),
//                binding.ticketAttention.text.toString(),
//                binding.ticketEtapa.text.toString(),
//                binding.ticketFailures.text.toString(),
//                binding.ticketType.text.toString(),
//                binding.ticketAddress.text.toString(),
//                binding.ticketRif.text.toString(),
//                binding.ticketRif2.text.toString(),
//                binding.equipoAInstalar.text.toString(),
//                binding.ticketTelefono2.text.toString(),
//                binding.ticketStatus.text.toString())
////            printRecibo("Novaservice")
////            printRecibo("asdasdasdasdasdasd")
//        }
//0412 5778583

//        CoroutineScope(Dispatchers.IO).launch {
////            val gson = Gson()
//            val getAllTicketsResponse = RetrofitInstance.api.getAllTickets()
//
////            val recyclerView = binding.classes
////            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
////            recyclerView.adapter = CaseAdapter(getAllTicketsResponse) {
////                onItemSelected(it)
////            }
////                Log.i("tickets", getAllTicketsResponse.body()!!.toString())
////                Log.i("tickets", "onCreate: ${GsonBuilder().setPrettyPrinting().create().toJson(getAllTicketsResponse.body())}")
////            }
//        }
//        GlobalScope.launch(Dispatchers.IO) {
//            val LoginUserResponse = try {
//                        val newUser = Play(
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                            "",
//                        )
////                        Log.i("network responses", newUser.toString())
//                RetrofitInstance.api.getAllTickets(newUser)
//            } catch (error: IOException) {
//
//                Log.i("Network responses", error.toString())
//                this@MainActivity.runOnUiThread(Runnable {
//                    Toast.makeText(
//                        this@MainActivity,
//                        error.toString(),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                })
////                           Toast.makeText(this@LoginActivity, "weird serror", Toast.LENGTH_LONG).show()
////                 binding.loginWrapper.visibility = View.VISIBLE
//                return@launch
//            } catch (e: HttpException) {
//                Log.i("Network responses", e.toString())
////                   binding.loginWrapper.visibility = View.VISIBLE
//                this@MainActivity.runOnUiThread(Runnable {
//                    Toast.makeText(
//                        this@MainActivity,
//                        e.toString(),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                })
//                return@launch
//            }
//            if (LoginUserResponse.isSuccessful && LoginUserResponse.body() != null) {
//                withContext(Dispatchers.Main) {
//                    val recyclerView = binding.classes
//                    recyclerView.layoutManager = LinearLayoutManager(applicationContext)
//                    recyclerView.adapter = CaseAdapter(LoginUserResponse.body()?.list_case!!) {
//                        onItemSelected(it)
//                    }
//                    Log.i("network responses login", LoginUserResponse.body().toString())
//
//                }
//            } else {
//                withContext(Dispatchers.Main) {
//                    val recyclerView = binding.classes
//                    recyclerView.layoutManager = LinearLayoutManager(applicationContext)
//                    recyclerView.adapter = CaseAdapter(LoginUserResponse.body()?.list_case!!) {
//                        onItemSelected(it)
//                    }
//                    Log.i("network responses login", LoginUserResponse.body().toString())
//
//                }
////                       binding.loading.visibility = View.GONE
////                        binding.notLoading.visibility = View.VISIBLE
////                        val intent = Intent(this@MainActivity, MainActivity::class.java)
////                        startActivity(intent)
//                this@MainActivity.runOnUiThread(Runnable {
//                    Toast.makeText(
//                        this@MainActivity,
//                        "error",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                })
////                        Toast.makeText(this@MainActivity, "Ocurrio un error", Toast.LENGTH_SHORT).show()
//            }
//        }

//        binding.newCase.setOnClickListener {
//            val newCaseIntent = Intent(this@MainActivity, RegisterCaseActivity::class.java)
//            startActivity(newCaseIntent)
//        }

//            binding.export.setOnClickListener {
//                Toast.makeText(this, "voy a exportar", Toast.LENGTH_LONG).show()
//
//
//
//
//
//            }
    }
    private fun getDateTimeFromEpocLongOfSeconds(epoc: Long): String {
        try {
            val netDate = Date(epoc*1000)
            return netDate.toString()
        } catch (e: Exception) {
            return e.toString()
        }
    }
    private fun getDateTimeFromEpocLongOfSeconds2(epoc: Long): String {
        try {
            val netDate = Date(epoc*1000)
            return netDate.toString()
        } catch (e: Exception) {
            return e.toString()
        }
    }
    private fun onItemSelected(it: Operations)  {
        resultTicketStatusId = it.id.toString()
        binding.ticketModal.visibility = View.VISIBLE
        binding.tcid.text = it.id
        binding.tcid.visibility = View.GONE
        binding.ticketTitlef.text = it.titulo!!.toString().replace(Regex("/\\s+/g"), "_")
        binding.ticketProyect.text = it.proyecto!!
        binding.ticketAssigned.text = it.asignada_a
        binding.ticketDocumentOrigen.text = it.documento_origen
        binding.ticketStart.text = it.fecha_de_inicio
        binding.afiliacion.text = it.numero_de_afiliacion
        binding.ticketFinal.text = it.fecha_final
        binding.ticketType.text = it.tipo_de_tarea
        binding.ticketInicial.text = it.fecha_de_inicio
        binding.ticketEtapa.text = it.etapa
        binding.ticketAttention.text = it.zona_de_atencion
        binding.equipo.text = it.equipo
        binding.denominacion.text = it.denominacion_comercial
//        binding.equipoAInstalar.text = it.equipo_a_instalar
        if(it.equipo_a_instalar == "undefined") {
            binding.equipoassign.text = "N/A"
        } else {
            binding.equipoassign.text = it.equipo_a_instalar
        }
        binding.ticketRif2.text = it.RIF
        binding.ticketAddress.text = it.direccion
        binding.ticketTelefono2.text = it.telefono_2
        binding.ticketFailures.text = it.fallas
        binding.ticketStatus.text = it.status

        resultTicketNumber = it.documento_origen.toString()
        resultTicketSubscripted = it.numero_de_afiliacion.toString()
        binding.numeroAfiliado.text = resultTicketSubscripted
        binding.resultTicketNumber.text = resultTicketNumber
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")
        val current = LocalDateTime.now().format(formatter)
        binding.resultTicketDate.text = current.toString()
//                ,
//                binding..text,
//                binding..text,
//                binding..text,
//                binding..text,
//                binding..text,
//                binding..text,
//                binding..text,
//                binding..text,
//                binding..text,
//                binding..text
//                binding..text
//                binding..text
//                binding..text
//                binding..text
//                binding..text)

        binding.printButton.setOnClickListener {
            binding.vt.setOnClickListener {
                binding.ticketCierre3.visibility = View.VISIBLE
                binding.ticketModal.visibility = View.GONE
            }
//
            binding.spx.setOnClickListener {
                binding.ticketCierre3.visibility = View.VISIBLE
                binding.ticketModal.visibility = View.GONE
            }

            binding.ip.setOnClickListener {
                binding.ticketCierre3.visibility = View.VISIBLE
                binding.ticketModal.visibility = View.GONE
            }
//
            binding.retiro.setOnClickListener {
                binding.ticketCierre3.visibility = View.VISIBLE
                binding.ticketModal.visibility = View.GONE
            }

            binding.rp.setOnClickListener {
                binding.ticketCierre3.visibility = View.VISIBLE
                binding.ticketModal.visibility = View.GONE
            }
//
            binding.vt.setOnClickListener {
                binding.ticketModal.visibility = View.GONE
                binding.ticketCierre3.visibility = View.VISIBLE
            }

            binding.prv.setOnClickListener {
                binding.ticketCierre.visibility = View.VISIBLE
                binding.ticketCierre3.visibility = View.VISIBLE
            }
            printEncabezado()
            printRecibo(
                "Titulo: ${binding.ticketTitlef.text.toString()}",
                "Fecha: ${binding.ticketStart.text.toString()}",
                binding.ticketStart.text.toString().replace("ticket_#", "Numero de ticket "),
                "Asignada: ${binding.ticketAssigned.text.toString()}",
                "Proyecto: ${binding.ticketProyect.text.toString()}",
                "Zona de Atencion: ${binding.ticketAttention.text.toString()}",
                "Etapa: ${binding.ticketEtapa.text.toString()}",
                "Zona de Atención: ${binding.ticketAttention.text.toString()}",
                "Fallas: ${binding.ticketFailures.text.toString()}",
                "Tipo de tarea: ${binding.ticketType.text.toString()}",
//                binding.equipoAInstalar.text.toString(),
                "Telefono: ${binding.ticketTelefono2.text.toString()}",
                "Numero de Afililacion: ${binding.afiliacion.text.toString()}",
                "Equipo: ${binding.equipo.text.toString()}",
                "Equipo asignado: ${binding.equipoassign.text.toString()}",
                "Rif: ${binding.ticketRif2.text.toString()}",
                "Direccion: ${binding.ticketAddress.text.toString()}",
            )

        }
    }







    fun POSInitService(contextIn: Context) {
        context = contextIn
        try {
            deviceManager = ConnUtils.getDeviceManager()
            deviceConnParams = NS3ConnParams()
            deviceManager.init(
                context,
                K21_DRIVER_NAME,
                deviceConnParams,
                object : DeviceEventListener<ConnectionCloseEvent> {
                    override fun onEvent(event: ConnectionCloseEvent, handler: Handler) {
                        if (event.isSuccess) {

                        }
                        if (event.isFailed) {

                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun getUIHandler(): Handler? {
                        return null
                    }
                })
            deviceManager.connect()
            securityModule =
                deviceManager.device.getStandardModule(ModuleType.COMMON_SECURITY) as K21SecurityModule
            printerModule = deviceManager.device.getStandardModule(ModuleType.COMMON_PRINTER) as Printer


        } catch (e: Exception) {

        }
    }




    var printListener: PrintListener = object : PrintListener {
        override fun onSuccess() {

        }

        override fun onError(error: ErrorCode, msg: String) {

        }
    }
// ----------------------------------------------------------------------------------------------------
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")

        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // set filename
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        vFilename = "FOTO4444_" + timeStamp + ".png"

        // set direcory folder
        val file = File(Environment.getExternalStorageDirectory().getPath(), vFilename);
        val image_uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }
//    ---------------------------------------------------------------------------------------------


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //called when user presses ALLOW or DENY from Permission Request Popup
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera()
                }
                else{
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Toast.makeText(this@MainActivity, "Foto anexada con exito", Toast.LENGTH_LONG).show()

            //File object of camera image
            val file = File(Environment.getExternalStorageDirectory().path, vFilename);

            FileToSend = file

            val uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);

            fun writeFileOnInternalStorage(mcoContext: Context, sFileName: String?, sBody: String?) {
                val dir = File(mcoContext.filesDir, "mydir")
                if (!dir.exists()) {
                    dir.mkdir()
                }

                try {
                    val gpxfile = File(dir, sFileName)
                    val writer = FileWriter(gpxfile)
                    writer.append(sBody)
                    writer.flush()
                    writer.close()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }


            Log.i("encodexxx", file.toString())
            Log.i("encodexxx", uri.toString())
            try {
                val bm = BitmapFactory.decodeFile(file.toString())
                val stream2 = ByteArrayOutputStream()
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream2)
                val byteFormat = stream2.toByteArray()
                val imgString = Base64.getEncoder().encodeToString(byteFormat)
                Log.i("encodexxx", imgString)

                Glide.with(this).load(file).into(binding.mylogo);
//                =========================================




                val bitmapToEncode: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                Log.i("encodexxxxc", bitmapToEncode.toString())
                val stream: ByteArrayOutputStream = ByteArrayOutputStream();
                bitmapToEncode.compress(Bitmap.CompressFormat.PNG,100, stream)
                Log.i("encodexxx", bitmapToEncode.toString())
                var bytes = byteArrayOf()
                val ccx = uri.toString()
                val encodedString = Base64.getEncoder().encodeToString(ccx.toByteArray())
                FileToSendBASE64 = imgString

                Log.i("encodexxx64", FileToSendBASE64.toString())

//                bytes = stream.toByteArray()
//                Log.i("encodexxx", bitmapToEncode.toString())
            }catch (e: IOException) {
//                e.printStackTrace();
            }







////            longToast(file.toString())
//+
//            //Uri of camera image

//            binding.mylogo.setImageURI(uri)
        }
    }

//    private fun printEncabezado(q:String,w:String,e:String,r:String,t:String,y:String,u:String,i:String,o:String,p:String,a:String,s:String,d:String,f:String,g:String,h:String,j:String) {
fun writeFileOnInternalStorage(mcoContext: Context, sFileName: String?, sBody: String?) {
    val dir = File(mcoContext.filesDir, "mydir")
    if (!dir.exists()) {
        dir.mkdir()
    }

    try {
        val gpxfile = File(dir, sFileName)
        val writer = FileWriter(gpxfile)
        writer.append(sBody)
        writer.flush()
        writer.close()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}
    private fun printEncabezado() {
        try {
            deviceManager = ConnUtils.getDeviceManager()
            deviceConnParams = NS3ConnParams()
            deviceManager.init(
                this@MainActivity,
                K21_DRIVER_NAME,
                deviceConnParams,
                object : DeviceEventListener<ConnectionCloseEvent> {
                    override fun onEvent(event: ConnectionCloseEvent, handler: Handler) {
                        if (event.isSuccess) {

                        }
                        if (event.isFailed) {

                        }
                    }

                    override fun getUIHandler(): Handler? {
                        return null
                    }
                })
            deviceManager.connect()
            securityModule =
                deviceManager.device.getStandardModule(ModuleType.COMMON_SECURITY) as K21SecurityModule
            printerModule = deviceManager.device.getStandardModule(ModuleType.COMMON_PRINTER) as Printer


        } catch (e: Exception) {

        }
        printerModule = deviceManager.device.getStandardModule(ModuleType.COMMON_PRINTER) as Printer
//        open camera


        var printScriptUtil = printerModule.getPrintScriptUtil(this@MainActivity)

        var format = TextFormat()
        format.alignment = Alignment.LEFT
        format.fontScale = FontScale.ORINARY
        format.enFontSize = EnFontSize.FONT_8x24
        format.isLinefeed = true
        format.fontScale = FontScale.ORINARY

        printScriptUtil.setGray(10)
        //pls add the font file in assets folder.

    var imageFormat = ImageFormat()
    imageFormat.alignment = Alignment.CENTER
    imageFormat.width = 180
    imageFormat.height = 90
    imageFormat.offset = 0
    var ds = getResources().getDrawable(R.drawable.nativa);
    val drawas = ds as BitmapDrawable
    val bitmap = drawas.bitmap
        var linea = "Detalle Del Caso\n"
        //printScriptUtil.setLineSpacing(1)

        //format.enFontSize = EnFontSize.FONT_10x8
        printScriptUtil.addImage(imageFormat, bitmap)
        printScriptUtil.addText(format, linea)
        printScriptUtil.addPaperFeed(2)
        printScriptUtil.print(printListener)
    }

    fun printRecibo(q:String,w:String,e:String,r:String,t:String,y:String,u:String,i:String,o:String,p:String,g:String?,zx:String?,sd:String?,xc:String?,cv:String?, x:String?) {
        try {
            deviceManager = ConnUtils.getDeviceManager()
            deviceConnParams = NS3ConnParams()
            deviceManager.init(
                this@MainActivity,
                K21_DRIVER_NAME,
                deviceConnParams,
                object : DeviceEventListener<ConnectionCloseEvent> {
                    override fun onEvent(event: ConnectionCloseEvent, handler: Handler) {
                        if (event.isSuccess) {

                        }
                        if (event.isFailed) {

                        }
                    }

                    override fun getUIHandler(): Handler? {
                        return null
                    }
                })
            deviceManager.connect()
            securityModule =
                deviceManager.device.getStandardModule(ModuleType.COMMON_SECURITY) as K21SecurityModule
            printerModule = deviceManager.device.getStandardModule(ModuleType.COMMON_PRINTER) as Printer


        } catch (e: Exception) {

        }
        printerModule = deviceManager.device.getStandardModule(ModuleType.COMMON_PRINTER) as Printer
        var printScriptUtil = printerModule.getPrintScriptUtil(this@MainActivity)

        if (printerModule.getStatus() == PrinterStatus.NORMAL) {

            var format = TextFormat()
            format.alignment = Alignment.LEFT
            format.fontScale = FontScale.ORINARY
            format.enFontSize = EnFontSize.FONT_8x24
            format.isLinefeed = false
            format.fontScale = FontScale.ORINARY

            var printScriptUtil = printerModule.getPrintScriptUtil(this@MainActivity)


            printScriptUtil.setGray(8)
            //pls add the font file in assets folder.
            val name = "InconsolataRegular.ttf"
            printScriptUtil.addFont(this@MainActivity, name)
            var lineaPrn = ""

            try {






                    format.enFontSize = EnFontSize.FONT_10x16



//                Xq no me salen algnuos campos

//                linea = String.format(
//                    "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n", "%.42s\n" + "%.42s\n\n",
//
//                )


                    /* if (listRecibo[1].contains("CIERRE"))
                        format.enFontSize = EnFontSize.FONT_8x16
                    else
                        format.enFontSize = EnFontSize.FONT_8x16*/
//                "BANCO BNC - RIF J-30984132-7" + "Novaservices\n"




                    var linea = "`${q}\n\' ${w}\n\' ${e}\n\' ${t}\n\' ${y}\n\' ${u}\n\' ${i}\n\' ${r}\n\' ${o}\n\' ${p}\n\' ${g}\n\' ${zx}\n\' ${sd}\n\' ${xc}\n\' ${cv}\n\' ${x}\n\'`"
                    printScriptUtil.addText(format, linea)

                    printScriptUtil.addPaperFeed(4)
                    printScriptUtil.print(printListener)


            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    fun printReciboFinal2(
        q: String,
        w: String,
        e: String,
        r: String,
        t: String,
        y: String,
        u: String,
        i: String,
    ) {
        try {
            deviceManager = ConnUtils.getDeviceManager()
            deviceConnParams = NS3ConnParams()
            deviceManager.init(
                this@MainActivity,
                K21_DRIVER_NAME,
                deviceConnParams,
                object : DeviceEventListener<ConnectionCloseEvent> {
                    override fun onEvent(event: ConnectionCloseEvent, handler: Handler) {
                        if (event.isSuccess) {

                        }
                        if (event.isFailed) {

                        }
                    }

                    override fun getUIHandler(): Handler? {
                        return null
                    }
                })
            deviceManager.connect()
            securityModule =
                deviceManager.device.getStandardModule(ModuleType.COMMON_SECURITY) as K21SecurityModule
            printerModule = deviceManager.device.getStandardModule(ModuleType.COMMON_PRINTER) as Printer


        } catch (e: Exception) {

        }
        printerModule = deviceManager.device.getStandardModule(ModuleType.COMMON_PRINTER) as Printer
        var printScriptUtil = printerModule.getPrintScriptUtil(this@MainActivity)

        if (printerModule.getStatus() == PrinterStatus.NORMAL) {

            var format = TextFormat()
            format.alignment = Alignment.LEFT
            format.fontScale = FontScale.ORINARY
            format.enFontSize = EnFontSize.FONT_8x24
            format.isLinefeed = false
            format.fontScale = FontScale.ORINARY

            var printScriptUtil = printerModule.getPrintScriptUtil(this@MainActivity)


            printScriptUtil.setGray(8)
            //pls add the font file in assets folder.
            val name = "InconsolataRegular.ttf"
            printScriptUtil.addFont(this@MainActivity, name)
            var lineaPrn = ""

            try {






                format.enFontSize = EnFontSize.FONT_10x16
                var imageFormat = ImageFormat()

                imageFormat.alignment = Alignment.CENTER
                imageFormat.width = 180
                imageFormat.height = 90
                imageFormat.offset = 0

//                Xq no me salen algnuos campos

//                linea = String.format(
//                    "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n", "%.42s\n" + "%.42s\n\n",
//
//                )


                /* if (listRecibo[1].contains("CIERRE"))
                    format.enFontSize = EnFontSize.FONT_8x16
                else
                    format.enFontSize = EnFontSize.FONT_8x16*/
//                "BANCO BNC - RIF J-30984132-7" + "Novaservices\n"

                var linea = "`Informacion De Gestion\n\' Canales De Atencion\n\' ${q}\n\' ${w}\n\' ${e}\n\' ${r}\n\' ${t}\n\' ${y}\n\' ${u}\n\' sdsdsd'`"
                var ds = getResources().getDrawable(R.drawable.nativa);
                val drawas = ds as BitmapDrawable
                val bitmap = drawas.bitmap
                printScriptUtil.addImage(imageFormat, bitmap)
                printScriptUtil.addText(format, linea)

                printScriptUtil.addPaperFeed(4)
                printScriptUtil.print(printListener)


            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }
    fun printReciboFinal(q:String,w:String,e:String) {
        try {
            deviceManager = ConnUtils.getDeviceManager()
            deviceConnParams = NS3ConnParams()
            deviceManager.init(
                this@MainActivity,
                K21_DRIVER_NAME,
                deviceConnParams,
                object : DeviceEventListener<ConnectionCloseEvent> {
                    override fun onEvent(event: ConnectionCloseEvent, handler: Handler) {
                        if (event.isSuccess) {

                        }
                        if (event.isFailed) {

                        }
                    }

                    override fun getUIHandler(): Handler? {
                        return null
                    }
                })
            deviceManager.connect()
            securityModule =
                deviceManager.device.getStandardModule(ModuleType.COMMON_SECURITY) as K21SecurityModule
            printerModule = deviceManager.device.getStandardModule(ModuleType.COMMON_PRINTER) as Printer


        } catch (e: Exception) {

        }
        printerModule = deviceManager.device.getStandardModule(ModuleType.COMMON_PRINTER) as Printer
        var printScriptUtil = printerModule.getPrintScriptUtil(this@MainActivity)

        if (printerModule.getStatus() == PrinterStatus.NORMAL) {

            var format = TextFormat()
            format.alignment = Alignment.LEFT
            format.fontScale = FontScale.ORINARY
            format.enFontSize = EnFontSize.FONT_8x24
            format.isLinefeed = false
            format.fontScale = FontScale.ORINARY

            var printScriptUtil = printerModule.getPrintScriptUtil(this@MainActivity)


            printScriptUtil.setGray(8)
            //pls add the font file in assets folder.
            val name = "InconsolataRegular.ttf"
            printScriptUtil.addFont(this@MainActivity, name)
            var lineaPrn = ""

            try {






                format.enFontSize = EnFontSize.FONT_10x16



//                Xq no me salen algnuos campos

//                linea = String.format(
//                    "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n", "%.42s\n" + "%.42s\n\n",
//
//                )


                /* if (listRecibo[1].contains("CIERRE"))
                    format.enFontSize = EnFontSize.FONT_8x16
                else
                    format.enFontSize = EnFontSize.FONT_8x16*/
//                "BANCO BNC - RIF J-30984132-7" + "Novaservices\n"
                var imageFormat = ImageFormat()

                imageFormat.alignment = Alignment.CENTER
                imageFormat.width = 180
                imageFormat.height = 90
                imageFormat.offset = 0
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")
                val current = LocalDateTime.now().format(formatter)
                var linea = "`Cierre De Gestion\n\' \n\' ${q.replace("ticket_#", "Numero de ticket: ")}\n\' Status: ${w}\n\' Observaciones: ${e}\n\' Fecha de cierre: ${current}\n\' Denominacion Comercial: ${binding.denominacion.text.toString()}\n\' Rif: ${binding.ticketRif2.text.toString()}\n\' Afiliado: ${binding.equipo.text.toString()}\n\" Numero de Serial: ${binding.numeroAfiliado.text.toString()} \n\' \n\' \n\' \n\' \n\'  FIRMA Y SELLO DE COMERCIO \n\' \n\' \n\' \n\' \n\' -----------------------------------------------------------\n\' \n\' \n\' \n\'`"
                var ds = getResources().getDrawable(R.drawable.nativa);
                val drawas = ds as BitmapDrawable
                val bitmap = drawas.bitmap
                printScriptUtil.addImage(imageFormat, bitmap)
                printScriptUtil.addText(format, linea)

                printScriptUtil.addPaperFeed(4)
                printScriptUtil.print(printListener)


            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun printReporteGeneral(valor: String) {
        deviceManager = ConnUtils.getDeviceManager()
        deviceConnParams = NS3ConnParams()
        deviceManager.init(
            this@MainActivity,
            K21_DRIVER_NAME,
            deviceConnParams,
            object : DeviceEventListener<ConnectionCloseEvent> {
                override fun onEvent(event: ConnectionCloseEvent, handler: Handler) {
                    if (event.isSuccess) {

                    }
                    if (event.isFailed) {

                    }
                }

                override fun getUIHandler(): Handler? {
                    return null
                }
            })
        deviceManager.connect()
        securityModule =
            deviceManager.device.getStandardModule(ModuleType.COMMON_SECURITY) as K21SecurityModule
        printerModule = deviceManager.device.getStandardModule(ModuleType.COMMON_PRINTER) as Printer

        try {
            if (printerModule.getStatus() == PrinterStatus.NORMAL) {
//                printEncabezado("REPORTE GENERAL")
                var format = TextFormat()
                format.alignment = Alignment.LEFT
                format.fontScale = FontScale.ORINARY
                format.enFontSize = EnFontSize.FONT_8x24
                format.isLinefeed = false
                format.fontScale = FontScale.ORINARY

                var printScriptUtil = printerModule.getPrintScriptUtil(this@MainActivity)


                printScriptUtil.setGray(8)
                //pls add the font file in assets folder.
                val name = "InconsolataRegular.ttf"
                printScriptUtil.addFont(this@MainActivity, name)
                var lineaPrn = ""
//                printScriptUtil.addText(format, valor)
                printScriptUtil.addText(format,String.format("%.48s\n" + "%.48s\n\n", "mira michain"))
                printScriptUtil.addPaperFeed(4)
                printScriptUtil.print(printListener)


            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }





    }


