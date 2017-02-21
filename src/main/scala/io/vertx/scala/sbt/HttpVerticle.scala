package io.vertx.scala.sbt

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.eventbus.DeliveryOptions
import io.vertx.scala.ext.web.Router

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class HttpVerticle extends ScalaVerticle {


  override def startFuture(): Future[Unit] = {
    //Create a router to answer GET-requests to "/hello" with "world"
    val router = Router.router(vertx)
    val deliveryOptions = DeliveryOptions().setSendTimeout(100)
    val route = router
      .get("/hello")
      .handler(httpRequest => {
        vertx.eventBus()
          .sendFuture[String]("testAddress", "test", DeliveryOptions().setSendTimeout(100))
          .onComplete {
            case Success(s) => httpRequest.response().end(s"world ${s.body()}")
            case Failure(t) => httpRequest.response().end(s"failed ${t.getMessage}")
          }
      })

    vertx
      .createHttpServer()
      .requestHandler(router.accept)
      .listenFuture(8666)
      .map(_ => ())
  }
}
