package controllers

import org.scalatest.ParallelTestExecution
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Injecting

trait ControllerBaseSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with ParallelTestExecution
