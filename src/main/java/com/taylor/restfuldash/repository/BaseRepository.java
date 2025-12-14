package com.taylor.restfuldash.repository;

import com.taylor.restfuldash.model.BaseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseRepository extends JpaRepository<BaseModel, Long> {
    // JpaRepository provides all CRUD methods automatically:
    // save(), findAll(), findById(), deleteById(), etc.
    // Add custom query methods here if needed
}