package com.knoldus.order

import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import com.knoldus.order.WebServer
import org.scalatest.{Matchers, WordSpec}


class OrderWebServerTest extends WordSpec with Matchers with ScalatestRouteTest with WebServer{

  "User" should {
    "should be able to create order" in {
      val wsClient = WSProbe()
      WS("/create", wsClient.flow) ~> route ~>
        check {
          // check response for WS Upgrade headers
          isWebSocketUpgrade shouldEqual true

          // manually run a WS conversation
          wsClient.sendMessage("10000")
          wsClient.expectMessage("Your order is created. Order value is 10000")
        }
    }
  }
}
