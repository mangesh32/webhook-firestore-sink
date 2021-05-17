package sink.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.core.ApiFuture
import com.google.cloud.Timestamp
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.GeoPoint
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.google.cloud.firestore.QuerySnapshot
import com.google.cloud.firestore.WriteResult
import com.google.gson.Gson
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import io.micronaut.context.ApplicationContext
import io.micronaut.core.version.annotation.Version
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.swagger.v3.oas.annotations.tags.Tag
import sink.controller.dto.JsonData
import sink.controller.dto.KeyValueMap
import sink.controller.dto.Payload
import sink.firebase.FirestoreService

import javax.inject.Inject


@Slf4j
@Controller("/webhook")
@Tag(name = "Webhooks")
@Version("1")
class WebhookController {

    Firestore db
    ObjectMapper mapper = new ObjectMapper()

    WebhookController(FirestoreService firestoreService){
        db = firestoreService.getFirestore()
    }

    @Get("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    HttpResponse<String> hello() {

        HttpResponse.ok("HelloWorld")
    }

    @Post("/firestore-alerts")
    @Consumes(MediaType.APPLICATION_JSON)
    HttpResponse pushAlertsToFirestore(@Header String secret, @Body String body) {

        if(!secret || secret != 'secret'){
            return HttpResponse.unauthorized()
        }

        def slurper = new JsonSlurper()
        def parsedData = slurper.parseText(body)
        if(!(parsedData instanceof List<Payload>)){
            log.info("Hi from DAAS, msg=>{}", body)
            return HttpResponse.ok("No!!😝")
        }

        List<Payload> payloads = mapper.readValue(body, new TypeReference<List<Payload>>() {})
        Payload payload = null
        if(payloads){
            payload = payloads[0]
        }

        log.info("Alert Received = {}", payload)

        if(!payload || !payload.jsonData || !payload.jsonData.keyValueMap || !payload.jsonData.keyValueMap.threatValue){
            log.info("Alert Not Eligible, Ignoring")
            return HttpResponse.ok("Alert Skipped")
        }

        KeyValueMap data = payload.jsonData.keyValueMap

        Map incidentMap = [
                0:  [ incident : 'Fire',
                      alertType: 'Fire Alert'],
                1:  [ incident : 'Human',
                      alertType: 'Human Alert'],
                17: [ incident : 'Cat',
                      alertType: 'Animal Alert'],
                18: [ incident : 'Dog',
                      alertType: 'Animal Alert'],
                19: [ incident : 'Horse',
                      alertType: 'Animal Alert'],
                20: [ incident : 'Sheep',
                      alertType: 'Animal Alert'],
                21: [ incident : 'Cow',
                      alertType: 'Animal Alert'],
                22: [ incident : 'Elephant',
                      alertType: 'Animal Alert'],
                23: [ incident : 'Bear',
                      alertType: 'Animal Alert'],
                24: [ incident : 'Zebra',
                      alertType: 'Animal Alert'],
                25: [ incident : 'Giraffe',
                      alertType: 'Animal Alert'],
        ]

        String hubId = null
        String incident = null
        String alertType = null
        String address = null
        GeoPoint location = null
        Timestamp timestamp = null
        Integer probability = null
        String desc = null

        if(data != null){
            if(data.latitude && data.longitude){
                location = new GeoPoint(data.latitude, data.longitude)

                def get = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=${data.latitude},${data.longitude}&key=AIzaSyArCVMTlwXjiFxqynyhm5OH-F5weiQABbQ").openConnection()
                def getRC = get.getResponseCode()
                log.info("Geocode API Response="+getRC)
                if (getRC == 200) {
                    def geocodeResponse = slurper.parseText(get.getInputStream().getText())
                    if(geocodeResponse && geocodeResponse.results) {
                        address = geocodeResponse.results[0].formatted_address
                        log.info('Address='+ address)
                    }
                }
            }
            if(payload.hubId){
                hubId = payload.hubId
            }
            if(payload.epochTimeMillis){
                timestamp = Timestamp.of(new Date(payload.epochTimeMillis))
            }
            if(data.threatValue){
                List arr = data.threatValue.split(",")
                if(arr && arr.size() >= 2){

                    int key = arr[0] as int
                    if(!incidentMap.containsKey(key)){
                        log.error("Invalid incident Key, key="+ key)
                        return HttpResponse.badRequest("Invalid incident Key")
                    }

                    incident = incidentMap.get(key).get("incident")
                    alertType = incidentMap.get(key).get("alertType")
                    probability = arr[1] as Integer
                    if(arr.size() > 2)
                        desc = arr.subList(2,arr.size()).join(", ")

                    log.info("Threat incident={}, alertType={}", incident, alertType)
                }
            }
        }

        DocumentReference docRef = db.collection("threats").document()
        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set([
                "hubId": hubId,
                "incident": incident,
                "alertType": alertType,
                "probability": probability,
                "location": location,
                "address": address,
                "time": timestamp,
                "desc": desc
        ])

        // result.get() blocks on response
        log.info("Added Alert to Firestore, Update time : " + result.get().getUpdateTime())

        HttpResponse.created("Alert Created")
    }


}
