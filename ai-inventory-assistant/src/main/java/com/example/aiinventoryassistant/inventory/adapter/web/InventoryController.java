package com.example.aiinventoryassistant.inventory.adapter.web;

import com.example.aiinventoryassistant.inventory.application.InventoryService;
import com.example.aiinventoryassistant.inventory.domain.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.*;

@RestController
@RequestMapping("/api")
public class InventoryController {
    private final InventoryService service;
    public InventoryController(InventoryService service) { this.service = service; }
    public record Proposal(String sku, String warehouse, long quantity, Movement.Type type) {}
    public record ProductStock(String sku, long total, List<Stock> warehouses) {}

    @GetMapping("/products")
    public List<Product> products(@RequestParam(defaultValue = "") String q) { return service.search(q); }
    @GetMapping("/products/{sku}/stock")
    public ProductStock productStock(@PathVariable String sku) {
        List<Stock> stocks = service.productStock(sku);
        long total = stocks.stream().mapToLong(Stock::quantity).reduce(0, Math::addExact);
        return new ProductStock(sku, total, stocks);
    }
    @GetMapping("/warehouses/{code}/stock")
    public List<Stock> warehouseStock(@PathVariable String code) { return service.warehouseStock(code); }
    @PostMapping("/stock-movements")
    @ResponseStatus(HttpStatus.CREATED)
    public Movement propose(@RequestBody Proposal request) {
        return service.propose(request.sku(), request.warehouse(), request.quantity(), request.type());
    }
    @PostMapping("/stock-movements/{id}/confirm")
    public Movement confirm(@PathVariable UUID id) { return service.confirm(id); }

    @GetMapping("/stock-movements/{id}")
    public Movement movement(@PathVariable UUID id) { return service.findMovement(id); }

    @GetMapping("/stock-movements")
    public List<Movement> movements() { return service.movements(); }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> missing(NoSuchElementException error) { return Map.of("message", error.getMessage()); }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalid(IllegalArgumentException error) { return Map.of("message", error.getMessage()); }
    
    @ExceptionHandler({IllegalStateException.class, ArithmeticException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflict(RuntimeException error) { return Map.of("message", error.getMessage()); }
}
