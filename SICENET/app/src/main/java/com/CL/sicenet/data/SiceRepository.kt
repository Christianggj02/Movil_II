package com.CL.sicenet.data

import android.util.Log
import com.CL.sicenet.model.Student
import com.CL.sicenet.model.User
import com.CL.sicenet.network.siceApiService
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import okhttp3.RequestBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.awaitResponse

interface SiceRepository {
    suspend fun getAccess(matricula: kotlin.String, password: kotlin.String, userKind: kotlin.String): User
    suspend fun getInfo(): Student
}

class NetworkSiceRepository(
    private val siceApiService: siceApiService,
    private val retrofit: Retrofit
): SiceRepository{

    override suspend fun getAccess(matricula: kotlin.String, password: kotlin.String, userKind: kotlin.String): User {
        siceApiService.getCookie().awaitResponse()

        val bodyXMLRequest =
            """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <accesoLogin xmlns="http://tempuri.org/">
                      <strMatricula>%S</strMatricula>
                      <strContrasenia>%s</strContrasenia>
                      <tipoUsuario>ALUMNO</tipoUsuario>
                    </accesoLogin>
                  </soap:Body>
                </soap:Envelope>
            """.trimIndent()

        var result: User = User(false,"",0,"","")
        val request = RequestBody.create(MediaType.get("text/xml"),bodyXMLRequest.format(matricula,password))

        val response = siceApiService.getAccess(request).awaitResponse()
        if (response.isSuccessful) {
            val xml = response.body()?.string().toString()
            Log.d("SOAP", xml)

            try {
                result = Gson().fromJson(extracJson(xml,"accesoLoginResult"), User::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Log.d("SOAP", "Error: ${response.code()}")
        }
        return result
    }

    override suspend fun getInfo(): Student{
        val bodyRequestXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()
        var result: Student = Student(
            fechaReins = "",
            modEducativo = 0,
            adeudo = false,
            urlFoto = "",
            adeudoDescripcion = "",
            inscrito = false,
            estatus = "",
            semActual = 0,
            cdtosAcumulados = 0,
            cdtosActuales = 0,
            especialidad = "",
            carrera = "",
            lineamiento = 0,
            nombre = "",
            matricula = ""
        )

        val response = siceApiService.getInfo(RequestBody.create(MediaType.get("text/xml"),bodyRequestXml)).awaitResponse()
        if (response.isSuccessful) {
            val xml = response.body()?.string().toString()
            Log.d("SOAP", xml)

            try {
                result = Gson().fromJson(extracJson(xml,"getAlumnoAcademicoWithLineamientoResult"), Student::class.java)
                Log.d("Informacion",result.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Log.d("SOAP", "Error: ${response.code()}")
        }
        return result
    }

    fun extracJson(responseBody: String, tag: String):String{
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(responseBody.reader())

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == tag) {
                parser.next()
                return parser.text
            }
            eventType = parser.next()
        }
        return ""
    }
}