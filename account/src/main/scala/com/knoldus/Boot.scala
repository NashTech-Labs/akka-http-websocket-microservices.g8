package com.knoldus

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.knoldus.account.WebServer

import scala.util.{Failure, Success}

object Boot extends App with WebServer {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher


  val bindingFuture = Http().bindAndHandle(route, "localhost", 9001)

  bindingFuture.onComplete {
    case Success(binding) ⇒
      println(s"Server is listening on localhost:9001")
    case Failure(e) ⇒
      println(s"Binding failed with ${e.getMessage}")
      system.terminate()
  }

}
