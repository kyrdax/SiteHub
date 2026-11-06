import java.io.File

actual fun dataDirectory(): String {
    val dir = File(System.getProperty("user.home"), ".sitehub")
    dir.mkdirs()
    return dir.absolutePath
}