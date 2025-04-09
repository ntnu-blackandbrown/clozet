package stud.ntnu.no.backend.review.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import stud.ntnu.no.backend.review.dto.ReviewDTO;
import stud.ntnu.no.backend.review.entity.Review;

/**
 * Mapper for converting between Review entities and DTOs.
 *
 * <p>This interface uses MapStruct to generate the implementation.
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper {

  /**
   * Converts a Review entity to a ReviewDTO.
   *
   * @param review the Review entity
   * @return the ReviewDTO
   */
  @Mapping(source = "reviewee.id", target = "revieweeId")
  @Mapping(source = "reviewee.username", target = "revieweeUsername")
  @Mapping(source = "reviewer.id", target = "reviewerId")
  @Mapping(source = "reviewer.username", target = "reviewerUsername")
  @Mapping(source = "transaction.id", target = "transactionId")
  ReviewDTO toDto(Review review);

  /**
   * Converts a ReviewDTO to a Review entity.
   *
   * @param reviewDTO the ReviewDTO
   * @return the Review entity
   */
  @Mapping(target = "reviewee.id", source = "revieweeId")
  @Mapping(target = "reviewer.id", source = "reviewerId")
  @Mapping(target = "transaction.id", source = "transactionId")
  Review toEntity(ReviewDTO reviewDTO);

  /**
   * Converts a list of Review entities to a list of ReviewDTOs.
   *
   * @param reviews the list of Review entities
   * @return the list of ReviewDTOs
   */
  List<ReviewDTO> toDtoList(List<Review> reviews);
}
