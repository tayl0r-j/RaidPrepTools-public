package com.taylor.restfuldash.service;
import com.taylor.restfuldash.model.BaseModel;
import com.taylor.restfuldash.repository.BaseRepository;
import com.taylor.restfuldash.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

// Service layer that uses BaseRepository for data operations 
@Service
@RequiredArgsConstructor
public class BaseService {

    // Dependency injection of BaseRepository
    private final BaseRepository baseRepository;

    // Base service methods and properties
    // Save or update a model
    public BaseModel save(BaseModel model) {
        return baseRepository.save(model);
    }

    // Retrieve all models
    public List<BaseModel> findAll() {
        return baseRepository.findAll();
    }

    public BaseModel findById(Long id) {
        BaseModel model = baseRepository.findById(id).orElse(null);
        if (model == null) {
            throw new NotFoundException("BaseModel not found with id " + id);
        }
        return model;
    }

    // Update an existing model
    public BaseModel update(Long id, BaseModel updatedModel) {
        BaseModel existingModel = findById(id); // Throws NotFoundException if not found
        existingModel.setName(updatedModel.getName());
        existingModel.setDescription(updatedModel.getDescription());
        existingModel.setUpdatedAt(java.time.LocalDateTime.now());
        return baseRepository.save(existingModel);
    }

    // Delete a model by ID
    public void deleteById(Long id) {
        findById(id); // Throws NotFoundException if not found
        baseRepository.deleteById(id);
    }

}

