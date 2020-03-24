package app

import scala.util.{Failure, Success, Try}

object Decision {
  def parse(decision: String): Try[Decision] = decision match {
    case "T" => Success(apply(true))
    case "F" => Success(apply(false))
    case _ => Failure(new Exception(s"Invalid error in Decision: $decision"))
  }
}
case class Decision(decision: Boolean)
