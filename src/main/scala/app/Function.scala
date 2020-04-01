package app

import scala.util.Try

object Function {


  def map2[T1, T2, R](t1y: Try[T1], t2y: Try[T2])(f: (T1, T2) => R): Try[R] =
    for (
      t1 <- t1y;
      t2 <- t2y
    ) yield f(t1, t2)

  def map3[T1, T2, T3, R](t1y: Try[T1], t2y: Try[T2], t3y: Try[T3])(f: (T1, T2, T3) => R): Try[R] =
    for(
      t1 <- t1y;
      t2 <- t2y;
      t3 <- t3y
    ) yield f(t1, t2, t3)

  def map7[T1, T2, T3, T4, T5, T6, T7, R](t1y: Try[T1], t2y: Try[T2], t3y: Try[T3], t4y: Try[T4], t5y: Try[T5], t6y: Try[T6], t7y: Try[T7])
                                         (f: (T1, T2, T3, T4, T5, T6, T7) => R): Try[R] =
    for(
      t1 <- t1y;
      t2 <- t2y;
      t3 <- t3y;
      t4 <- t4y;
      t5 <- t5y;
      t6 <- t6y;
      t7 <- t7y
    ) yield f(t1,t2,t3,t4,t5,t6,t7)

  def lift[T, R](f: T => R): Try[T] => Try[R] = _ map f

  def lift2[T1, T2, R](f: (T1, T2) => R): (Try[T1], Try[T2]) => Try[R] = (a, b) => for( ax <- a; bx <- b) yield f(ax, bx)

  def lift3[T1, T2, T3, R](f: (T1, T2, T3) => R): (Try[T1], Try[T2], Try[T3]) => Try[R] =
    (t1, t2, t3) => for(
      t1x <- t1;
      t2x <- t2;
      t3x <- t3
    ) yield f(t1x, t2x, t3x)

  def lift4[T1, T2, T3, T4, R](f: (T1, T2, T3, T4) => R): (Try[T1], Try[T2], Try[T3], Try[T4]) => Try[R] =
    (t1, t2, t3, t4) => for(
      t1x <- t1;
      t2x <- t2;
      t3x <- t3;
      t4x <- t4
    ) yield f(t1x, t2x, t3x, t4x)

  def lift7[T1, T2, T3, T4, T5, T6, T7, R](f: (T1, T2, T3, T4, T5, T6, T7) => R):
  (Try[T1], Try[T2], Try[T3], Try[T4], Try[T5], Try[T6], Try[T7]) => Try[R] =
    (t1, t2, t3, t4, t5, t6, t7) => for(
      t1x <- t1;
      t2x <- t2;
      t3x <- t3;
      t4x <- t4;
      t5x <- t5;
      t6x <- t6;
      t7x <- t7
    ) yield f(t1x, t2x, t3x, t4x, t5x, t6x, t7x)

  def invert2[T1, T2, R](f: T1 => T2 => R): T2 => T1 => R = t1 => t2 => f(t2)(t1)

  def invert3[T1, T2, T3, R](f: T1 => T2 => T3 => R): T3 => T2 => T1 => R =
    t1 => t2 => t3 => f(t3)(t2)(t1)

  def invert4[T1, T2, T3, T4, R](f: T1 => T2 => T3 => T4 => R): T4 => T3 => T2 => T1 => R =
    t1 => t2 => t3 => t4 => f(t4)(t3)(t2)(t1)

  def uncurried[T1, T2, R](f: T1 => T2 => R): (T1) => T2 => R =
    (t1) => t2 => f(t1)(t2)

  def uncurried2[T1, T2, T3, R](f: T1 => T2 => T3 => R): (T1, T2) => T3 => R =
    (t1, t2) => t3 => f(t1)(t2)(t3)

  def uncurried3[T1, T2, T3, T4, R](f: T1 => T2 => T3 => T4 => R): (T1, T2, T3) => T4 => R =
    (t1, t2, t3) => t4 => f(t1)(t2)(t3)(t4)


  def uncurried4[T1, T2, T3, T4, T5, R](f: T1 => T2 => T3 => T4 => T5 => R): (T1, T2, T3, T4) => T5 => R =
    (t1, t2, t3, t4) => t5 => f(t1)(t2)(t3)(t4)(t5)

  def uncurried7[T1, T2, T3, T4, T5, T6, T7, T8, R](f: T1 => T2 => T3 => T4 => T5 => T6 => T7 => T8 => R): (T1, T2, T3, T4, T5, T6, T7) => T8 => R =
    (t1, t2, t3, t4, t5, t6, t7) => f(t1)(t2)(t3)(t4)(t5)(t6)(t7)

  def sequence[X](xys: Seq[Try[X]]): Try[Seq[X]] = (Try(Seq[X]()) /: xys) {
    (xsy, xy) => for (xs <- xsy; x <- xy) yield xs :+ x
  }
}
