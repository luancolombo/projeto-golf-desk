package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.ReceiptItemControllerDocs;
import com.project.golfofficeapi.dto.ReceiptItemDTO;
import com.project.golfofficeapi.services.ReceiptItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/receipt-item")
@Tag(name = "Receipt Items", description = "Endpoints for receipt line items")
public class ReceiptItemController implements ReceiptItemControllerDocs {

    private final ReceiptItemService service;

    public ReceiptItemController(ReceiptItemService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public List<ReceiptItemDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ReceiptItemDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @GetMapping(value = "/receipt/{receiptId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public List<ReceiptItemDTO> findByReceiptId(@PathVariable("receiptId") Long receiptId) {
        return service.findByReceiptId(receiptId);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ReceiptItemDTO create(@Valid @RequestBody ReceiptItemDTO receiptItem) {
        return service.create(receiptItem);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ReceiptItemDTO update(@Valid @RequestBody ReceiptItemDTO receiptItem) {
        return service.update(receiptItem);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
