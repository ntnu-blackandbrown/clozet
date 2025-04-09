package stud.ntnu.no.backend.item.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stud.ntnu.no.backend.category.repository.CategoryRepository;
import stud.ntnu.no.backend.item.mapper.ItemMapper;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.location.repository.LocationRepository;
import stud.ntnu.no.backend.shippingoption.repository.ShippingOptionRepository;
import stud.ntnu.no.backend.user.repository.UserRepository;

@Configuration
public class ItemServiceConfig {

  @Bean
  public ItemService itemService(
      ItemRepository itemRepository,
      CategoryRepository categoryRepository,
      UserRepository userRepository,
      LocationRepository locationRepository,
      ShippingOptionRepository shippingOptionRepository,
      ItemMapper itemMapper) {
    return new ItemServiceImpl(
        itemRepository,
        categoryRepository,
        userRepository,
        locationRepository,
        shippingOptionRepository,
        itemMapper);
  }
}
