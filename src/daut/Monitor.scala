package daut

//import scala.language.implicitConversions
import common._

object Options {
  var PRINT = true
}

class Monitor[E <: AnyRef] {
  val monitorName = this.getClass().getSimpleName()

  private var monitors: List[Monitor[E]] = List()
  private var states: Set[state] = Set()
  var first: Boolean = true

  private var statesToRemove: Set[state] = Set()
  private var statesToAdd: Set[state] = Set()

  def monitor(monitors: Monitor[E]*) {
    this.monitors ++= monitors
  }

  type Transitions = PartialFunction[E, Set[state]]

  def noTransitions: Transitions = {
    case _ if false => null
  }

  val emptyStateSet: Set[state] = Set()

  trait state {
    private var transitions: Transitions = noTransitions

    var isFinal: Boolean = true

    def watch(ts: Transitions) {
      transitions = ts
    }

    def always(ts: Transitions) {
      transitions = ts andThen (_ + this)
    }

    def hot(ts: Transitions) {
      transitions = ts
      isFinal = false
    }

    def wnext(ts: Transitions) {
      transitions = ts orElse { case _ => error }
    }

    def next(ts: Transitions) {
      transitions = ts orElse { case _ => error }
      isFinal = false
    }

    def unless(ts1: Transitions)(ts2: Transitions) {
      transitions = ts1 orElse (ts2 andThen (_ + this))
    }

    def until(ts1: Transitions)(ts2: Transitions) {
      transitions = ts1 orElse (ts2 andThen (_ + this))
      isFinal = false
    }

    def apply(event: E): Option[Set[state]] =
      if (transitions.isDefinedAt(event))
        Some(transitions(event)) else None

    if (first) {
      states += this
      first = false
    }

    override def toString: String = "temporal operator"
  }

  case object ok extends state
  case object error extends state

  def error(msg: String): state = {
    println("\n*** " + msg + "\n")
    error
  }

  def watch(ts: Transitions) = new state { watch(ts) }
  def always(ts: Transitions) = new state { always(ts) }
  def hot(ts: Transitions) = new state { hot(ts) }
  def wnext(ts: Transitions) = new state { wnext(ts) }
  def next(ts: Transitions) = new state { next(ts) }
  def unless(ts1: Transitions)(ts2: Transitions) = new state { unless(ts1)(ts2) }
  def until(ts1: Transitions)(ts2: Transitions) = new state { until(ts1)(ts2) }

  def exists(pred: PartialFunction[state, Boolean]): Boolean = {
    states exists (pred orElse { case _ => false })
  }

  type StateTransitions = PartialFunction[state, Set[state]]

  def find(ts1: StateTransitions) = new {
    def orelse(otherwise: => Set[state]): Set[state] = {
      val matchingStates = states filter (ts1.isDefinedAt(_))
      if (!matchingStates.isEmpty) {
        (for (matchingState <- matchingStates) yield ts1(matchingState)).flatten
      } else
        otherwise
    }
  }

  def ensure(b: Boolean): state = {
    if (b) ok else error
  }

  def initial(s: state) { states += s }

  implicit def convState2Boolean(s: state): Boolean =
    states contains s

  implicit def convUnit2StateSet(u: Unit): Set[state] =
    Set(ok)

  implicit def convBoolean2StateSet(b: Boolean): Set[state] =
    Set(if (b) ok else error)

  implicit def convState2StateSet(state: state): Set[state] =
    Set(state)

  implicit def conTuple2StateSet(states: (state, state)): Set[state] =
    Set(states._1, states._2)

  implicit def conTriple2StateSet(states: (state, state, state)): Set[state] =
    Set(states._1, states._2, states._3)

  implicit def convList2StateSet(states: List[state]): Set[state] =
    states.toSet

  implicit def convState2AndState(s1: state) = new {
    def &(s2: state): Set[state] = Set(s1, s2)
  }

  implicit def conStateSet2AndStateSet(set: Set[state]) = new {
    def &(s: state): Set[state] = set + s
  }

  def verify(event: E): Int =  {
    var status = Constants.STATUS_UNKNWON
    if (Options.PRINT) Monitor.printEvent(event)
    for (sourceState <- states) {
      sourceState(event) match {
        case None =>
        case Some(targetStates) =>
          statesToRemove += sourceState
          for (targetState <- targetStates) {
            targetState match {
              case `error` => {
                if (Options.PRINT) println("\n*** error!\n")
                status = Constants.STATUS_FAILURE
              }
              case `ok`    => {
                if (Options.PRINT) println("\n*** Success!\n")
                status = Constants.STATUS_SUCCESS
              }
              case _       => {
                statesToAdd += targetState
                //status = Constants.STATUS_SUCCESS
              }
            }
          }
      }
    }
    states --= statesToRemove
    states ++= statesToAdd
    statesToRemove = emptyStateSet
    statesToAdd = emptyStateSet
    if (Options.PRINT) printStates()
    for (monitor <- monitors) {
      monitor.verify(event)
    }
    if (status != Constants.STATUS_FAILURE)Constants.STATUS_SUCCESS else status
  }

  def end() {
    println(s"ENDING TRACE EVALUATION FOR $monitorName")
    val hotStates = states filter (!_.isFinal)
    if (!hotStates.isEmpty) {
      println()
      println(s"*** non final $monitorName states:")
      println()
      hotStates foreach println
    }
    for (monitor <- monitors) {
      monitor.end()
    }
  }

  def apply(event:E): Unit = {
    verify(event)
  }

  def printStates() {
    if (!states.isEmpty) {
      val topline = "--- " + monitorName + ("-" * 20)
      val bottomline = "-" * topline.length
      println(topline)
      for (s <- states) {
        println(s)
      }
      println(bottomline)
      println()
    } else {
      println("no states in " + monitorName)
    }
  }
}

object Monitor {
  var currentEvent: AnyRef = null

  def printEvent(event: AnyRef) {
    if (!(event eq currentEvent)) {
      println("\n===[" + event + "]===\n")
      currentEvent = event
    }
  }
}