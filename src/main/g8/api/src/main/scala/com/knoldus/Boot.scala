package com.knoldus

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.knoldus.server.WebServer

import scala.util.{Failure, Success}

object Boot extends App {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val api = new WebServer
  val bindingFuture = Http().bindAndHandle(api.route, "localhost", 8080)

  bindingFuture.onComplete {
    case Success(binding) ⇒
      println(s"Server is listening on localhost:8080")
    case Failure(e) ⇒
      println(s"Binding failed with ${e.getMessage}")
      system.terminate()
  }

}
