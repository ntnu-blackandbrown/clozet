package stud.ntnu.no.backend.item.entity;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;
import stud.ntnu.no.backend.location.entity.Location;
import stud.ntnu.no.backend.message.entity.Message;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;
import stud.ntnu.no.backend.transaction.entity.Transaction;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testDefaultConstructor() {
        Item item = new Item();
        
        assertNull(item.getId());
        assertNull(item.getSeller());
        assertNull(item.getCategory());
        assertNull(item.getLocation());
        assertNull(item.getShippingOption());
        assertNull(item.getTitle());
        assertNull(item.getShortDescription());
        assertNull(item.getLongDescription());
        assertEquals(0.0, item.getPrice());
        assertEquals(0.0, item.getLatitude());
        assertEquals(0.0, item.getLongitude());
        assertNull(item.getCondition());
        assertNull(item.getSize());
        assertNull(item.getBrand());
        assertNull(item.getColor());
        assertFalse(item.isAvailable());
        assertFalse(item.isVippsPaymentEnabled());
        assertNull(item.getCreatedAt());
        assertNull(item.getUpdatedAt());
        assertNotNull(item.getFavorites());
        assertTrue(item.getFavorites().isEmpty());
        assertNull(item.getImages());
        assertNull(item.getTransactions());
        assertNull(item.getMessages());
    }
    
    @Test
    void testGettersAndSetters() {
        Item item = new Item();
        
        Long id = 1L;
        User seller = new User();
        Category category = new Category();
        Location location = new Location();
        ShippingOption shippingOption = new ShippingOption();
        String title = "Test Item";
        String shortDescription = "Short test description";
        String longDescription = "Long test description with details";
        double price = 199.99;
        double latitude = 59.9139;
        double longitude = 10.7522;
        String condition = "New";
        String size = "M";
        String brand = "Test Brand";
        String color = "Red";
        boolean isAvailable = true;
        boolean isVippsPaymentEnabled = true;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        Set<Favorite> favorites = new HashSet<>();
        List<ItemImage> images = new ArrayList<>();
        List<Transaction> transactions = new ArrayList<>();
        List<Message> messages = new ArrayList<>();
        
        item.setId(id);
        item.setSeller(seller);
        item.setCategory(category);
        item.setLocation(location);
        item.setShippingOption(shippingOption);
        item.setTitle(title);
        item.setShortDescription(shortDescription);
        item.setLongDescription(longDescription);
        item.setPrice(price);
        item.setLatitude(latitude);
        item.setLongitude(longitude);
        item.setCondition(condition);
        item.setSize(size);
        item.setBrand(brand);
        item.setColor(color);
        item.setAvailable(isAvailable);
        item.setVippsPaymentEnabled(isVippsPaymentEnabled);
        item.setCreatedAt(createdAt);
        item.setUpdatedAt(updatedAt);
        item.setFavorites(favorites);
        item.setImages(images);
        item.setTransactions(transactions);
        item.setMessages(messages);
        
        assertEquals(id, item.getId());
        assertEquals(seller, item.getSeller());
        assertEquals(category, item.getCategory());
        assertEquals(location, item.getLocation());
        assertEquals(shippingOption, item.getShippingOption());
        assertEquals(title, item.getTitle());
        assertEquals(shortDescription, item.getShortDescription());
        assertEquals(longDescription, item.getLongDescription());
        assertEquals(price, item.getPrice());
        assertEquals(latitude, item.getLatitude());
        assertEquals(longitude, item.getLongitude());
        assertEquals(condition, item.getCondition());
        assertEquals(size, item.getSize());
        assertEquals(brand, item.getBrand());
        assertEquals(color, item.getColor());
        assertTrue(item.isAvailable());
        assertTrue(item.isVippsPaymentEnabled());
        assertEquals(createdAt, item.getCreatedAt());
        assertEquals(updatedAt, item.getUpdatedAt());
        assertEquals(favorites, item.getFavorites());
        assertEquals(images, item.getImages());
        assertEquals(transactions, item.getTransactions());
        assertEquals(messages, item.getMessages());
    }
    
    @Test
    void testFavoritesRelationship() {
        Item item = new Item();
        Set<Favorite> favorites = new HashSet<>();
        
        Favorite favorite1 = new Favorite();
        favorite1.setId(1L);
        favorite1.setItem(item);
        
        Favorite favorite2 = new Favorite();
        favorite2.setId(2L);
        favorite2.setItem(item);
        
        favorites.add(favorite1);
        favorites.add(favorite2);
        
        item.setFavorites(favorites);
        
        assertEquals(2, item.getFavorites().size());
        assertTrue(item.getFavorites().contains(favorite1));
        assertTrue(item.getFavorites().contains(favorite2));
    }
    
    @Test
    void testImagesRelationship() {
        Item item = new Item();
        List<ItemImage> images = new ArrayList<>();
        
        ItemImage image1 = new ItemImage();
        image1.setId(1L);
        image1.setItem(item);
        
        ItemImage image2 = new ItemImage();
        image2.setId(2L);
        image2.setItem(item);
        
        images.add(image1);
        images.add(image2);
        
        item.setImages(images);
        
        assertEquals(2, item.getImages().size());
        assertEquals(image1, item.getImages().get(0));
        assertEquals(image2, item.getImages().get(1));
    }
} 