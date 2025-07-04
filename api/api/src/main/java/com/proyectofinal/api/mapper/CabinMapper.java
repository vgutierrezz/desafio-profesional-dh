package com.proyectofinal.api.mapper;

import com.proyectofinal.api.dto.*;
import com.proyectofinal.api.model.*;
import com.proyectofinal.api.repository.ICategoryRepository;
import com.proyectofinal.api.repository.IFeatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CabinMapper {

    private final IFeatureRepository featureRepository;
    private final ICategoryRepository categoryRepository;

    //DTO --> Entity
    public Cabin toEntity(CabinDTO dto) {
        //Creo la dirección
        Address addressEntity = new Address(
                dto.getAddress().getStreet(),
                dto.getAddress().getNumber(),
                dto.getAddress().getLocation(),
                dto.getAddress().getProvince(),
                dto.getAddress().getCountry()
        );

        //Busco la categoría por el nombre
        Optional<Category> categoryEntity = categoryRepository.findByNameIgnoreCase(dto.getCategory().getName());

        Cabin cabin = new Cabin();
        cabin.setName(dto.getName());
        cabin.setDescription(dto.getDescription());
        cabin.setCapacity(dto.getCapacity());
        cabin.setPrice(dto.getPrice());
        cabin.setRating(dto.getRating());
        cabin.setAddress(addressEntity);
        cabin.setCategory(categoryEntity.orElseThrow(() -> new RuntimeException("Categoría no encontrada")));

        List<Image> images = new ArrayList<>();
        if (dto.getImageFiles() != null) {
            images = dto.getImageFiles().stream()
                    .map(file -> {
                        try {
                            return new Image(file.getOriginalFilename(), file.getBytes(), cabin);
                        } catch (IOException e) {
                            throw new RuntimeException("Error al procesar imagen: " + file.getOriginalFilename(), e);
                        }
                    })
                    .collect(Collectors.toList());
        }
        cabin.setImages(images);

        // Características
        Set<Feature> features = dto.getFeatures().stream()
                .map(featureDto -> featureRepository.findByName(featureDto.getName())
                        .orElseGet(() -> featureRepository.save(new Feature(null, featureDto.getName(), new HashSet<>())))
                ).collect(Collectors.toSet());
        cabin.setFeatures(features);

        return cabin;
    }

    //Entity --> DTO
    public CabinDTO toDTO(Cabin cabin) {
        CabinDTO dto = new CabinDTO();
        dto.setId(cabin.getId());
        dto.setName(cabin.getName());
        dto.setDescription(cabin.getDescription());
        dto.setCapacity(cabin.getCapacity());
        dto.setPrice(cabin.getPrice());
        dto.setRating(cabin.getRating());
        dto.setCategory(new CategoryDTO(cabin.getCategory().getId(), cabin.getCategory().getName(), cabin.getCategory().getDescription()));

        dto.setImages(
                cabin.getImages().stream()
                        .map(ImageDTO::new)
                        .collect(Collectors.toList())
        );
        dto.setAddress(
                new AddressDTO(cabin.getAddress())
        );
        dto.setFeatures(cabin.getFeatures()
                .stream()
                .map(f -> new FeatureDTO(f.getId(), f.getName()))
                .collect(Collectors.toList())
        );
        return dto;
    }

    public Cabin updateEntityFromDto(Cabin cabin, CabinDTO dto) throws Exception {
        // Campos simples
        cabin.setName(dto.getName());
        cabin.setDescription(dto.getDescription());
        cabin.setCapacity(dto.getCapacity());
        cabin.setPrice(dto.getPrice());
        cabin.setRating(dto.getRating());

        // Dirección
        Address address = cabin.getAddress();
        if (address == null) {
            address = new Address();
        }
        address.setStreet(dto.getAddress().getStreet());
        address.setNumber(dto.getAddress().getNumber());
        address.setLocation(dto.getAddress().getLocation());
        address.setProvince(dto.getAddress().getProvince());
        address.setCountry(dto.getAddress().getCountry());
        cabin.setAddress(address);

        // Categoría
        Category category = categoryRepository.findByNameIgnoreCase(dto.getCategory().getName())
                .orElseThrow(() -> new Exception("Categoría no encontrada"));
        cabin.setCategory(category);

        // Imágenes
        List<Image> images = dto.getImagesEntities(cabin);
        cabin.getImages().clear();
        cabin.getImages().addAll(images);

        // Características
        Set<Feature> features = dto.getFeatures().stream()
                .map(featureDTO -> featureRepository.findById(featureDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Feature no encontrada con id: " + featureDTO.getId())))
                .collect(Collectors.toSet());

        cabin.setFeatures(features);

        return cabin;
    }
}
