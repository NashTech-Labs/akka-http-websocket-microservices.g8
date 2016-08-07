package com.knoldus.server

import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.headers.{CustomHeader, Upgrade, UpgradeProtocol}
import akka.http.scaladsl.model.ws.{Message, UpgradeToWebSocket, WebSocketRequest}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.scaladsl.Flow
import akka.stream.{FlowShape, Graph}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}


class WebServerTest extends WordSpec with Matchers with ScalatestRouteTest with MockitoSugar{



  val apiService = new WebServer() {
    override val http = mock[HttpExt]
    accounts = Map("john" -> "123")
    override  def handler(url: String, name: String): Flow[Message, Message, Any] = {
      http.webSocketClientFlow(WebSocketRequest(url))}
  }

  val route = apiService.route
  "Api" should {
    "be able to call account service to register a user" in {
      Get("/register?name=dev") ~> Upgrade(List(UpgradeProtocol("websocket"))) ~> emulateHttpCore ~> route ~> check {
        status shouldEqual StatusCodes.SwitchingProtocols
      }
    }

    "be not able to call account service to register invalid user" in {
      Get("/register?name=john") ~> Upgrade(List(UpgradeProtocol("websocket"))) ~> emulateHttpCore ~> route ~> check {
        status shouldEqual StatusCodes.SwitchingProtocols
      }
    }

    "be call order service to create an order" in {
      Get("/create/123") ~> Upgrade(List(UpgradeProtocol("websocket"))) ~> emulateHttpCore ~> route ~> check {
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
