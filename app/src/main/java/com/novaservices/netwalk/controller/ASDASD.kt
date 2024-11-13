//package com.digipay.digimpos.POS.NEWLAND
//
//import android.content.Context
//import android.icu.text.SimpleDateFormat
//import android.os.Handler
//import android.os.RemoteException
//
//import com.digipay.digimpos.Persistencia.DB_Config
//import com.digipay.digimpos.Persistencia.DataBase
//
//import com.digipay.digimpos.utils.Utiles
//
//import com.newland.me.ConnUtils
//import com.newland.me.DeviceManager
//import com.newland.mtype.ConnectionCloseEvent
//
//import com.newland.mtype.ModuleType
//
//import com.newland.mtype.conn.DeviceConnParams
//import com.newland.mtype.event.DeviceEventListener
//
//import com.newland.mtype.module.common.printer.*
//import com.newland.mtype.module.common.security.K21SecurityModule
//
//import com.newland.mtypex.nseries3.NS3ConnParams
//
//import java.util.*
//
//
//class NewlandController2 {
//
//
//    private val K21_DRIVER_NAME: String = "com.newland.me.K21Driver"
//    private var deviceConnParams: DeviceConnParams? = null
//
//    private lateinit var deviceManager: DeviceManager
//    private lateinit var securityModule: K21SecurityModule
//    private lateinit var printerModule: Printer
//
//
//
//    private lateinit var  context: Context
//
//
//    fun POSInitService(contextIn: Context) {
//        context = contextIn
//        try {
//            deviceManager = ConnUtils.getDeviceManager()
//            deviceConnParams = NS3ConnParams()
//            deviceManager.init(
//                context,
//                K21_DRIVER_NAME,
//                deviceConnParams,
//                object : DeviceEventListener<ConnectionCloseEvent> {
//                    override fun onEvent(event: ConnectionCloseEvent, handler: Handler) {
//                        if (event.isSuccess) {
//
//                        }
//                        if (event.isFailed) {
//
//                        }
//                    }
//
//                    override fun getUIHandler(): Handler? {
//                        return null
//                    }
//                })
//            deviceManager.connect()
//            securityModule =
//                deviceManager.device.getStandardModule(ModuleType.COMMON_SECURITY) as K21SecurityModule
//            printerModule = deviceManager.device.getStandardModule(ModuleType.COMMON_PRINTER) as Printer
//
//
//        } catch (e: Exception) {
//
//        }
//    }
//
//
//
//
//    var printListener: PrintListener = object : PrintListener {
//        override fun onSuccess() {
//
//        }
//
//        override fun onError(error: ErrorCode, msg: String) {
//
//        }
//    }
//
//
//    private fun printEncabezado(titulo: String) {
//
//        var printScriptUtil = printerModule.getPrintScriptUtil(context)
//
//        var format = TextFormat()
//        format.alignment = Alignment.LEFT
//        format.fontScale = FontScale.ORINARY
//        format.enFontSize = EnFontSize.FONT_8x24
//        format.isLinefeed = false
//        format.fontScale = FontScale.ORINARY
//
//        printScriptUtil.setGray(8)
//        //pls add the font file in assets folder.
//        val name = "InconsolataRegular.ttf"
//        printScriptUtil.addFont(context, name)
//
//
//        val db = DataBase(context)
//        val dbConfig = DB_Config(db)
//        val objConfig = dbConfig.obtenerConfig()
//        val nombreComercio = objConfig!!.cfgNombreComercio
//        val rif = objConfig!!.cfgRIF
//        val loteActual = "LOTE: ${objConfig!!.cfgLoteActual.toInt()}"
//
//        val terminalMer = "AFIL: ${objConfig!!.cfgMerchantID} TER: ${objConfig!!.cfgTerminalID}"
//
//        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
//        var strDate = dateFormat.format(Date())
//
//        var dia = strDate.substring(6, 8)
//        var mes = strDate.substring(4, 6)
//        var anio = strDate.substring(0, 4)
//
//        var hora = strDate.substring(8, 10)
//        var min = strDate.substring(10, 12)
//        var seg = strDate.substring(12)
//        var fecha = "FECHA: $dia-$mes-$anio  $hora:$min:$seg"
//
//
//        var linea = String.format(
//            "%.48s\n" + "%.48s\n" + "%.48s\n" + "%.48s\n" + "%.48s\n\n",
//            Utiles().center("BANCO BNC - RIF J-30984132-7", 48, ' '),
//            Utiles().center(titulo, 48, ' '),
//            Utiles().center(fecha, 48, ' '),
//            Utiles().center(nombreComercio, 48, ' '),
//            Utiles().center(rif, 48, ' ')
//        );
//
//
//        //printScriptUtil.setLineSpacing(1)
//        printScriptUtil.addText(format, linea)
//        printScriptUtil.print(printListener)
//
//        //format.enFontSize = EnFontSize.FONT_10x8
//        linea = String.format("%.48s\n" + "%.48s\n\n", terminalMer, loteActual)
//        printScriptUtil.addText(format, linea)
//        printScriptUtil.print(printListener)
//    }
//
//     fun printRecibo(valor: String) {
//
//        if (printerModule.getStatus() == PrinterStatus.NORMAL) {
//
//            var format = TextFormat()
//            format.alignment = Alignment.LEFT
//            format.fontScale = FontScale.ORINARY
//            format.enFontSize = EnFontSize.FONT_8x24
//            format.isLinefeed = false
//            format.fontScale = FontScale.ORINARY
//
//            var printScriptUtil = printerModule.getPrintScriptUtil(context)
//
//
//            printScriptUtil.setGray(8)
//            //pls add the font file in assets folder.
//            val name = "InconsolataRegular.ttf"
//            printScriptUtil.addFont(context, name)
//            var lineaPrn = ""
//
//            try {
//
//                var listRecibo = valor.split("\n")
//
//
//
//                if (listRecibo.size <= 13) {
//                    printEncabezado("TRANSACCION FALLIDA")
//
//                    format.enFontSize = EnFontSize.FONT_8x24
//                    for (linea in listRecibo) {
//                        lineaPrn = Utiles().center(linea, 42, ' ')
//                        printScriptUtil.addText(format, lineaPrn)
//                    }
//                    lineaPrn = "*".padEnd(42, '*')
//                    printScriptUtil.addText(format, lineaPrn)
//
//
//                } else {
//
//                    var linea = ""
//                    if (!listRecibo[1].contains("ANULA")) {
//                        linea = String.format(
//                            "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n\n",
//                            Utiles().center("BANCO BNC - RIF J-30984132-7", 42, ' '),
//                            Utiles().center(listRecibo[1].trim(), 42, ' '),
//                            Utiles().center(listRecibo[2].trim(), 42, ' '),
//                            Utiles().center(listRecibo[3].trim(), 42, ' '),
//                            Utiles().center(listRecibo[4].trim(), 42, ' '),
//                            Utiles().center(listRecibo[5].trim(), 42, ' ')
//                        )
//                    } else {
//                        linea = String.format(
//                            "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n" + "%.42s\n\n",
//                            Utiles().center("BANCO BNC - RIF J-30984132-7", 42, ' '),
//                            "*".padEnd(38, '*'),
//                            "*".padEnd(38, '*'),
//                            Utiles().center(listRecibo[1].trim(), 42, ' '),
//                            "*".padEnd(38, '*'),
//                            "*".padEnd(38, '*'),
//                            Utiles().center(listRecibo[2].trim(), 42, ' '),
//                            Utiles().center(listRecibo[3].trim(), 42, ' '),
//                            Utiles().center(listRecibo[4].trim(), 42, ' '),
//                            Utiles().center(listRecibo[5].trim(), 42, ' ')
//                        )
//                    }
//
//                    format.enFontSize = EnFontSize.FONT_10x16
//                    printScriptUtil.addText(format, linea)
//
//
//                    if (!listRecibo[1].contains("CIERRE")) {
//                        linea = String.format(
//                            "%.42s\n" + "%.42s\n" + "%.42s\n\n",
//                            listRecibo[6].trim(),
//                            listRecibo[7].trim(),
//                            listRecibo[8].trim()
//                        )
//                        //format.enFontSize = EnFontSize.FONT_8x16
//                        printScriptUtil.addText(format, linea)
//
//                    } else {
//                        linea = String.format(
//                            "%.42s\n" + "%.42s\n\n",
//                            listRecibo[6].trim(),
//                            listRecibo[7].trim()
//                        )
//                        //format.enFontSize = EnFontSize.FONT_8x16
//                        printScriptUtil.addText(format, linea)
//                        linea = String.format(
//                            "%.42s\n\n",
//                            Utiles().center(listRecibo[8].trim(), 42, ' ')
//                        )
//                        // format.enFontSize = EnFontSize.FONT_10x16
//                        printScriptUtil.addText(format, linea)
//                    }
//
//
//
//                    if (!listRecibo[1].contains("CIERRE")) {
//                        format.enFontSize = EnFontSize.FONT_8x24
//                        linea = String.format(
//                            "%.48s\n" + "%.48s\n\n",
//                            Utiles().center(listRecibo[9].trim(), 48, ' '),
//                            Utiles().center(listRecibo[10].trim(), 48, ' ')
//                        )
//                        //format.enFontSize = EnFontSize.FONT_10x16
//                        printScriptUtil.addText(format, linea)
//                        format.enFontSize = EnFontSize.FONT_10x16
//                    } else {
//                        linea = String.format(
//                            "%.42s\n" + "%.42s\n\n",
//                            Utiles().center(listRecibo[9].trim(), 42, ' '),
//                            Utiles().center(listRecibo[10].trim(), 42, ' ')
//                        )
//                        //format.enFontSize = EnFontSize.FONT_10x16
//                        printScriptUtil.addText(format, linea)
//                    }
//
//                    linea = String.format(
//                        "%.42s\n" + "%.42s\n" + "%.42s\n\n",
//                        listRecibo[11].trim(),
//                        listRecibo[12].trim(),
//                        listRecibo[13].trim()
//                    )
//                    /* if (listRecibo[1].contains("CIERRE"))
//                        format.enFontSize = EnFontSize.FONT_8x16
//                    else
//                        format.enFontSize = EnFontSize.FONT_8x16*/
//
//
//                    printScriptUtil.addText(format, linea)
//
//
//                    if (listRecibo.size >= 15) {
//                        linea =
//                            String.format("%.42s\n", Utiles().center( Utiles().eliminarBytesNoAscii(listRecibo[14]).trim(), 42, ' '));
//                        //ormat.enFontSize = EnFontSize.FONT_10x16
//                        printScriptUtil.addText(format, linea)
//                    }
//
//                    if (listRecibo.size >= 16) {
//                        if (!listRecibo[15].trim().equals("")) {
//                            linea =
//                                String.format(
//                                    "%.42s\n",
//                                    Utiles().center(listRecibo[15].trim(), 42, ' ')
//                                );
//                            //format.enFontSize = EnFontSize.FONT_10x16
//                            printScriptUtil.addText(format, linea)
//                        }
//                    }
//                    if (listRecibo.size >= 17) {
//                        if (!listRecibo[16].trim().equals("")) {
//                            linea =
//                                String.format(
//                                    "%.42s\n",
//                                    Utiles().center(listRecibo[16].trim(), 42, ' ')
//                                );
//                            //format.enFontSize = EnFontSize.FONT_10x16
//                            printScriptUtil.addText(format, linea)
//                        }
//                    }
//
//                }
//                printScriptUtil.addPaperFeed(4)
//                printScriptUtil.print(printListener)
//            } catch (e: RemoteException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//
//
//    fun printReporteGeneral(valor: String) {
//        try {
//            if (printerModule.getStatus() == PrinterStatus.NORMAL) {
//                printEncabezado("REPORTE GENERAL")
//                var format = TextFormat()
//                format.alignment = Alignment.LEFT
//                format.fontScale = FontScale.ORINARY
//                format.enFontSize = EnFontSize.FONT_8x24
//                format.isLinefeed = false
//                format.fontScale = FontScale.ORINARY
//
//                var printScriptUtil = printerModule.getPrintScriptUtil(context)
//
//
//                printScriptUtil.setGray(8)
//                //pls add the font file in assets folder.
//                val name = "InconsolataRegular.ttf"
//                printScriptUtil.addFont(context, name)
//                var lineaPrn = ""
//                printScriptUtil.addText(format, valor)
//
//                printScriptUtil.addPaperFeed(4)
//                printScriptUtil.print(printListener)
//
//
//            }
//        } catch (e: RemoteException) {
//            e.printStackTrace()
//        }
//    }
//
//
//
//}