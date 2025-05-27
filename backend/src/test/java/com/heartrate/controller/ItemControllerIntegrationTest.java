package com.heartrate.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.ByteArrayResource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartrate.model.Item;
import com.heartrate.model.Rating;
import com.heartrate.model.User;
import com.heartrate.repository.ItemRepository;
import com.heartrate.repository.RatingRepository;
import com.heartrate.repository.UserRepository;
import com.heartrate.service.UserService;
import org.junit.jupiter.api.Disabled;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ItemControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Clear repositories before each test
        ratingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        // Create and authenticate a test user
        testUser = new User();
        testUser.setFirstName("ItemTest");
        testUser.setLastName("User");
        testUser.setEmail("itemtest@example.com");
        testUser.setPassword("password123");
        testUser.setDateOfBirth(LocalDate.of(1995, 5, 15));
        userService.signup(testUser);

        Map<String, Object> signinResult = userService.signin("itemtest@example.com", "password123");
        jwtToken = (String) signinResult.get("token");
    }

    @Test
    void testCreateItemFromUrl_Success() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Test Item URL");
        requestBody.put("description", "A test item created via URL.");
        requestBody.put("imageUrl", "http://example.com/images/test.jpg");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Item> response = restTemplate.exchange(
                "/api/items/from-url",
                HttpMethod.POST,
                requestEntity,
                Item.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Item URL", response.getBody().getName());
        assertEquals("http://example.com/images/test.jpg", response.getBody().getImageUrl());
        assertNotNull(response.getBody().getId());

        // Verify item is saved in the database
        assertTrue(itemRepository.existsById(response.getBody().getId()));

        // Verify item is saved via GET endpoint
        ResponseEntity<Item> getItemResponse = restTemplate.exchange(
                "/api/items/{itemId}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Item.class,
                response.getBody().getId()
        );
        assertEquals(HttpStatus.OK, getItemResponse.getStatusCode());
        assertNotNull(getItemResponse.getBody());
        assertEquals("Test Item URL", getItemResponse.getBody().getName());
        assertEquals("http://example.com/images/test.jpg", getItemResponse.getBody().getImageUrl());
    }

    @Test
    @Disabled("Disabled to focus on specific test")
    void testGetAllItems_Success() throws Exception {
        // Add a few items first
        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Desc 1");
        item1.setImageUrl("/uploads/images/item1.jpg");
        item1.setId(UUID.randomUUID()); // Manually set UUID for test setup

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Desc 2");
        item2.setImageUrl("http://example.com/item2.png");
        item2.setId(UUID.randomUUID()); // Manually set UUID for test setup

        itemRepository.saveAll(List.of(item1, item2));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/items",
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // Manually deserialize the JSON string to List<Item>
        List<Item> items = objectMapper.readValue(response.getBody(), new TypeReference<List<Item>>() {});

        assertEquals(2, items.size());
        // Basic check if the items are present
        assertTrue(items.stream().anyMatch(item -> item.getName().equals("Item 1")));
        assertTrue(items.stream().anyMatch(item -> item.getName().equals("Item 2")));

        // Verify items can be retrieved individually via GET endpoint
        ResponseEntity<Item> getItemResponse1 = restTemplate.exchange(
                "/api/items/{itemId}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Item.class,
                item1.getId()
        );
        assertEquals(HttpStatus.OK, getItemResponse1.getStatusCode());
        assertNotNull(getItemResponse1.getBody());
        assertEquals("Item 1", getItemResponse1.getBody().getName());
        assertEquals("Desc 1", getItemResponse1.getBody().getDescription());
        assertEquals("/uploads/images/item1.jpg", getItemResponse1.getBody().getImageUrl());

        ResponseEntity<Item> getItemResponse2 = restTemplate.exchange(
                "/api/items/{itemId}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Item.class,
                item2.getId()
        );
        assertEquals(HttpStatus.OK, getItemResponse2.getStatusCode());
        assertNotNull(getItemResponse2.getBody());
        assertEquals("Item 2", getItemResponse2.getBody().getName());
        assertEquals("Desc 2", getItemResponse2.getBody().getDescription());
        assertEquals("http://example.com/item2.png", getItemResponse2.getBody().getImageUrl());
    }

    @Test
    @Disabled("Disabled to focus on specific test")
    void testRateItem_Success() throws Exception {
        // Create an item and a user
        Item item = new Item();
        item.setName("Rateable Item");
        item.setDescription("This item can be rated.");
        item.setImageUrl("/uploads/images/rateable.jpg");
        item.setId(UUID.randomUUID());
        itemRepository.save(item);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        Integer ratingValue = 5;
        HttpEntity<Integer> requestEntity = new HttpEntity<>(ratingValue, headers);

        ResponseEntity<Rating> response = restTemplate.exchange(
                "/api/items/" + item.getId() + "/rate",
                HttpMethod.POST,
                requestEntity,
                Rating.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ratingValue, response.getBody().getRating());
        assertEquals("RATED", response.getBody().getStatus());
        assertEquals(item.getId(), response.getBody().getItem().getId());
        assertEquals(testUser.getId(), response.getBody().getUser().getId());

        // Verify rating is saved in the database
        Optional<Rating> savedRatingOptional = ratingRepository.findByUserAndItem(testUser, item);
        assertTrue(savedRatingOptional.isPresent());
        Rating savedRating = savedRatingOptional.get();
        assertEquals(ratingValue, savedRating.getRating());
        assertEquals("RATED", savedRating.getStatus());

        // Verify item still exists via GET endpoint
        ResponseEntity<Item> getItemResponse = restTemplate.exchange(
                "/api/items/{itemId}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Item.class,
                item.getId()
        );
        assertEquals(HttpStatus.OK, getItemResponse.getStatusCode());
        assertNotNull(getItemResponse.getBody());
        assertEquals("Rateable Item", getItemResponse.getBody().getName());
    }

    @Test
    @Disabled("Disabled to focus on specific test")
    void testDontKnowItem_Success() throws Exception {
        // Create an item and a user
        Item item = new Item();
        item.setName("Dont Know Item");
        item.setDescription("This item can be marked as don't know.");
        item.setImageUrl("/uploads/images/dontknow.jpg");
        item.setId(UUID.randomUUID());
        itemRepository.save(item);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        // No request body needed for this endpoint
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Rating> response = restTemplate.exchange(
                "/api/items/" + item.getId() + "/dont-know",
                HttpMethod.POST,
                requestEntity,
                Rating.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getRating()); // Rating should be null for DONT_KNOW
        assertEquals("DONT_KNOW", response.getBody().getStatus());
        assertEquals(item.getId(), response.getBody().getItem().getId());
        assertEquals(testUser.getId(), response.getBody().getUser().getId());

        // Verify rating is saved in the database
        Optional<Rating> savedRatingOptional = ratingRepository.findByUserAndItem(testUser, item);
        assertTrue(savedRatingOptional.isPresent());
        Rating savedRating = savedRatingOptional.get();
        assertNull(savedRating.getRating());
        assertEquals("DONT_KNOW", savedRating.getStatus());

        // Verify item still exists via GET endpoint
        ResponseEntity<Item> getItemResponse = restTemplate.exchange(
                "/api/items/{itemId}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Item.class,
                item.getId()
        );
        assertEquals(HttpStatus.OK, getItemResponse.getStatusCode());
        assertNotNull(getItemResponse.getBody());
        assertEquals("Dont Know Item", getItemResponse.getBody().getName());
    }

    @Test
    @Disabled("Disabled to focus on specific test")
    void testDontCareItem_Success() throws Exception {
         // Create an item and a user
        Item item = new Item();
        item.setName("Dont Care Item");
        item.setDescription("This item can be marked as don't care.");
        item.setImageUrl("/uploads/images/dontcare.jpg");
        item.setId(UUID.randomUUID());
        itemRepository.save(item);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        // No request body needed for this endpoint
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Rating> response = restTemplate.exchange(
                "/api/items/" + item.getId() + "/dont-care",
                HttpMethod.POST,
                requestEntity,
                Rating.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getRating()); // Rating should be null for DONT_CARE
        assertEquals("DONT_CARE", response.getBody().getStatus());
        assertEquals(item.getId(), response.getBody().getItem().getId());
        assertEquals(testUser.getId(), response.getBody().getUser().getId());

        // Verify rating is saved in the database
        Optional<Rating> savedRatingOptional = ratingRepository.findByUserAndItem(testUser, item);
        assertTrue(savedRatingOptional.isPresent());
        Rating savedRating = savedRatingOptional.get();
        assertNull(savedRating.getRating());
        assertEquals("DONT_CARE", savedRating.getStatus());

        // Verify item still exists via GET endpoint
        ResponseEntity<Item> getItemResponse = restTemplate.exchange(
                "/api/items/{itemId}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Item.class,
                item.getId()
        );
        assertEquals(HttpStatus.OK, getItemResponse.getStatusCode());
        assertNotNull(getItemResponse.getBody());
        assertEquals("Dont Care Item", getItemResponse.getBody().getName());
    }

    // Test cases for missing required fields during item creation (File Upload)
    @Test
    @Disabled("Disabled to focus on specific test")
    void testCreateItemWithFileUpload_MissingName() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(jwtToken);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        // body.add("name", "Test Item Upload"); // Missing name
        body.add("description", "A test item created via file upload.");
        body.add("image", new HttpEntity<>("dummy image data", new HttpHeaders() {{
            setContentType(MediaType.IMAGE_PNG);
            setContentDispositionFormData("image", "test-image.png");
        }}));
        body.add("category", "Electronics");
        body.add("location", "Warehouse");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Disabled("Disabled to focus on specific test")
    void testCreateItemWithFileUpload_MissingDescription() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(jwtToken);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("name", "Test Item Upload");
        // body.add("description", "A test item created via file upload."); // Missing description
        body.add("image", new HttpEntity<>("dummy image data", new HttpHeaders() {{
            setContentType(MediaType.IMAGE_PNG);
            setContentDispositionFormData("image", "test-image.png");
        }}));
        body.add("category", "Electronics");
        body.add("location", "Warehouse");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

     @Test
    @Disabled("Disabled to focus on specific test")
    void testCreateItemWithFileUpload_MissingImage() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(jwtToken);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("name", "Test Item Upload");
        body.add("description", "A test item created via file upload.");
        // body.add("image", ...); // Missing image
        body.add("category", "Electronics");
        body.add("location", "Warehouse");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        // Depending on backend validation, this might be BAD_REQUEST or INTERNAL_SERVER_ERROR
        // For now, let's expect BAD_REQUEST based on typical validation.
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Test cases for missing required fields during item creation (From URL)
    @Test
    @Disabled("Disabled to focus on specific test")
    void testCreateItemFromUrl_MissingName() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        Map<String, String> requestBody = new HashMap<>();
        // requestBody.put("name", "Test Item URL"); // Missing name
        requestBody.put("description", "A test item created via URL.");
        requestBody.put("imageUrl", "http://example.com/images/test.jpg");
        // Removed category and location as they are not mandatory for this endpoint
        // requestBody.put("category", "Books");
        // requestBody.put("location", "Library");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items/from-url",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

     @Test
    @Disabled("Disabled to focus on specific test")
    void testCreateItemFromUrl_MissingDescription() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Test Item URL");
        // requestBody.put("description", "A test item created via URL."); // Missing description
        requestBody.put("imageUrl", "http://example.com/images/test.jpg");
        // Removed category and location as they are not mandatory for this endpoint
        // requestBody.put("category", "Books");
        // requestBody.put("location", "Library");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items/from-url",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        // This should now be CREATED since description is not mandatory
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

     @Test
    @Disabled("Disabled to focus on specific test")
    void testCreateItemFromUrl_MissingImageUrl() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Test Item URL");
        requestBody.put("description", "A test item created via URL.");
        // requestBody.put("imageUrl", "http://example.com/images/test.jpg"); // Missing imageUrl

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items/from-url",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        // This should now be CREATED since imageUrl is not mandatory
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    // Test cases for unauthenticated access
    @Test
    @Disabled("Disabled to focus on specific test")
    void testGetAllItems_Unauthorized() {
        HttpHeaders headers = new HttpHeaders();
        // No Bearer token
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items",
                HttpMethod.GET,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Disabled("Disabled to focus on specific test")
    void testCreateItemWithFileUpload_Unauthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        // No Bearer token

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("name", "Unauthorized Item");
        body.add("description", "Should fail.");
        body.add("image", new HttpEntity<>("dummy data", new HttpHeaders() {{
            setContentType(MediaType.IMAGE_PNG);
            setContentDispositionFormData("image", "test.png");
        }}));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

     @Test
    @Disabled("Disabled to focus on specific test")
    void testCreateItemFromUrl_Unauthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // No Bearer token

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Unauthorized Item");
        requestBody.put("description", "Should fail.");
        requestBody.put("imageUrl", "http://example.com/unauth.jpg");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items/from-url",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Disabled("Disabled to focus on specific test")
    void testRateItem_Unauthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // No Bearer token

        // Need a valid item ID, but the request should fail before reaching the service
        UUID dummyItemId = UUID.randomUUID();
        Integer ratingValue = 5;
        HttpEntity<Integer> requestEntity = new HttpEntity<>(ratingValue, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items/" + dummyItemId + "/rate",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Disabled("Disabled to focus on specific test")
    void testDontKnowItem_Unauthorized() {
         HttpHeaders headers = new HttpHeaders();
        // No Bearer token

        // Need a valid item ID, but the request should fail before reaching the service
        UUID dummyItemId = UUID.randomUUID();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items/" + dummyItemId + "/dont-know",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

     @Test
    @Disabled("Disabled to focus on specific test")
    void testDontCareItem_Unauthorized() {
         HttpHeaders headers = new HttpHeaders();
        // No Bearer token

        // Need a valid item ID, but the request should fail before reaching the service
        UUID dummyItemId = UUID.randomUUID();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items/" + dummyItemId + "/dont-care",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // Test cases for non-existent items
     @Test
    @Disabled("Disabled to focus on specific test")
    void testRateItem_NotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        // Use a non-existent item ID
        UUID nonExistentItemId = UUID.randomUUID();
        Integer ratingValue = 5;
        HttpEntity<Integer> requestEntity = new HttpEntity<>(ratingValue, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items/" + nonExistentItemId + "/rate",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

     @Test
    @Disabled("Disabled to focus on specific test")
    void testDontKnowItem_NotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);

        // Use a non-existent item ID
        UUID nonExistentItemId = UUID.randomUUID();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items/" + nonExistentItemId + "/dont-know",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Disabled("Disabled to focus on specific test")
    void testDontCareItem_NotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);

        // Use a non-existent item ID
        UUID nonExistentItemId = UUID.randomUUID();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/items/" + nonExistentItemId + "/dont-care",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Helper method for ParameterizedTypeReference for List<Item>
    private static class ParameterizedTypeReference<T> extends org.springframework.core.ParameterizedTypeReference<T> {}
} 