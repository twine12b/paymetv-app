package com.paymetv.app.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Gatling simulation to test HPA behavior under sudden load spikes.
 * Creates sudden bursts of traffic to test rapid scale-up capabilities.
 */
class HPASpikeTestSimulation extends Simulation {

  // Configuration
  val baseUrl = System.getProperty("baseUrl", "http://localhost")
  val baselineRps = System.getProperty("baselineRps", "20").toInt
  val spikeRps = System.getProperty("spikeRps", "150").toInt
  val spikeDuration = System.getProperty("spikeDuration", "2").toInt // minutes

  // HTTP protocol configuration
  val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json")
    .userAgentHeader("Gatling-HPA-Spike-Test")
    .shareConnections

  // CPU intensive scenario
  val cpuIntensiveScenario = scenario("CPU Intensive - Spike")
    .exec(
      http("Heavy CPU Load")
        .get("/api/load/cpu")
        .queryParam("durationMs", "3000")
        .queryParam("intensity", "8")
        .check(status.is(200))
    )
    .pause(1.second, 2.seconds)

  // Fibonacci scenario
  val fibonacciScenario = scenario("Fibonacci - Spike")
    .exec(
      http("Fibonacci Calculation")
        .get("/api/load/fibonacci")
        .queryParam("n", "40")
        .check(status.is(200))
    )
    .pause(1.second, 3.seconds)

  // Setup: Baseline -> Spike -> Baseline -> Spike pattern
  setUp(
    cpuIntensiveScenario.inject(
      constantUsersPerSec(baselineRps * 0.5).during(2.minutes), // Baseline
      rampUsersPerSec(baselineRps * 0.5).to(spikeRps * 0.6).during(30.seconds), // Spike up
      constantUsersPerSec(spikeRps * 0.6).during(spikeDuration.minutes), // Sustained spike
      rampUsersPerSec(spikeRps * 0.6).to(baselineRps * 0.5).during(1.minute), // Ramp down
      constantUsersPerSec(baselineRps * 0.5).during(2.minutes), // Back to baseline
      rampUsersPerSec(baselineRps * 0.5).to(spikeRps * 0.6).during(30.seconds), // Second spike
      constantUsersPerSec(spikeRps * 0.6).during(spikeDuration.minutes) // Sustained second spike
    ),
    
    fibonacciScenario.inject(
      constantUsersPerSec(baselineRps * 0.3).during(2.minutes),
      rampUsersPerSec(baselineRps * 0.3).to(spikeRps * 0.4).during(30.seconds),
      constantUsersPerSec(spikeRps * 0.4).during(spikeDuration.minutes),
      rampUsersPerSec(spikeRps * 0.4).to(baselineRps * 0.3).during(1.minute),
      constantUsersPerSec(baselineRps * 0.3).during(2.minutes),
      rampUsersPerSec(baselineRps * 0.3).to(spikeRps * 0.4).during(30.seconds),
      constantUsersPerSec(spikeRps * 0.4).during(spikeDuration.minutes)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.max.lt(15000), // Allow higher max during spikes
      global.successfulRequests.percent.gt(90) // 90% success rate (some failures expected during spikes)
    )
}

