package app

import scala.util.Try

object Function {

  def lift[T, R](f: T => R): Try[T] => Try[R] = _ map f

  def uncurried[T1, T2, R](f: T1 => T2 => R): (T1) => T2 => R =
    (t1) => t2 => f(t1)(t2)
}
