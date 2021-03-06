package dtc.laws

import java.time.temporal.{ChronoField, ChronoUnit}
import java.time.{LocalDate, LocalTime}

import cats.kernel.laws._
import dtc.LocalDateTimeTC
import dtc.syntax.localDateTime._
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll

/**
  * Laws, that must be obeyed by any LocalDateTimeTC instance
  */
trait LocalDateTimeLaws[A] {
  implicit def D: LocalDateTimeTC[A]

  val genLocalDate: Gen[LocalDate]
  val genLocalTime: Gen[LocalTime]

  def twoConsequentNowCalls = {
    val prev = D.now
    val current = D.now
    prev ?<= current
  }

  def constructorConsistency = forAll(genLocalDate, genLocalTime) { (date: LocalDate, time: LocalTime) =>
    val dt = D.of(date, time)
    (dt.date ?== date) && (dt.time ?== time.truncatedTo(ChronoUnit.MILLIS))
  }

  def plainConstructorConsistency = forAll(genLocalDate, genLocalTime) { (date: LocalDate, time: LocalTime) =>
    val dt = D.of(
      date.getYear, date.getMonthValue, date.getDayOfMonth,
      time.getHour, time.getMinute, time.getSecond, time.get(ChronoField.MILLI_OF_SECOND))
    (dt.date ?== date) && (dt.time ?== time.truncatedTo(ChronoUnit.MILLIS))
  }
}

object LocalDateTimeLaws {
  def apply[A](
    gLocalTime: Gen[LocalTime],
    gLocalDate: Gen[LocalDate])(
    implicit ev: LocalDateTimeTC[A]): LocalDateTimeLaws[A] = new LocalDateTimeLaws[A] {
    def D: LocalDateTimeTC[A] = ev
    val genLocalDate: Gen[LocalDate] = gLocalDate
    val genLocalTime: Gen[LocalTime] = gLocalTime
  }
}
