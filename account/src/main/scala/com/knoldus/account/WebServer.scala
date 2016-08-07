package com.knoldus.account

import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives
import akka.stream.scaladsl.{Flow, Sink, Source}


trait WebServer extends Directives {

  var accounts: Map[String, String] = Map.empty

  def route =
    path("register") {
      parameter('name) { name ⇒
        handleWebSocketMessages(handler(name))
      }
    }

  def handler(name: String): Flow[Message, Message, Any] = {
    Flow[Message].collect {
      case TextMessage.Strict(txt) => txt
    }.via(validateAndRegister(name))
      .map {
        case msg: String => TextMessage.Strict(msg)
      }
  }

  private def validateAndRegister(name: String) = {
    accounts.get(name) match {
      case Some(id) => Flow.fromSinkAndSource(Sink.ignore, Source.single(name + "::You are already registered !!!!"))
      case None => {
        val id = java.util.UUID.randomUUID().toString
        accounts += (name -> id)
        Flow.fromSinkAndSource(Sink.ignore, Source.single(id))
      }
    }
  }
}
