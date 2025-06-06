package com.proyectofinal.api.service.impl;

import com.proyectofinal.api.dto.CabinDTO;
import com.proyectofinal.api.model.Address;
import com.proyectofinal.api.model.Cabin;
import com.proyectofinal.api.model.Category;
import com.proyectofinal.api.repository.ICabinRepository;
import com.proyectofinal.api.repository.ICategoryRepository;
import com.proyectofinal.api.service.ICabinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CabinService  implements ICabinService {

    private ICabinRepository cabinRepository;
    private ICategoryRepository categoryRepository;

    @Autowired
    public CabinService(ICabinRepository cabinRepository, ICategoryRepository categoryRepository) {
        this.cabinRepository = cabinRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CabinDTO save(CabinDTO cabinDTO) {
        //Mapear las entidades a mano
        //Creo la dirección
        Address addressEntity = new Address(
                cabinDTO.getAddress().getStreet(),
                cabinDTO.getAddress().getNumber(),
                cabinDTO.getAddress().getLocation(),
                cabinDTO.getAddress().getProvince(),
                cabinDTO.getAddress().getCountry()
        );
        //Busco la categoría por el nombre
        Optional<Category> categoryEntity = categoryRepository.findByNameIgnoreCase(cabinDTO.getCategoryName());

        //Creo cada ImgCabin

        //Creo la cabaña y le asigno la dirección
        Cabin cabinEntity = new Cabin(
                cabinDTO.getName(),
                cabinDTO.getDescription(),
                cabinDTO.getImage(),
                cabinDTO.getCapacity(),
                cabinDTO.getPrice(),
                addressEntity,
                categoryEntity.get()
        );

        //Persisto mi entidad Cabaña en la base de datos
        cabinRepository.save(cabinEntity);

        //Creo el DTO para devolver al cliente
        CabinDTO cabinDTOResponse = new CabinDTO(
                cabinEntity.getId(),
                cabinEntity.getName(),
                cabinEntity.getDescription(),
                cabinEntity.getImage(),
                cabinEntity.getCapacity(),
                cabinEntity.getPrice(),
                cabinEntity.getAddress().getId(),
                cabinEntity.getAddress().getStreet(),
                cabinEntity.getAddress().getNumber(),
                cabinEntity.getAddress().getLocation(),
                cabinEntity.getAddress().getProvince(),
                cabinEntity.getAddress().getCountry(),
                cabinEntity.getCategory().getName()
        );
        return cabinDTOResponse;
    }

    @Override
    public Optional<CabinDTO> findById(Long id) {
        //Busco la cabaña
        Optional<Cabin> cabinWanted =  cabinRepository.findById(id);

        //Creo un Optional para devolverlo al cliente
        Optional<CabinDTO> cabinDTOResponse = null;

        if(cabinWanted.isPresent()){
            //Creo el DTO para devolver al cliente
            CabinDTO cabinDTO = new CabinDTO(
                    cabinWanted.get().getId(),
                    cabinWanted.get().getName(),
                    cabinWanted.get().getDescription(),
                    cabinWanted.get().getImage(),
                    cabinWanted.get().getCapacity(),
                    cabinWanted.get().getPrice(),
                    cabinWanted.get().getAddress().getId(),
                    cabinWanted.get().getAddress().getStreet(),
                    cabinWanted.get().getAddress().getNumber(),
                    cabinWanted.get().getAddress().getLocation(),
                    cabinWanted.get().getAddress().getProvince(),
                    cabinWanted.get().getAddress().getCountry(),
                    cabinWanted.get().getCategory().getName()
            );
            //Convierto el DTO a Optional para devolverlo al cliente
            cabinDTOResponse =  Optional.of(cabinDTO);
        }
        return cabinDTOResponse;
    }

    @Override
    public CabinDTO update(CabinDTO cabinDTO) throws Exception{

        if((cabinRepository.findById(cabinDTO.getId()).isPresent())){
           Optional<Cabin> cabinEntity = cabinRepository.findById(cabinDTO.getId());
            //Busco la dirección asociada a la cabaña
            Address addressEntity = cabinEntity.get().getAddress();
            //seteo los nuevos valores de la dirección
            addressEntity.setStreet(cabinDTO.getAddress().getStreet());
            addressEntity.setNumber(cabinDTO.getAddress().getNumber());
            addressEntity.setLocation(cabinDTO.getAddress().getLocation());
            addressEntity.setProvince(cabinDTO.getAddress().getProvince());
            addressEntity.setCountry(cabinDTO.getAddress().getCountry());

            Optional<Category> categoryEntity = categoryRepository.findByNameIgnoreCase(cabinDTO.getCategoryName());

            //Seteo la cabaña con los nuevos valores
            cabinEntity.get().setName(cabinDTO.getName());
            cabinEntity.get().setDescription(cabinDTO.getDescription());
            cabinEntity.get().setImage(cabinDTO.getImage());
            cabinEntity.get().setCapacity(cabinDTO.getCapacity());
            cabinEntity.get().setPrice(cabinDTO.getPrice());
            cabinEntity.get().setAddress(addressEntity);
            cabinEntity.get().setCategory(categoryEntity.get());

            //Persisto mi entidad Cabaña en la base de datos
            cabinRepository.save(cabinEntity.get());

            //Creo el DTO para devolver al cliente
            CabinDTO cabinDTOResponse = new CabinDTO(
                    cabinEntity.get().getId(),
                    cabinEntity.get().getName(),
                    cabinEntity.get().getDescription(),
                    cabinEntity.get().getImage(),
                    cabinEntity.get().getCapacity(),
                    cabinEntity.get().getPrice(),
                    cabinEntity.get().getAddress().getId(),
                    cabinEntity.get().getAddress().getStreet(),
                    cabinEntity.get().getAddress().getNumber(),
                    cabinEntity.get().getAddress().getLocation(),
                    cabinEntity.get().getAddress().getProvince(),
                    cabinEntity.get().getAddress().getCountry(),
                    cabinEntity.get().getCategory().getName()
            );
            return cabinDTOResponse;
        } else {
            throw new Exception("La cabaña que quiere actualizar no existe");
        }

    }

    @Override
    public void deleteById(Long id) throws RuntimeException{
        Optional<CabinDTO> cabinWanted = findById(id);
        if(cabinWanted.isPresent()){
            cabinRepository.deleteById(id);
        }else {
            throw new RuntimeException("Cabaña no encontrada");
        }
    }

    @Override
    public List<CabinDTO> findAll() {
        //Traer todas las cabañas de la base de datos
        List<Cabin> cabinList = cabinRepository.findAll();

        //Creo la lista para devolver
        List<CabinDTO> cabinDTOList = new ArrayList<>();

        //Recorro la lista obtenido
        for (Cabin cabin : cabinList) {
            //Mapeo a DTO
            CabinDTO cabinDTO = new CabinDTO(
                    cabin.getId(),
                    cabin.getName(),
                    cabin.getDescription(),
                    cabin.getImage(),
                    cabin.getCapacity(),
                    cabin.getPrice(),
                    cabin.getAddress().getId(),
                    cabin.getAddress().getStreet(),
                    cabin.getAddress().getNumber(),
                    cabin.getAddress().getLocation(),
                    cabin.getAddress().getProvince(),
                    cabin.getAddress().getCountry(),
                    cabin.getCategory().getName()
            );
            //Agrego a la lista de DTOs
            cabinDTOList.add(cabinDTO);
        }
        return cabinDTOList;
    }

    @Override
    public List<CabinDTO> findByCategoryName(String categoryName) {
        //Traer todas las cabañas de la base de datos
        List<Cabin> cabinList = cabinRepository.findByCategoryName(categoryName);

        //Creo la lista para devolver
        List<CabinDTO> cabinDTOList = new ArrayList<>();

        //Recorro la lista obtenido
        for (Cabin cabin : cabinList) {
            //Mapeo a DTO
            CabinDTO cabinDTO = new CabinDTO(
                    cabin.getId(),
                    cabin.getName(),
                    cabin.getDescription(),
                    cabin.getImage(),
                    cabin.getCapacity(),
                    cabin.getPrice(),
                    cabin.getAddress().getId(),
                    cabin.getAddress().getStreet(),
                    cabin.getAddress().getNumber(),
                    cabin.getAddress().getLocation(),
                    cabin.getAddress().getProvince(),
                    cabin.getAddress().getCountry(),
                    cabin.getCategory().getName()
            );
            //Agrego a la lista de DTOs
            cabinDTOList.add(cabinDTO);
        }
        return cabinDTOList;
    }
}
