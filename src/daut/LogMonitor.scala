package daut

trait LogEvent
case class login(username: String) extends LogEvent
case class logout(username: String) extends LogEvent
case class register(username: String) extends LogEvent

class LogMonitor extends Monitor[LogEvent] {
  
}