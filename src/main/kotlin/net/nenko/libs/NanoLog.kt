package net.nenko.libs

import java.io.File
import java.io.FileNotFoundException
import java.io.PrintStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class NanoLogLevel(private val lvl: Int) {
   FATAL(0),
   ERROR(1),
   WARN(2),
   INFO(3),
   DEBUG(4),
   TRACE(5);

   fun isEnabled(thresholdLevel: NanoLogLevel) = lvl <= thresholdLevel.lvl;
}

private val NANO_LOG_FORMATTER1 = DateTimeFormatter.ofPattern("yyMMdd HHmmss")

/**
 * Lightweight single thread logger, suitable for command line applications
 */
class NanoLog(val level: NanoLogLevel, val fileTemplate: String?) {

   private val printStream: PrintStream =
      if(fileTemplate == null) {
         System.out;
      } else {
         val file = File(fileTemplate)
         try {
            PrintStream(file)
         } catch (e: FileNotFoundException) {
            System.out.println("NanoLog error - failed to open/create file $fileTemplate")
            System.out
         }
      }


   private fun buildRecord(template: String, severity: NanoLogLevel, args: Array<Any>): String {
      val sb = StringBuilder(template.length + args.size * 10)
      val now = LocalDateTime.now()
      val formattedDateTime = if (printStream === System.out) {
         // output to the console - use local time in logged record
         now.format(NANO_LOG_FORMATTER1)
      } else {
         // output to a file - use UTC time in logged record
         now.atZone(ZoneId.of("UTC")).format(NANO_LOG_FORMATTER1)
      }
      sb.append(formattedDateTime)
      sb.append(' ').append(severity.name).append(" - ")
      if (args.size == 0) {
         sb.append(template)
      } else {
         appendMsg(sb, template, args)
      }
      sb.append('\n')
      return sb.toString()
   }


   private fun appendMsg(sb: StringBuilder, template: String, arguments: Array<Any>) {
      val parts = template.split("\\{\\}".toRegex(), 9999).toTypedArray()
      for (i in parts.indices) {
         sb.append(parts[i])
         if (i < parts.size - 1) {
            sb.append(if (i < arguments.size) arguments[i].toString() else "{}")
         }
      }
   }

   private fun writeRecord(record: String) {
      printStream.print(record)
   }

   fun error(format: String) {
      if (NanoLogLevel.ERROR.isEnabled(level)) {
         val record: String = buildRecord(format, NanoLogLevel.ERROR, arrayOf())
         writeRecord(record)
      }
   }

}