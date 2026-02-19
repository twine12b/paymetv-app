package com.paymetv.app.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Gatling simulation to test HPA scale-down behavior.
 * Starts with high load, then gradually decreases to trigger pod scale-down.
 */
class HPAScaleDownSimulation extends Simulation {

  // Configuration
  val baseUrl = System.getProperty("baseUrl", "http://localhost")
  val initialRps = System.getProperty("initialRps", "100").toInt
  val rampDownDuration = System.getProperty("rampDownDuration", "5").toInt // minutes
  val coolDownDuration = System.getProperty("coolDownDuration", "3").toInt // minutes

  // HTTP protocol configuration
  val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json")
    .userAgentHeader("Gatling-HPA-ScaleDown-Test")
    .shareConnections

  // CPU Load Scenario
  val cpuLoadScenario = scenario("CPU Load - Scale Down")
    .exec(
      http("Generate CPU Load")
        .get("/api/load/cpu")
        .queryParam("durationMs", "1500")
        .queryParam("intensity", "5")
        .check(status.is(200))
    )
    .pause(2.seconds, 4.seconds)

  // Fibonacci Scenario
  val fibonacciScenario = scenario("Fibonacci - Scale Down")
    .exec(
      http("Calculate Fibonacci")
        .get("/api/load/fibonacci")
        .queryParam("n", "35")
        .check(status.is(200))
    )
    .pause(3.seconds, 5.seconds)

  // Setup: Start high, then ramp down to near zero
  setUp(
    // CPU Load: Start at initialRps, ramp down to 10% over rampDownDuration
    cpuLoadScenario.inject(
      constantUsersPerSec(initialRps * 0.6).during(1.minute), // Initial high load
      rampUsersPerSec(initialRps * 0.6).to(initialRps * 0.1).during(rampDownDuration.minutes),
      constantUsersPerSec(initialRps * 0.1).during(coolDownDuration.minutes)
    ),
    
    // Fibonacci: Start at 40% of initialRps, ramp down to 5%
    fibonacciScenario.inject(
      constantUsersPerSec(initialRps * 0.4).during(1.minute),
      rampUsersPerSec(initialRps * 0.4).to(initialRps * 0.05).during(rampDownDuration.minutes),
      constantUsersPerSec(initialRps * 0.05).during(coolDownDuration.minutes)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.max.lt(10000),
      global.successfulRequests.percent.gt(95)
    )
}

