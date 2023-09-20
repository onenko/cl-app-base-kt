import net.nenko.libs.NanoLog
import net.nenko.libs.NanoLogLevel

val log = NanoLog(NanoLogLevel.INFO, null)

fun main(args: Array<String>) {
   println("Hello World!")

   // Try adding program arguments at Run/Debug configuration
   println("Program arguments: ${args.joinToString()}")

   log.error("TEST")

}
