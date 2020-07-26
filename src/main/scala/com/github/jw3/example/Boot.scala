package com.github.jw3.example

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import io.cloudstate.operator.Watcher
import skuber.api.client.{EventType, WatchEvent}
import skuber.json.batch.format._
import skuber.json.format._

object Boot extends App {
  private val operatorNamespace = "default"

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  import system.dispatcher
  val client = skuber.k8sInit

  Watcher.watch[gvc.Resource](
    client.usingNamespace(operatorNamespace),
    Flow[WatchEvent[gvc.Resource]].map {
      case WatchEvent(EventType.ADDED, gv) =>
        println(s"gvc ${gv.name} added ${gv.spec.uri}")
        client create Cloner.make(gv)
      case WatchEvent(EventType.MODIFIED, gv) =>
        println(s"gvc ${gv.name} modified")
      case WatchEvent(EventType.DELETED, gv) =>
        println(s"gvc ${gv.name} deleted")
      case _ =>
    }
  )
}
