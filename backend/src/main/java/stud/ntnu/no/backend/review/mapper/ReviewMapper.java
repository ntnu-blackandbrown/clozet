package stud.ntnu.no.backend.review.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import stud.ntnu.no.backend.review.dto.ReviewDTO;
import stud.ntnu.no.backend.review.entity.Review;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    
    @Mapping(source = "reviewee.id", target = "revieweeId")
    @Mapping(source = "reviewee.username", target = "revieweeUsername")
    @Mapping(source = "reviewer.id", target = "reviewerId")
    @Mapping(source = "reviewer.username", target = "reviewerUsername")
    @Mapping(source = "transaction.id", target = "transactionId")
    ReviewDTO toDto(Review review);
    
    @Mapping(target = "reviewee.id", source = "revieweeId")
    @Mapping(target = "reviewer.id", source = "reviewerId")
    @Mapping(target = "transaction.id", source = "transactionId")
    Review toEntity(ReviewDTO reviewDTO);
    
    List<ReviewDTO> toDtoList(List<Review> reviews);
}