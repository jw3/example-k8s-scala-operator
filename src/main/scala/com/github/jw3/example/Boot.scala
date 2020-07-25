package com.github.jw3.example

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import skuber.ConfigMap
import skuber.api.client.{EventType, WatchEvent}
import skuber.json.format._

object Boot extends App {
  private val operatorNamespace = "default"
  private val configMapName = "example-scala-operator"

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  import system.dispatcher
  val client = skuber.k8sInit

  Watcher.watchSingle[ConfigMap](
    client.usingNamespace(operatorNamespace),
    configMapName,
    Flow[WatchEvent[ConfigMap]].map {
      case WatchEvent(EventType.ADDED, map) =>
        println(s"cm ${map.name} added")
      case WatchEvent(EventType.MODIFIED, map) =>
        println(s"cm ${map.name} modified")
      case WatchEvent(EventType.DELETED, map) =>
        println(s"cm ${map.name} deleted")
      case _ =>
    }
  )
}
