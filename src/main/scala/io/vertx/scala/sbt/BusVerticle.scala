package io.vertx.scala.sbt

import io.vertx.lang.scala.ScalaVerticle

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class BusVerticle extends ScalaVerticle {

  override def startFuture(): Future[Unit] = {
    vertx
      .eventBus()
      .consumer[String]("testAddress")
      .handler(_.reply(s"Hello World! ${hashCode()}"))
      .completionFuture()
  }
}
