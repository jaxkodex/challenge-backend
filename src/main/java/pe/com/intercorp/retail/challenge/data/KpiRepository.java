package pe.com.intercorp.retail.challenge.data;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pe.com.intercorp.retail.challenge.api.dto.Client;
import pe.com.intercorp.retail.challenge.api.dto.ClientStats;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;

@Repository
@AllArgsConstructor
public class KpiRepository {
    private ClientRepository clientRepository;

    public CompletableFuture<ClientStats> get(Integer startAt, Integer size) {
        return clientRepository.list(startAt, size).thenApply(clients ->
                new ClientStats(clients.stream().mapToDouble(Client::getAge).average().orElse(Double.NaN),
                        Math.sqrt(clients.stream().mapToDouble(Client::getAge).boxed().collect(VARIANCE_COLLECTOR))));
    }

    private static final Collector<Double, double[], Double> VARIANCE_COLLECTOR = Collector.of( // See https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
            () -> new double[3], // {count, mean, M2}
            (acu, d) -> { // See chapter about Welford's online algorithm and https://math.stackexchange.com/questions/198336/how-to-calculate-standard-deviation-with-streaming-inputs
                acu[0]++; // Count
                double delta = d - acu[1];
                acu[1] += delta / acu[0]; // Mean
                acu[2] += delta * (d - acu[1]); // M2
            },
            (acuA, acuB) -> { // See chapter about "Parallel algorithm" : only called if stream is parallel ...
                double delta = acuB[1] - acuA[1];
                double count = acuA[0] + acuB[0];
                acuA[2] = acuA[2] + acuB[2] + delta * delta * acuA[0] * acuB[0] / count; // M2
                acuA[1] += delta * acuB[0] / count;  // Mean
                acuA[0] = count; // Count
                return acuA;
            },
            acu -> acu[2] / (acu[0] - 1.0), // Var = M2 / (count - 1)
            Collector.Characteristics.UNORDERED);
}
