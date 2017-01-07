package examples
import daut._
import common._

trait LockEvent
case class acquire(thread: Int, lock: Int) extends LockEvent
case class release(thread: Int, lock: Int) extends LockEvent
               
class Test extends Monitor[LockEvent] {
  //daut.Options.PRINT = false
  case class Locked(thread: Int, lock: Int) extends state {
    hot {
      case acquire(_, `lock`) => error
      case release(`thread`, `lock`) => ok
    }
  }

  always {
    case acquire(t, l)                  => Locked(t, l)
    case release(t, l) if !Locked(t, l) => error
  }
}

object Main extends App {
  override def main(args: Array[String]) {
    val m = new Test
    var status = m.verify(acquire(1, 10))
    if(Constants.STATUS_SUCCESS == status) 
      println("Success") 
    else if (Constants.STATUS_UNKNWON == status)
      println("Unknown")
    else println("Failure")
    
    status = m.verify(acquire(2, 10))
    if(Constants.STATUS_SUCCESS == status) 
      println("Success") 
    else if (Constants.STATUS_UNKNWON == status)
      println("Unknown")
    else println("Failure")  
  }
}