package com.knoldus.order

import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives
import akka.stream.scaladsl.{Flow, Source}


trait WebServer extends Directives {

  def route =
    path("create") {
        handleWebSocketMessages(handler)
    }

  def handler: Flow[Message, Message, Any] = {
    Flow[Message].mapConcat {
      case tm: TextMessage =>
        TextMessage(Source.single("Your order is created. Order value is ") ++ tm.textStream) :: Nil
    }

  }
}
