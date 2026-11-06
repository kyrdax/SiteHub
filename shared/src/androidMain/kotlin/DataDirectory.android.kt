import com.kyrdax.sitehub.AppContext

actual fun dataDirectory(): String = AppContext.instance.filesDir.absolutePath