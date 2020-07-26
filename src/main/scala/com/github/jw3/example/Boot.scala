package com.github.jw3.example

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import io.cloudstate.operator.Watcher
import skuber.api.client.{EventType, WatchEvent}
import skuber.json.format._

object Boot extends App {
  private val operatorNamespace = "default"

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  import system.dispatcher
  val client = skuber.k8sInit

  Watcher.watch[MyCRD.Resource](
    client.usingNamespace(operatorNamespace),
    Flow[WatchEvent[MyCRD.Resource]].map {
      case WatchEvent(EventType.ADDED, crd) =>
        println(s"mycrd ${crd.name} added with uri of ${crd.spec.uri}")
      case WatchEvent(EventType.MODIFIED, crd) =>
        println(s"mycrd ${crd.name} modified")
      case WatchEvent(EventType.DELETED, crd) =>
        println(s"mycrd ${crd.name} deleted")
      case _ =>
    }
  )
}
