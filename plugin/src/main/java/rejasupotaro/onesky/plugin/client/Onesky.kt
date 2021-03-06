package rejasupotaro.onesky.plugin.client

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.result.Result
import org.apache.commons.codec.binary.Hex
import java.io.File
import java.security.MessageDigest

/** @see <a href="https://github.com/onesky/api-documentation-platform/blob/master/README.md">API</a> */
class Onesky(val apiKey: String, val apiSecret: String, val projectId: Int) {
    val endpoint = "https://platform.api.onesky.io"
    val version = 1
    val urlPrefix
        get() = "$endpoint/$version"
    var httpClient = HttpClient()

    /** Get list of files.
     * @see <a href="https://github.com/onesky/api-documentation-platform/blob/master/resources/file.md">Files API</a>
     * */
    fun files(): Result<String, FuelError> {
        val params = authParams()

        return httpClient.get("$urlPrefix/projects/$projectId/files", params)
    }

    /** Get all locales.
     * @see <a hef="https://github.com/onesky/api-documentation-platform/blob/master/resources/locale.md">Locales API</a>
     * */
    fun locales(): Result<String, FuelError> {
        val params = authParams()

        return httpClient.get("$urlPrefix/locales", params)
    }

    /** Get specific file.
     * @see <a href="https://github.com/onesky/api-documentation-platform/blob/master/resources/translation.md">Translations API</a>
     * */
    fun downloadFile(locale: String, fileName: String): Result<String, FuelError> {
        val params = authParams()
        params.add("source_file_name" to fileName)
        params.add("locale" to locale)

        return httpClient.get("$urlPrefix/projects/$projectId/translations", params)
    }

    /** Get 'strings.xml' file.
     * @see <a href="https://github.com/onesky/api-documentation-platform/blob/master/resources/translation.md">Translations API</a>
     * */
    fun download(locale: String): Result<String, FuelError> {
        val params = authParams()
        params.add("source_file_name" to "strings.xml")
        params.add("locale" to locale)

        return httpClient.get("$urlPrefix/projects/$projectId/translations", params)
    }

    /**
     * @see <a href="https://github.com/onesky/api-documentation-platform/blob/master/resources/file.md">Files API</a>
     * */
    fun upload(translationFile: File): Result<String, FuelError> {
        val params = authParams()
        params.add("file" to translationFile.name)
        params.add("file_format" to "ANDROID_XML")

        return httpClient.post("$urlPrefix/projects/$projectId/files", params, translationFile)
    }

    fun languages(): Result<String, FuelError> {
        val params = authParams()
        return httpClient.get("$urlPrefix/projects/$projectId/languages", params)
    }

    fun authParams(): MutableList<Pair<String, String>> {
        val md = MessageDigest.getInstance("MD5")
        val timestamp = (System.currentTimeMillis() / 1000L).toString()
        val devHash = Hex.encodeHexString(md.digest((timestamp + apiSecret).toByteArray()))

        return mutableListOf("api_key" to apiKey,
                "dev_hash" to devHash,
                "timestamp" to timestamp)
    }
}
