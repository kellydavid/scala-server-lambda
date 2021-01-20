package io.github.howardjohn.lambda.http4szio

import io.github.howardjohn.lambda.ProxyEncoding._
import io.github.howardjohn.lambda.http4s.Http4sLambdaHandlerK
import org.http4s._
import zio._
import zio.Runtime
import zio.internal.Platform
import zio.interop.catz._

class Http4sLambdaHandlerZIO(val service: HttpRoutes[Task]) extends Http4sLambdaHandlerK[Task] {
  val runtime = Runtime.default

  def handleRequest(request: ProxyRequest): ProxyResponse =
    parseRequest(request)
      .map(runRequest)
      .flatMap(request => runtime.unsafeRun(request.either))
      .fold(errorResponse, identity)
}
