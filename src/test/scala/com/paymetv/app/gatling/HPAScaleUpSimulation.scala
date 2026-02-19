package com.paymetv.app.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Gatling simulation to test HPA scale-up behavior.
 * Gradually increases load to trigger horizontal pod autoscaling.
 */
class HPAScaleUpSimulation extends Simulation {

  // Configuration - can be overridden via system properties
  val baseUrl = System.getProperty("baseUrl", "http://localhost")
  val targetRps = System.getProperty("targetRps", "100").toInt
  val rampDuration = System.getProperty("rampDuration", "5").toInt // minutes
  val sustainDuration = System.getProperty("sustainDuration", "3").toInt // minutes

  // HTTP protocol configuration
  val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json")
    .userAgentHeader("Gatling-HPA-Test")
    .shareConnections

  // Scenario 1: CPU Load Test - Gradual Ramp Up
  val cpuLoadScenario = scenario("CPU Load - Scale Up")
    .exec(
      http("Generate CPU Load")
        .get("/api/load/cpu")
        .queryParam("durationMs", "2000")
        .queryParam("intensity", "7")
        .check(status.is(200))
        .check(jsonPath("$.requestId").exists)
    )
    .pause(1.second, 3.seconds)

  // Scenario 2: Fibonacci Calculation
  val fibonacciScenario = scenario("Fibonacci - Scale Up")
    .exec(
      http("Calculate Fibonacci")
        .get("/api/load/fibonacci")
        .queryParam("n", "38")
        .check(status.is(200))
        .check(jsonPath("$.result").exists)
    )
    .pause(2.seconds, 4.seconds)

  // Scenario 3: Prime Number Calculation
  val primeScenario = scenario("Prime Numbers - Scale Up")
    .exec(
      http("Find Prime Numbers")
        .get("/api/load/prime")
        .queryParam("limit", "500000")
        .check(status.is(200))
        .check(jsonPath("$.primeCount").exists)
    )
    .pause(1.second, 2.seconds)

  // Scenario 4: Health Check (lightweight)
  val healthCheckScenario = scenario("Health Check")
    .exec(
      http("Health Check")
        .get("/api/health")
        .check(status.is(200))
    )
    .pause(5.seconds)

  // Setup the simulation with multiple scenarios
  setUp(
    // CPU Load: Ramp up from 0 to targetRps over rampDuration, then sustain
    cpuLoadScenario.inject(
      rampUsersPerSec(0).to(targetRps * 0.5).during(rampDuration.minutes),
      constantUsersPerSec(targetRps * 0.5).during(sustainDuration.minutes)
    ),
    
    // Fibonacci: Ramp up from 0 to 30% of targetRps
    fibonacciScenario.inject(
      rampUsersPerSec(0).to(targetRps * 0.3).during(rampDuration.minutes),
      constantUsersPerSec(targetRps * 0.3).during(sustainDuration.minutes)
    ),
    
    // Prime: Ramp up from 0 to 20% of targetRps
    primeScenario.inject(
      rampUsersPerSec(0).to(targetRps * 0.2).during(rampDuration.minutes),
      constantUsersPerSec(targetRps * 0.2).during(sustainDuration.minutes)
    ),
    
    // Health Check: Constant low rate
    healthCheckScenario.inject(
      constantUsersPerSec(2).during((rampDuration + sustainDuration).minutes)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.max.lt(10000), // Max response time < 10s
      global.successfulRequests.percent.gt(95) // 95% success rate
    )
}

