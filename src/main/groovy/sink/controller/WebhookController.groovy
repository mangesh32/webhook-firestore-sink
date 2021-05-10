package sink.controller

import com.google.api.core.ApiFuture
import com.google.cloud.Timestamp
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.GeoPoint
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.google.cloud.firestore.QuerySnapshot
import com.google.cloud.firestore.WriteResult
import com.google.gson.Gson
import groovy.util.logging.Slf4j
import io.micronaut.context.ApplicationContext
import io.micronaut.core.version.annotation.Version
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.swagger.v3.oas.annotations.tags.Tag
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

        ApiFuture<QuerySnapshot> query = db.collection("alerts").get()

        // query.get() blocks on response
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments()
        for (QueryDocumentSnapshot document : documents) {
            response.add(document.getData())
        }

        return HttpResponse.ok(response)
    }

    @Post("/firestore-alerts")
    HttpResponse pushAlertsToFirestore() {

        DocumentReference docRef = db.collection("alerts").document()

        Map<String, Object> data = new HashMap<>();
        data.put("incident", "FIRE")
        data.put("probability", 79)
        data.put("time", Timestamp.of(new Date()))
        data.put("location", new GeoPoint(90,180))

        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);

        // result.get() blocks on response
        log.info("Added Alert to Firestore, Update time : " + result.get().getUpdateTime())

        HttpResponse.created("Added Alert to Firestore")
    }


}
