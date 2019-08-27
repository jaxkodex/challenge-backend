package pe.com.intercorp.retail.challenge.data;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pe.com.intercorp.retail.challenge.api.dto.ClientStats;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Repository
public class KpiRepository {
    private static final String COLLECTION_NAME = "kpiclients";
    private CollectionReference collectionReference;
    private Executor taskExecutor;

    @Autowired
    public KpiRepository(Firestore firestore, Executor taskExecutor) {
        this.collectionReference = firestore.collection(COLLECTION_NAME);
        this.taskExecutor = taskExecutor;
    }

    public CompletableFuture get() {
        ApiFuture<DocumentSnapshot> response = collectionReference.document("stats").get();
        CompletableFuture<ClientStats> stats = new CompletableFuture<>();
        ApiFutures.addCallback(response, new ApiFutureCallback<DocumentSnapshot>() {
            @Override
            public void onFailure(Throwable t) {
                stats.completeExceptionally(t);
            }

            @Override
            public void onSuccess(DocumentSnapshot result) {
                try {
                    stats.complete(result.toObject(ClientStats.class));
                } catch (Exception e) {
                    stats.completeExceptionally(e);
                }
            }
        }, taskExecutor);
        return stats;
    }
}
