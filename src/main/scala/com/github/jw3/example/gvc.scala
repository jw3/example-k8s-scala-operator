package com.github.jw3.example

import java.time.ZonedDateTime

import play.api.libs.functional.syntax._
import play.api.libs.json._
import skuber.ResourceSpecification.Subresources
import skuber.apiextensions.CustomResourceDefinition
import skuber.json.format._
import skuber.{CustomResource, ListResource, ResourceDefinition}

object gvc {
  type Resource = CustomResource[Spec, Status]
  type ResourceList = ListResource[Resource]

  case class Spec(
      uri: String,
      ref: String,
      pvsz: String
  )
  object Spec {
    implicit val format: Format[Spec] = Json.format
  }

  case class Condition(
      `type`: String,
      status: String,
      reason: Option[String] = None,
      message: Option[String] = None,
      severity: Option[String] = None,
      lastUpdateTime: Option[ZonedDateTime] = None,
      lastTransitionTime: Option[ZonedDateTime] = None
  )

  object Condition {
    private implicit val timeFormat: Format[ZonedDateTime] =
      Format(skuber.json.format.timeReads, skuber.json.format.timewWrites)
    implicit val format: Format[Condition] = Json.format
  }

  case class Status(
      conditions: List[Condition]
  )

  object Status {
    implicit val format: Format[Status] =
      (__ \ "conditions")
        .formatNullable[List[Condition]]
        .inmap[Status](c => Status(c.getOrElse(Nil)), s => Some(s.conditions))
  }

  implicit val mycrdResourceDefinition = ResourceDefinition[Resource](
    group = "jw3.github.com",
    version = "v1",
    kind = "GVC",
    shortNames = Nil,
    subresources = Some(Subresources().withStatusSubresource)
  )

  implicit val statusSubEnabled = CustomResource.statusMethodsEnabler[Resource]

  val crd = CustomResourceDefinition[Resource]
  def apply(name: String, spec: Spec) = CustomResource[Spec, Status](spec).withName(name)
}
