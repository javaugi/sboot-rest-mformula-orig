
package com.spring5.aicloud.gcp.dataflow;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {

    private final Firestore db;

    public FirestoreService() {
        FirestoreOptions options = FirestoreOptions.getDefaultInstance();
        this.db = options.getService();
    }

    public String save(String collection, Map<String, Object> data) {
        try {
            DocumentReference docRef = db.collection(collection).document();
            ApiFuture<WriteResult> future = docRef.create(data);
            return future.get().getUpdateTime().toString();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
