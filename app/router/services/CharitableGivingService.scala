/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package router.services

import config.{AppConfig, FeatureSwitch}
import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.mvc.Request
import router.connectors.CharitableGivingConnector
import router.constants.Versions.{VERSION_1, VERSION_2}
import router.httpParsers.SelfAssessmentHttpParser.SelfAssessmentOutcome
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class CharitableGivingService @Inject()(val appConfig: AppConfig,
                                  val charitableGivingConnector: CharitableGivingConnector) extends Service {


  def put(body: JsValue)(implicit hc: HeaderCarrier, req: Request[_]): Future[SelfAssessmentOutcome] = {

    withApiVersion {
      case Some(VERSION_1) => charitableGivingConnector.put("${req.uri}", body)
      case Some(VERSION_2) => {
        println("A")
        val featureSwitch = FeatureSwitch(appConfig.featureSwitch)
        println("B")
        if (featureSwitch.isCharitableGivingV2Enabled) { 
          println("C")
          charitableGivingConnector.put(s"/$VERSION_2${req.uri}", body) 
        } else { 
          println("D")

          println("D1: " + s"${req.uri}")
          println("D2: " + body)
          println("D3: " + req)
          // println("D4: " + "${req.uri}")


          val foo = charitableGivingConnector.put("${req.uri}", body)(convertHeaderToVersion1, req)
println("FOO: " + foo)
          foo
        }
      }
    }
  }

}
