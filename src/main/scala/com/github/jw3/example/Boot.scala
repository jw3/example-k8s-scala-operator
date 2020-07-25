package com.github.jw3.example

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object Boot extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
}
