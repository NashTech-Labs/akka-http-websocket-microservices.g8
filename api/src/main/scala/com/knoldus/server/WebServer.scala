package com.knoldus.server

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.scaladsl.{Flow, Sink, Source}


class WebServer(implicit system: ActorSystem) extends Directives {

  var accounts: Map[String, String] = Map.empty
  val http: HttpExt = Http()

  def route =
    path("register") {
      parameter('name) { name ⇒
        handleWebSocketMessages(handler("ws://localhost:9001/register?name=" + name, name))
      }
    } ~ path("create" / Segment) { id =>
      accounts.values.toList.contains(id) match {
        case false => {
          handleWebSocketMessages(Flow[Message].collect {
            case TextMessage.Strict(txt) => txt
          }.via(Flow.fromSinkAndSource(Sink.ignore, Source.single("You are not registered!!!!!!!!!!!")))
            .map {
              case msg: String => TextMessage.Strict(msg)
            })
        }
        case true => handleWebSocketMessages(http.webSocketClientFlow(WebSocketRequest("ws://localhost:9002/create")))
      }
    }

  def handler(url: String, name: String): Flow[Message, Message, Any] = {
    http.webSocketClientFlow(WebSocketRequest(url)).map {
      case TextMessage.Strict(txt) => {
        if (txt contains ("You are already registered")) {
          TextMessage(txt)
        } else {
          accounts += (name -> txt)
          TextMessage(s"You are registered !!!! Use this ${txt} to create order!!")
        }
      }
    }

  }

}
