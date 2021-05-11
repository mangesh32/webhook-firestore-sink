package sink.controller

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
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.swagger.v3.oas.annotations.tags.Tag
import sink.controller.dto.JsonData
import sink.firebase.FirestoreService

import javax.inject.Inject


@Slf4j
@Controller("/webhook")
@Tag(name = "Webhooks")
@Version("1")
class WebhookController {

    Firestore db

    WebhookController(FirestoreService firestoreService){
        db = firestoreService.getFirestore()
    }

    @Get("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    HttpResponse<String> hello() {

        HttpResponse.ok("HelloWorld")
    }

    @Get("/firestore-alerts")
    @Produces(MediaType.APPLICATION_JSON)
    HttpResponse getAlerts() {

        def response = new ArrayList()

        ApiFuture<QuerySnapshot> query = db.collection("threats").get()

        // query.get() blocks on response
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments()
        for (QueryDocumentSnapshot document : documents) {
            response.add(document.getData())
        }

        return HttpResponse.ok(response)
    }

    @Post("/firestore-alerts")
    HttpResponse pushAlertsToFirestore(@Body JsonData data) {

        Map incidentMap = [
                0: [ incident : 'Fire', alertType: 'Fire Alert'],
                1: [ incident : 'Human', alertType: 'Human Alert'],
                17: [ incident : 'Cat', alertType: 'Animal Alert'],
                18: [ incident : 'Dog', alertType: 'Animal Alert'],
                19: [ incident : 'Horse', alertType: 'Animal Alert'],
                20: [ incident : 'Sheep', alertType: 'Animal Alert'],
                21: [ incident : 'Cow', alertType: 'Animal Alert'],
                22: [ incident : 'Elephant', alertType: 'Animal Alert'],
                23: [ incident : 'Bear', alertType: 'Animal Alert'],
                24: [ incident : 'Zebra', alertType: 'Animal Alert'],
                25: [ incident : 'Giraffe', alertType: 'Animal Alert'],
        ]

        String incident = null
        String alertType = null
        GeoPoint location = null
        Timestamp timestamp = null
        Integer probability = null
        String desc = null

        if(data != null){
            if(data.latitude && data.longitude){
                location = new GeoPoint(data.latitude, data.longitude)
            }
            if(data.epochTimeMillis){
                timestamp = Timestamp.of(new Date(data.epochTimeMillis))
            }
            if(data.threatFlag != null){
                log.info("Threat Flag=" + data.threatFlag)
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
                "incident": incident,
                "alertType": alertType,
                "probability": probability,
                "location": location,
                "time": timestamp,
                "desc": desc
        ])

        // result.get() blocks on response
        log.info("Added Alert to Firestore, Update time : " + result.get().getUpdateTime())

        HttpResponse.created("Alert Created")
    }


}
