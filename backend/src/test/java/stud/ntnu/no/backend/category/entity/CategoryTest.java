package stud.ntnu.no.backend.category.entity;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.item.entity.Item;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void testDefaultConstructor() {
        Category category = new Category();
        
        assertNull(category.getId());
        assertNull(category.getName());
        assertNull(category.getDescription());
        assertNull(category.getParent());
        assertNull(category.getCreatedAt());
        assertNull(category.getUpdatedAt());
        assertNull(category.getItems());
        assertNull(category.getSubcategories());
    }
    
    @Test
    void testGettersAndSetters() {
        Category category = new Category();
        
        Long id = 1L;
        String name = "Test Category";
        String description = "Category for testing";
        Category parent = new Category();
        parent.setId(2L);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        List<Item> items = new ArrayList<>();
        items.add(new Item());
        List<Category> subcategories = new ArrayList<>();
        subcategories.add(new Category());
        
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        category.setParent(parent);
        category.setCreatedAt(createdAt);
        category.setUpdatedAt(updatedAt);
        category.setItems(items);
        category.setSubcategories(subcategories);
        
        assertEquals(id, category.getId());
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
        assertEquals(parent, category.getParent());
        assertEquals(createdAt, category.getCreatedAt());
        assertEquals(updatedAt, category.getUpdatedAt());
        assertEquals(items, category.getItems());
        assertEquals(subcategories, category.getSubcategories());
    }
    
    @Test
    void testParentChildRelationship() {
        Category parent = new Category();
        parent.setName("Parent Category");
        
        Category child1 = new Category();
        child1.setName("Child Category 1");
        child1.setParent(parent);
        
        Category child2 = new Category();
        child2.setName("Child Category 2");
        child2.setParent(parent);
        
        List<Category> subcategories = new ArrayList<>();
        subcategories.add(child1);
        subcategories.add(child2);
        parent.setSubcategories(subcategories);
        
        assertEquals(parent, child1.getParent());
        assertEquals(parent, child2.getParent());
        assertEquals(2, parent.getSubcategories().size());
        assertTrue(parent.getSubcategories().contains(child1));
        assertTrue(parent.getSubcategories().contains(child2));
    }
    
    @Test
    void testItemsRelationship() {
        Category category = new Category();
        
        Item item1 = new Item();
        Item item2 = new Item();
        
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        
        category.setItems(items);
        
        assertEquals(2, category.getItems().size());
        assertTrue(category.getItems().contains(item1));
        assertTrue(category.getItems().contains(item2));
    }
} 