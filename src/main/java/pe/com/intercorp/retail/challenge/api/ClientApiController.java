package pe.com.intercorp.retail.challenge.api;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.com.intercorp.retail.challenge.api.dto.Client;
import pe.com.intercorp.retail.challenge.api.dto.ClientListResults;
import pe.com.intercorp.retail.challenge.api.dto.ClientStats;
import pe.com.intercorp.retail.challenge.service.ClientService;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
@RequestMapping("/clients")
@CrossOrigin(origins = {"http://localhost:4200", "https://challenge-intercorp.firebaseapp.com"}, methods = { RequestMethod.GET, RequestMethod.POST})
public class ClientApiController {
    private ClientService clientService;

    @ApiOperation(value = "Creación de cliente", response = Client.class)
    @PostMapping(consumes = "application/json", produces = "application/json")
    public CompletableFuture<ResponseEntity<Client>> create(@RequestBody @Valid Client client) {
        client.setId(null); // force the client creation and avoid unintended updates
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();

        return clientService.createOrUpdate(client)
                .thenApply((Client c) -> ResponseEntity
                        .created(builder.pathSegment("{id}").build(c.getId().toString()))
                        .body(c));
    }

    @ApiOperation(value = "Listado de clientes", response = ClientListResults.class)
    @GetMapping(produces = "application/json")
    public CompletableFuture<ResponseEntity<ClientListResults>> list(@RequestParam(defaultValue = "0") Integer page,
                                                                     @RequestParam(defaultValue = "20") Integer size) {
        return clientService.list(page*size, size)
                .thenApply(clientListResults -> ResponseEntity.ok(clientListResults));
    }

    @ApiOperation(value = "KPIs de clientes", response = ClientStats.class)
    @GetMapping(value = "/kpi", produces = "application/json")
    public CompletableFuture<ResponseEntity<ClientStats>> stats() {
        return clientService.stats()
                .thenApply(stats -> ResponseEntity.ok(stats));
    }
}
