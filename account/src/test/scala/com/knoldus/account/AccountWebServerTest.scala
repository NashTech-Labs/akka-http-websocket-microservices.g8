package com.knoldus.account

import akka.http.scaladsl.model.headers.{CustomHeader, Upgrade, UpgradeProtocol}
import akka.http.scaladsl.model.ws.{Message, UpgradeToWebSocket}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.{FlowShape, Graph}
import org.scalatest.{Matchers, WordSpec}


class AccountWebServerTest extends WordSpec with Matchers with ScalatestRouteTest with WebServer {

  "User" should {
    "be able to create account" in {
      Get("/register?name=john") ~>
        Upgrade(List(UpgradeProtocol("websocket"))) ~> emulateHttpCore ~>
        route ~> check {
        status shouldEqual StatusCodes.SwitchingProtocols
      }
    }
  }

  private def emulateHttpCore(req: HttpRequest): HttpRequest =
    req.header[Upgrade] match {
      case Some(upgrade) if upgrade.hasWebSocket => req.copy(headers = req.headers :+ upgradeToWebsocketHeaderMock)
      case _ => req
    }

  private def upgradeToWebsocketHeaderMock: UpgradeToWebSocket =
    new CustomHeader() with UpgradeToWebSocket {
      override def requestedProtocols = Nil

      override def name = "dummy"

      override def value = "dummy"

      override def renderInRequests = true

      override def renderInResponses = true

      override def handleMessages(handlerFlow: Graph[FlowShape[Message, Message], Any], subprotocol: Option[String]): HttpResponse =
        HttpResponse(StatusCodes.SwitchingProtocols)
    }
}
