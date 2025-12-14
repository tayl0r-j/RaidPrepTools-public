package com.taylor.restfuldash.controller;
import com.taylor.restfuldash.model.BaseModel;
import com.taylor.restfuldash.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// Controller layer that uses BaseService for handling HTTP requests
@RestController 
@RequestMapping("/api/base")
@RequiredArgsConstructor // Lombok annotation to generate constructor for final fields
public class BaseController {

    // Dependency injection of BaseService
    private final BaseService baseService;

    // Endpoint to save a model
    @PostMapping
    public ResponseEntity<BaseModel> save(@RequestBody BaseModel model) {
        BaseModel savedModel = baseService.save(model);
        return ResponseEntity.ok(savedModel);
    }

    // Endpoint to retrieve all models
    @GetMapping
    public ResponseEntity<List<BaseModel>> findAll() {
        List<BaseModel> models = baseService.findAll();
        return ResponseEntity.ok(models);
    }

    // Endpoint to find a model by ID
    @GetMapping("/{id}")
    public ResponseEntity<BaseModel> findById(@PathVariable Long id) {
        BaseModel model = baseService.findById(id);
        return ResponseEntity.ok(model);
    }

    // Endpoint to update an existing model
    @PutMapping("/{id}")
    public ResponseEntity<BaseModel> update(@PathVariable Long id, @RequestBody BaseModel model) {
        BaseModel updatedModel = baseService.update(id, model);
        return ResponseEntity.ok(updatedModel);
    }

    // Endpoint to delete a model by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        baseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}