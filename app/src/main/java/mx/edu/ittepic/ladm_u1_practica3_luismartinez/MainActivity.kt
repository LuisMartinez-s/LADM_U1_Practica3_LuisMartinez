package mx.edu.ittepic.ladm_u1_practica3_luismartinez

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {
    var arreglo: Array<Int> = Array(10) { 0 }
    var datos = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permisos()
        //--------------------ASIGNAR VALORES----------------------------------
        asignar.setOnClickListener {

            asignarValor()
        }//asignar

        //-------------------MOSTRAR------------------------------------
        mostrar.setOnClickListener {
            datos = ""
            (0..9).forEach {
                datos += "${arreglo[it]},"
            }

            mensaje(datos)
            textMostrar.text = datos
        }//mostrar

        //----------------GUARDAR SD----------------------
        guardar.setOnClickListener {
            if (noSD()) {
                mensaje("Inserte una memoria SD para guardar el archivo")
                return@setOnClickListener
            }//if

            if (editGuardar.text.toString().isEmpty()) {
                mensaje("El nombre del archivo no puede estár vacio")
                return@setOnClickListener
            }//if

            try {
                permisos() //comprobar y solicitar permisos SD

                var rutaSD = Environment.getExternalStorageDirectory()
                var datosArchivo = File(
                    rutaSD.absolutePath,
                    editGuardar.text.toString() + ".txt"
                ) //file (ruta,nombre)
                var flujoSalida = OutputStreamWriter(FileOutputStream(datosArchivo))
                datos = textMostrar.text.toString()
                flujoSalida.write(datos)
                flujoSalida.flush() //forzar escritura
                flujoSalida.close()
                mensaje("El archivo fue creado exitosamente")

                editGuardar.setText("")

            } catch (error: IOException) {
                mensaje(error.message.toString())
            }
        }//guardar

        //-----------------------------LEER SD-------------------------------------

        leer.setOnClickListener {
            permisos()
            if (noSD()) {
                mensaje("Inserte una memoria SD para leer el archivo")
                return@setOnClickListener
            }//if

            if (editLeer.text.toString().isEmpty()) {
                mensaje("El nombre del archivo no puede estár vacio")
                return@setOnClickListener
            }//if

            try {
                var rutaSD = Environment.getExternalStorageDirectory()
                var datosArchivo = File(
                    rutaSD.absolutePath,
                    editLeer.text.toString() + ".txt"
                ) //file (ruta,nombre)
                var flujoEntrada =
                    BufferedReader(InputStreamReader(FileInputStream(datosArchivo))) //BufferedReader = leer por linea -- esto es acceso a la memoria interna

                var data = flujoEntrada.readLine()

                textMostrar.setText(data)

                var vector = data.split(",")

                (0..9).forEach {
                    arreglo[it] = vector[it].toInt()
                }
                editLeer.setText("")

            } catch (error: IOException) {
                mensaje(error.message.toString())
            }

        }//leer
    }


    fun permisos() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 0
            )
        } else {
            //mensaje("LOS PERMISOS YA FUERON OTORGADOS")
        }
    }//permisos

    fun asignarValor() {
        if (editValor.text.toString().isEmpty() || editPosicion.text.toString().isEmpty()) {
            mensaje("No puede haber campos vacios")
            return
        }
        if (editPosicion.text.toString().toInt() < 0 || editPosicion.text.toString().toInt() > 9) {
            mensaje("La posición solo puede ser del 0 al 9")
            return
        }
        var posicion = editPosicion.text.toString().toInt()
        var valor = editValor.text.toString().toInt()
        arreglo[posicion] = valor
        editValor.setText("")
        editPosicion.setText("")
    }//asignarValor


    private fun mensaje(m: String) {
        AlertDialog.Builder(this)
            .setTitle("Atención")
            .setMessage(m)
            .setPositiveButton("Aceptar") { d, i -> }
            .show()
    }//mensaje

    fun noSD(): Boolean {
        var estado = Environment.getExternalStorageState()
        if (estado != Environment.MEDIA_MOUNTED) {
            return true
        }
        return false
    }

}
