package pe.com.intercorp.retail.challenge.api;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.com.intercorp.retail.challenge.api.dto.Client;
import pe.com.intercorp.retail.challenge.api.dto.ClientListResults;
import pe.com.intercorp.retail.challenge.service.ClientService;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
@RequestMapping("/clients")
public class ClientApiController {
    private ClientService clientService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public CompletableFuture<ResponseEntity<Client>> create(@RequestBody Client client) {
        client.setId(null); // force the client creation and avoid unintended updates
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();

        return clientService.createOrUpdate(client)
                .thenApply((Client c) -> ResponseEntity
                        .created(builder.pathSegment("{id}").build(c.getId().toString()))
                        .body(c));
    }

    @GetMapping(produces = "application/json")
    public CompletableFuture<ResponseEntity<ClientListResults>> list(@RequestParam(defaultValue = "0") Integer page,
                                                                     @RequestParam(defaultValue = "20") Integer size) {
        return clientService.list(page*size, size)
                .thenApply(clientListResults -> ResponseEntity.ok(clientListResults));
    }
}
