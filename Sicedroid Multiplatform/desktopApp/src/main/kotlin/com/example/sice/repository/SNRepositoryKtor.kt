package com.example.sice.repository

import com.example.sice.data.SNRepository
import com.example.sice.models.CalificacionFinalItem
import com.example.sice.models.CalificacionUnidadItem
import com.example.sice.models.CardexItem
import com.example.sice.models.CargaItem
import com.example.sice.models.ProfileStudent
import com.example.sice.models.PromedioInfo
import org.json.JSONArray
import org.json.JSONObject
import java.util.prefs.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.time.Instant
import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


/**
 * Implementación JVM de SNRepository usando Ktor para llamadas SOAP/HTTP.
 * - Reutiliza la estrategia de Android: enviar body XML, extraer bloque JSON con regex y parsear con org.json.
 * - Persiste la matrícula en Preferences (simple) para hasSession() y restauración.
 *
 * Ajusta `baseUrl` y los cuerpos SOAP (bodyX) según tu proyecto.
 */
class SNRepositoryKtor(
    private val baseUrl: String = "https://sicenet.surguanajuato.tecnm.mx/ws/wsalumnos.asmx",
    private val client: HttpClient = defaultClient()
) : SNRepository {

    private val prefs: Preferences = Preferences.userRoot().node("com.example.sice.shared")
    private var _currentMatricula: String? = prefs.get("matricula", null)

    companion object {
        // Cuerpos SOAP (copiados de tu Android)
        private val bodyAcceso = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <accesoLogin xmlns="http://tempuri.org/">
                  <strMatricula>%s</strMatricula>
                  <strContrasenia>%s</strContrasenia>
                  <tipoUsuario>ALUMNO</tipoUsuario>
                </accesoLogin>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        private val bodyPerfil = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/">
                  <strMatricula>%s</strMatricula>
                  <tipoUsuario>ALUMNO</tipoUsuario>
                </getAlumnoAcademicoWithLineamiento>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        private val bodyCardex = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAllKardexConPromedioByAlumno xmlns="http://tempuri.org/">
                  <aluLineamiento>%s</aluLineamiento>
                </getAllKardexConPromedioByAlumno>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        private val bodyCargaAcademica = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getCargaAcademicaByAlumno xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        private val bodyCalificacionesUnidades = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getCalifUnidadesByAlumno xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        private val bodyCalificacionFinal = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAllCalifFinalByAlumnos xmlns="http://tempuri.org/">
                  <bytModEducativo>%s</bytModEducativo>
                </getAllCalifFinalByAlumnos>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        // Cliente por defecto con almacenamiento de cookies personalizado
        fun defaultClient(): HttpClient = HttpClient(CIO) {
            install(HttpCookies) {
                storage = PreferencesCookiesStorage()
            }
        }
    }

    // Persistencia de matrícula
    override fun getCurrentMatricula(): String? = _currentMatricula
    override fun setCurrentMatricula(matricula: String?) {
        _currentMatricula = matricula
        if (matricula == null) prefs.remove("matricula") else prefs.put("matricula", matricula)
    }

    override fun hasSession(): Boolean {
        // Consideramos sesión si hay matrícula guardada y no vacía
        return !_currentMatricula.isNullOrBlank()
    }

    override fun logout() {
        _currentMatricula = null
        prefs.remove("matricula")
        // limpiar cookies en Preferences
        prefs.remove("cookies")
    }

    // Helper para POST SOAP (usa baseUrl ya con path)
    private suspend fun postSoap(soapAction: String, bodyXml: String): String? = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = client.post(baseUrl) {
                contentType(ContentType.Text.Xml)
                header("SOAPAction", soapAction)
                setBody(bodyXml)
            }
            response.bodyAsText()
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }

    override suspend fun acceso(m: String, p: String): String {
        val soap = bodyAcceso.format(m.uppercase(), p)
        val xml = postSoap("http://tempuri.org/accesoLogin", soap) ?: return "ERROR"
        val resultRegex = "<accesoLoginResult>(.*?)</accesoLoginResult>".toRegex()
        val result = resultRegex.find(xml)?.groupValues?.get(1)
        return if (result != null && result.contains("true", ignoreCase = true)) {
            setCurrentMatricula(m.uppercase())
            "OK"
        } else {
            setCurrentMatricula(null)
            "ERROR"
        }
    }

    override suspend fun profile(): ProfileStudent? {
        if (!hasSession()) return null
        val matricula = _currentMatricula ?: return null
        val soap = bodyPerfil.format(matricula)
        val xml = postSoap("http://tempuri.org/getAlumnoAcademicoWithLineamiento", soap) ?: return null
        val resultRegex = "<getAlumnoAcademicoWithLineamientoResult>(.*?)</getAlumnoAcademicoWithLineamientoResult>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val result = resultRegex.find(xml)?.groupValues?.get(1) ?: return null
        return try {
            val jsonObj = JSONObject(result)
            ProfileStudent(
                matricula = jsonObj.optString("matricula", matricula),
                nombre = jsonObj.optString("nombre", "Alumno"),
                carrera = jsonObj.optString("carrera", ""),
                semActual = jsonObj.optInt("semActual", 0),
                cdtosAcumulados = jsonObj.optInt("cdtosAcumulados", 0)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun cardex(lineamiento: Int): Pair<List<CardexItem>, PromedioInfo>? {
        if (!hasSession()) return null
        val soap = bodyCardex.format(lineamiento)
        val xml = postSoap("http://tempuri.org/getAllKardexConPromedioByAlumno", soap) ?: return null
        val resultRegex = "<getAllKardexConPromedioByAlumnoResult>(.*?)</getAllKardexConPromedioByAlumnoResult>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val result = resultRegex.find(xml)?.groupValues?.get(1) ?: return null
        return try {
            val jsonObj = JSONObject(result)
            val kardexArray = jsonObj.optJSONArray("lstKardex") ?: JSONArray()
            val list = mutableListOf<CardexItem>()
            for (i in 0 until kardexArray.length()) {
                val obj = kardexArray.getJSONObject(i)
                list.add(
                    CardexItem(
                        claveMateria = obj.optString("ClvMat"),
                        claveOficial = obj.optString("ClvOfiMat"),
                        materia = obj.optString("Materia"),
                        creditos = obj.optInt("Cdts"),
                        calificacion = obj.optInt("Calif"),
                        acreditacion = obj.optString("Acred"),
                        semestre = obj.optString("S1"),
                        periodo = obj.optString("P1"),
                        anio = obj.optString("A1")
                    )
                )
            }
            val promedioObj = jsonObj.getJSONObject("Promedio")
            val promedioInfo = PromedioInfo(
                promedioGral = promedioObj.optDouble("PromedioGral"),
                creditosAcumulados = promedioObj.optInt("CdtsAcum"),
                creditosPlan = promedioObj.optInt("CdtsPlan"),
                materiasCursadas = promedioObj.optInt("MatCursadas"),
                materiasAprobadas = promedioObj.optInt("MatAprobadas"),
                avanceCreditos = promedioObj.optDouble("AvanceCdts")
            )
            Pair(list, promedioInfo)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun cargaAcademica(): List<CargaItem>? {
        if (!hasSession()) return null
        val xml = postSoap("http://tempuri.org/getCargaAcademicaByAlumno", bodyCargaAcademica) ?: return null
        val resultRegex = "<getCargaAcademicaByAlumnoResult>(.*?)</getCargaAcademicaByAlumnoResult>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val result = resultRegex.find(xml)?.groupValues?.get(1) ?: return null
        return try {
            val jsonArray = JSONArray(result)
            val list = mutableListOf<CargaItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    CargaItem(
                        clvOficial = obj.optString("clvOficial"),
                        materia = obj.optString("Materia"),
                        grupo = obj.optString("Grupo"),
                        docente = obj.optString("Docente"),
                        creditos = obj.optInt("CreditosMateria"),
                        estadoMateria = obj.optString("EstadoMateria"),
                        observaciones = obj.optString("Observaciones"),
                        semipresencial = obj.optString("Semipresencial"),
                        lunes = obj.optString("Lunes"),
                        martes = obj.optString("Martes"),
                        miercoles = obj.optString("Miercoles"),
                        jueves = obj.optString("Jueves"),
                        viernes = obj.optString("Viernes"),
                        sabado = obj.optString("Sabado")
                    )
                )
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun calificacionesPorUnidad(): List<CalificacionUnidadItem>? {
        if (!hasSession()) return null
        val xml = postSoap("http://tempuri.org/getCalifUnidadesByAlumno", bodyCalificacionesUnidades) ?: return null
        val resultRegex = "<getCalifUnidadesByAlumnoResult>(.*?)</getCalifUnidadesByAlumnoResult>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val result = resultRegex.find(xml)?.groupValues?.get(1) ?: return null
        return try {
            val jsonArray = JSONArray(result)
            val list = mutableListOf<CalificacionUnidadItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    CalificacionUnidadItem(
                        materia = obj.optString("Materia"),
                        grupo = obj.optString("Grupo"),
                        observaciones = obj.optString("Observaciones"),
                        unidadesActivas = obj.optString("UnidadesActivas"),
                        c1 = obj.optString("C1"),
                        c2 = obj.optString("C2"),
                        c3 = obj.optString("C3"),
                        c4 = obj.optString("C4"),
                        c5 = obj.optString("C5"),
                        c6 = obj.optString("C6"),
                        c7 = obj.optString("C7"),
                        c8 = obj.optString("C8"),
                        c9 = obj.optString("C9"),
                        c10 = obj.optString("C10"),
                        c11 = obj.optString("C11"),
                        c12 = obj.optString("C12"),
                        c13 = obj.optString("C13")
                    )
                )
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun calificacionFinal(modEducativo: Int): List<CalificacionFinalItem>? {
        if (!hasSession()) return null
        val soap = bodyCalificacionFinal.format(modEducativo)
        val xml = postSoap("http://tempuri.org/getAllCalifFinalByAlumnos", soap) ?: return null
        val resultRegex = "<getAllCalifFinalByAlumnosResult>(.*?)</getAllCalifFinalByAlumnosResult>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val result = resultRegex.find(xml)?.groupValues?.get(1) ?: return null
        return try {
            val jsonArray = JSONArray(result)
            val list = mutableListOf<CalificacionFinalItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    CalificacionFinalItem(
                        calif = obj.optInt("calif"),
                        acreditacion = obj.optString("acred"),
                        grupo = obj.optString("grupo"),
                        materia = obj.optString("materia"),
                        observaciones = obj.optString("Observaciones")
                    )
                )
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
/**
 * Almacenamiento de cookies simple basado en java.util.prefs.Preferences.
 * Persiste una lista JSON de cookies para que sobrevivan entre ejecuciones.
 * No implementa políticas avanzadas de seguridad; cifrar si las cookies son sensibles.
 */
class PreferencesCookiesStorage(
    private val prefs: Preferences = Preferences.userRoot().node("com.example.sice.shared"),
    private val key: String = "cookies"
) : CookiesStorage {

    // Mutex para serializar acceso concurrente a las operaciones de lectura/escritura
    private val mutex = Mutex()

    /**
     * Añade o actualiza una cookie.
     * - Carga la lista actual, elimina entradas duplicadas (mismo name/domain/path),
     *   añade la cookie nueva y guarda la lista.
     * - Se ejecuta dentro de mutex.withLock para evitar condiciones de carrera.
     */
    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        mutex.withLock {
            val list = loadList().toMutableList()
            // Eliminar cookie previa con misma identidad (name + domain + path)
            list.removeIf { it.name == cookie.name && it.domain == cookie.domain && it.path == cookie.path }
            list.add(cookie)
            saveList(list)
        }
    }

    /**
     * Devuelve las cookies aplicables a la URL solicitada.
     * - Filtra por dominio y path para devolver solo las cookies que deberían enviarse.
     * - Protegido por mutex para consistencia.
     */
    override suspend fun get(requestUrl: Url): List<Cookie> {
        return mutex.withLock {
            val all = loadList()
            val urlHost = requestUrl.host
            all.filter { cookie ->
                // Comprueba coincidencia de dominio (soporta dominios con prefijo '.')
                val domainMatches = cookie.domain?.let { urlHost.endsWith(it.removePrefix(".")) } ?: true
                // Comprueba que la ruta de la cookie sea prefijo de la ruta de la petición
                val pathMatches = cookie.path?.let { requestUrl.encodedPath.startsWith(it) } ?: true
                domainMatches && pathMatches
            }
        }
    }

    // No hay recursos a liberar en esta implementación
    override fun close() { /* no-op */ }

    /**
     * Lee la lista de cookies desde Preferences.
     * - Si no hay datos devuelve lista vacía.
     * - Maneja errores devolviendo lista vacía (tolerancia a corrupción).
     * - Reconstruye objetos Cookie a partir del JSON almacenado.
     */
    private fun loadList(): List<Cookie> {
        val raw = prefs.get(key, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val out = mutableListOf<Cookie>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val name = o.optString("name")
                val value = o.optString("value")
                // domain puede ser "null" en el JSON; lo convertimos a null real
                val domain = o.optString("domain", null).takeIf { it != "null" }
                val path = o.optString("path", "/")
                // expires guardado como epoch millis o absent
                val expiresMillis = if (o.has("expires") && !o.isNull("expires")) o.optLong("expires", -1L) else -1L
                val expiresGmt = expiresMillis.takeIf { it > 0 }?.let { GMTDate(it) }
                val secure = o.optBoolean("secure", false)
                val httpOnly = o.optBoolean("httpOnly", false)

                // Reconstrucción mínima del objeto Cookie de Ktor
                val cookie = Cookie(
                    name = name,
                    value = value,
                    domain = domain,
                    path = path,
                    expires = expiresGmt,
                    secure = secure,
                    httpOnly = httpOnly
                )
                out.add(cookie)
            }
            out
        } catch (_: Exception) {
            // Si el JSON está corrupto o hay error, devolvemos lista vacía
            emptyList()
        }
    }

    /**
     * Serializa y guarda la lista de cookies en Preferences como un JSONArray.
     * - Guarda campos básicos: name, value, domain, path, expires (epoch millis), secure, httpOnly.
     * - Considerar cifrado si las cookies contienen información sensible.
     */
    private fun saveList(list: List<Cookie>) {
        val arr = JSONArray()
        for (c in list) {
            val o = JSONObject()
            o.put("name", c.name)
            o.put("value", c.value)
            o.put("domain", c.domain)
            o.put("path", c.path)
            // Guardamos expires como epoch millis o JSONObject.NULL si no existe
            val expiresMillis = c.expires?.timestamp ?: JSONObject.NULL
            o.put("expires", expiresMillis)
            o.put("secure", c.secure)
            o.put("httpOnly", c.httpOnly)
            arr.put(o)
        }
        // Persistir como string JSON en Preferences
        prefs.put(key, arr.toString())
    }
}