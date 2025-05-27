package com.heartrate.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.heartrate.controller.dto.ItemFromUrlRequest;
import com.heartrate.model.Item;
import com.heartrate.repository.ItemRepository;

@Service
public class ItemService {

    private final Path fileStorageLocation;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    public ItemService() {
        this.fileStorageLocation = Paths.get("uploads/images")
                                       .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Item saveItem(Item item, MultipartFile imageFile) {
        String originalFilename = imageFile.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(imageFile.getInputStream(), targetLocation);

            // Store a relative URL or path. This is the abstraction point.
            // If using cloud storage, you would store the cloud storage URL here.
            item.setImageUrl("/uploads/images/" + fileName);

            return itemRepository.save(item);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Item saveItemFromUrl(ItemFromUrlRequest itemRequest) {
        Item item = new Item();
        item.setName(itemRequest.getName());
        item.setDescription(itemRequest.getDescription());
        item.setImageUrl(itemRequest.getImageUrl());
        return itemRepository.save(item);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(UUID id) {
        return itemRepository.findById(id);
    }

    // Future methods for fetching items, etc.
} 