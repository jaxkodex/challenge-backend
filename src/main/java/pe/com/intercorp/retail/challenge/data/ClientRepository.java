package pe.com.intercorp.retail.challenge.data;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pe.com.intercorp.retail.challenge.api.dto.Client;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Repository
public class ClientRepository {
    private static final String COLLECTION_NAME = "clients";
    private static final Integer DEFAULT_QUERY_SIZE = 10;
    private static final Integer DEFAULT_QUERY_START = 0;
    private CollectionReference collectionReference;
    private Executor taskExecutor;

    @Autowired
    public ClientRepository(Firestore firestore, Executor taskExecutor) {
        this.collectionReference = firestore.collection(COLLECTION_NAME);
        this.taskExecutor = taskExecutor;
    }

    public CompletableFuture<Client> save(Client client) {
        ApiFuture<WriteResult> future = this.collectionReference.document(client.getId().toString())
                .set(client, SetOptions.merge());
        CompletableFuture<Client> theFuture = new CompletableFuture<>();

        ApiFutures.addCallback(future, new ApiFutureCallback<WriteResult>() {
            @Override
            public void onFailure(Throwable t) {
                theFuture.completeExceptionally(t);
            }

            @Override
            public void onSuccess(WriteResult result) {
                theFuture.complete(client);
            }
        }, taskExecutor);

        return theFuture;
    }

    public CompletableFuture<List<Client>> list(Integer startAt, Integer size) {
        Query query = collectionReference
                .orderBy("name")
                .startAt(Optional.of(startAt).orElse(DEFAULT_QUERY_START))
                .limit(Optional.ofNullable(size).orElse(DEFAULT_QUERY_SIZE));
        ApiFuture<QuerySnapshot> future = query.get();
        CompletableFuture<List<Client>> theFuture = new CompletableFuture();

        ApiFutures.addCallback(future, new ApiFutureCallback<QuerySnapshot>() {
            @Override
            public void onFailure(Throwable t) {
                theFuture.completeExceptionally(t);
            }

            @Override
            public void onSuccess(QuerySnapshot result) {
                try {
                    theFuture.complete(result.toObjects(Client.class));
                } catch (Exception e) {
                    theFuture.completeExceptionally(e);
                }
            }
        }, taskExecutor);
        return theFuture;
    }
}
