package com.paymetv.app.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Gatling simulation to test HPA stability under sustained load.
 * Maintains constant load at different levels to verify stable replica counts.
 */
class HPASustainedLoadSimulation extends Simulation {

  // Configuration
  val baseUrl = System.getProperty("baseUrl", "http://localhost")
  val targetRps = System.getProperty("targetRps", "80").toInt
  val testDuration = System.getProperty("testDuration", "10").toInt // minutes

  // HTTP protocol configuration
  val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json")
    .userAgentHeader("Gatling-HPA-Sustained-Test")
    .shareConnections

  // Mixed workload scenario
  val mixedWorkloadScenario = scenario("Mixed Workload - Sustained")
    .randomSwitch(
      50.0 -> exec(
        http("CPU Load")
          .get("/api/load/cpu")
          .queryParam("durationMs", "1500")
          .queryParam("intensity", "6")
          .check(status.is(200))
      ),
      30.0 -> exec(
        http("Fibonacci")
          .get("/api/load/fibonacci")
          .queryParam("n", "36")
          .check(status.is(200))
      ),
      15.0 -> exec(
        http("Prime Numbers")
          .get("/api/load/prime")
          .queryParam("limit", "300000")
          .check(status.is(200))
      ),
      5.0 -> exec(
        http("Health Check")
          .get("/api/health")
          .check(status.is(200))
      )
    )
    .pause(1.second, 3.seconds)

  // Setup: Sustained constant load
  setUp(
    mixedWorkloadScenario.inject(
      rampUsersPerSec(0).to(targetRps).during(2.minutes), // Ramp up
      constantUsersPerSec(targetRps).during(testDuration.minutes) // Sustain
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.percentile3.lt(8000), // 99th percentile < 8s
      global.responseTime.mean.lt(3000), // Mean response time < 3s
      global.successfulRequests.percent.gt(98) // 98% success rate
    )
}

