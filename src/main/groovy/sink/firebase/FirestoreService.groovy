package sink.firebase

import com.google.auth.oauth2.ComputeEngineCredentials
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Context

import javax.inject.Singleton

@Context
@CompileStatic
@Singleton
class FirestoreService {

    Firestore db

    FirestoreService(){

        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("key.json")
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount)
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options)
        db = FirestoreClient.getFirestore()
    }

    Firestore getFirestore(){
        return this.db
    }

}
