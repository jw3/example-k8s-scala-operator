package com.github.jw3.example

import com.github.jw3.example.gvc.{Resource => GVC}
import skuber.batch.Job
import skuber.{EnvVar, Pod, RestartPolicy, Volume}

object Cloner {
  private val mountPath = "/opt/app-root/mount"
  private val datadir = "data"

  def make(gvc: GVC): Job = {
    val vol = Volume(
      gvc.name,
      Volume.GitRepo(
        gvc.spec.uri,
        Some(gvc.spec.ref),
        Some(datadir)
      )
    )

    val container = skuber.Container(
      name = "lfs",
      image = "jwiii/python-36-centos7-lfs:latest",
      volumeMounts = List(
        Volume.Mount(
          gvc.name,
          mountPath
        )
      ),
      workingDir = Some(s"$mountPath/$datadir"),
      command = List("git", "lfs", "fetch")
    )

    val spec = Pod.Spec().addContainer(container).withRestartPolicy(RestartPolicy.Never).addVolume(vol)
    val templateSpec = Pod.Template.Spec.named("lfs").withPodSpec(spec)

    Job(gvc.name).withTemplate(templateSpec)
  }
}
