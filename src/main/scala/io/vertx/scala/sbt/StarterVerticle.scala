package io.vertx.scala.sbt

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.lang.scala.ScalaVerticle.nameForVerticle

class StarterVerticle extends ScalaVerticle {
  override def start(): Unit = {
    vertx.deployVerticle(nameForVerticle[HttpVerticle])
    vertx.deployVerticle(nameForVerticle[BusVerticle])
  }
}
