package stud.ntnu.no.backend.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import stud.ntnu.no.backend.review.dto.ReviewDTO;
import stud.ntnu.no.backend.review.service.ReviewService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class ReviewControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    void getAllReviews_ShouldReturnListOfReviews() throws Exception {
        // Given
        ReviewDTO review1 = new ReviewDTO();
        review1.setId(1L);
        review1.setReviewerId(101L);
        review1.setRevieweeId(201L);
        review1.setRating(5);
        review1.setComment("Great seller!");
        
        ReviewDTO review2 = new ReviewDTO();
        review2.setId(2L);
        review2.setReviewerId(102L);
        review2.setRevieweeId(202L);
        review2.setRating(4);
        review2.setComment("Good transaction");
        
        List<ReviewDTO> reviews = Arrays.asList(review1, review2);
        
        when(reviewService.getAllReviews()).thenReturn(reviews);

        // When/Then
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].rating").value(4))
                .andDo(document("reviews-get-all",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("Review ID"),
                                fieldWithPath("[].reviewerId").description("Reviewer ID"),
                                fieldWithPath("[].revieweeId").description("Reviewee ID"),
                                fieldWithPath("[].rating").description("Rating (1-5)"),
                                fieldWithPath("[].comment").description("Review comment")
                        )
                ));
                
        verify(reviewService).getAllReviews();
    }

    @Test
    void getReview_ShouldReturnReviewById() throws Exception {
        // Given
        Long reviewId = 1L;
        
        ReviewDTO review = new ReviewDTO();
        review.setId(reviewId);
        review.setReviewerId(101L);
        review.setRevieweeId(201L);
        review.setRating(5);
        review.setComment("Great seller!");
        
        when(reviewService.getReview(reviewId)).thenReturn(review);

        // When/Then
        mockMvc.perform(get("/api/reviews/" + reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Great seller!"))
                .andDo(document("review-get-by-id",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Review ID"),
                                fieldWithPath("reviewerId").description("Reviewer ID"),
                                fieldWithPath("revieweeId").description("Reviewee ID"),
                                fieldWithPath("rating").description("Rating (1-5)"),
                                fieldWithPath("comment").description("Review comment")
                        )
                ));
                
        verify(reviewService).getReview(reviewId);
    }

    @Test
    void getReviewsByReviewee_ShouldReturnReviewsForReviewee() throws Exception {
        // Given
        Long revieweeId = 201L;
        
        ReviewDTO review1 = new ReviewDTO();
        review1.setId(1L);
        review1.setReviewerId(101L);
        review1.setRevieweeId(revieweeId);
        review1.setRating(5);
        review1.setComment("Great seller!");
        
        ReviewDTO review2 = new ReviewDTO();
        review2.setId(2L);
        review2.setReviewerId(102L);
        review2.setRevieweeId(revieweeId);
        review2.setRating(4);
        review2.setComment("Good transaction");
        
        List<ReviewDTO> reviews = Arrays.asList(review1, review2);
        
        when(reviewService.getReviewsByReviewee(revieweeId)).thenReturn(reviews);

        // When/Then
        mockMvc.perform(get("/api/reviews/reviewee/" + revieweeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].revieweeId").value(revieweeId))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].revieweeId").value(revieweeId))
                .andDo(document("reviews-get-by-reviewee",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("Review ID"),
                                fieldWithPath("[].reviewerId").description("Reviewer ID"),
                                fieldWithPath("[].revieweeId").description("Reviewee ID"),
                                fieldWithPath("[].rating").description("Rating (1-5)"),
                                fieldWithPath("[].comment").description("Review comment")
                        )
                ));
                
        verify(reviewService).getReviewsByReviewee(revieweeId);
    }

    @Test
    void getReviewsByReviewer_ShouldReturnReviewsFromReviewer() throws Exception {
        // Given
        Long reviewerId = 101L;
        
        ReviewDTO review1 = new ReviewDTO();
        review1.setId(1L);
        review1.setReviewerId(reviewerId);
        review1.setRevieweeId(201L);
        review1.setRating(5);
        review1.setComment("Great seller!");
        
        ReviewDTO review2 = new ReviewDTO();
        review2.setId(2L);
        review2.setReviewerId(reviewerId);
        review2.setRevieweeId(202L);
        review2.setRating(4);
        review2.setComment("Good transaction");
        
        List<ReviewDTO> reviews = Arrays.asList(review1, review2);
        
        when(reviewService.getReviewsByReviewer(reviewerId)).thenReturn(reviews);

        // When/Then
        mockMvc.perform(get("/api/reviews/reviewer/" + reviewerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].reviewerId").value(reviewerId))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].reviewerId").value(reviewerId))
                .andDo(document("reviews-get-by-reviewer",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("Review ID"),
                                fieldWithPath("[].reviewerId").description("Reviewer ID"),
                                fieldWithPath("[].revieweeId").description("Reviewee ID"),
                                fieldWithPath("[].rating").description("Rating (1-5)"),
                                fieldWithPath("[].comment").description("Review comment")
                        )
                ));
                
        verify(reviewService).getReviewsByReviewer(reviewerId);
    }

    @Test
    void createReview_ShouldReturnCreatedReview() throws Exception {
        // Given
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewerId(101L);
        reviewDTO.setRevieweeId(201L);
        reviewDTO.setRating(5);
        reviewDTO.setComment("Great seller!");
        
        ReviewDTO createdReview = new ReviewDTO();
        createdReview.setId(1L);
        createdReview.setReviewerId(101L);
        createdReview.setRevieweeId(201L);
        createdReview.setRating(5);
        createdReview.setComment("Great seller!");
        
        when(reviewService.createReview(any(ReviewDTO.class))).thenReturn(createdReview);

        // When/Then
        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Great seller!"))
                .andDo(document("review-create",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("reviewerId").description("Reviewer ID"),
                                fieldWithPath("revieweeId").description("Reviewee ID"),
                                fieldWithPath("rating").description("Rating (1-5)"),
                                fieldWithPath("comment").description("Review comment"),
                                fieldWithPath("id").description("Review ID").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").description("Created review ID"),
                                fieldWithPath("reviewerId").description("Reviewer ID"),
                                fieldWithPath("revieweeId").description("Reviewee ID"),
                                fieldWithPath("rating").description("Rating (1-5)"),
                                fieldWithPath("comment").description("Review comment")
                        )
                ));
                
        verify(reviewService).createReview(any(ReviewDTO.class));
    }

    @Test
    void updateReview_ShouldReturnUpdatedReview() throws Exception {
        // Given
        Long reviewId = 1L;
        
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewerId(101L);
        reviewDTO.setRevieweeId(201L);
        reviewDTO.setRating(4);
        reviewDTO.setComment("Updated: Good seller!");
        
        ReviewDTO updatedReview = new ReviewDTO();
        updatedReview.setId(reviewId);
        updatedReview.setReviewerId(101L);
        updatedReview.setRevieweeId(201L);
        updatedReview.setRating(4);
        updatedReview.setComment("Updated: Good seller!");
        
        when(reviewService.updateReview(eq(reviewId), any(ReviewDTO.class))).thenReturn(updatedReview);

        // When/Then
        mockMvc.perform(put("/api/reviews/" + reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Updated: Good seller!"))
                .andDo(document("review-update",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("reviewerId").description("Reviewer ID"),
                                fieldWithPath("revieweeId").description("Reviewee ID"),
                                fieldWithPath("rating").description("Updated rating (1-5)"),
                                fieldWithPath("comment").description("Updated review comment"),
                                fieldWithPath("id").description("Review ID").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").description("Review ID"),
                                fieldWithPath("reviewerId").description("Reviewer ID"),
                                fieldWithPath("revieweeId").description("Reviewee ID"),
                                fieldWithPath("rating").description("Updated rating (1-5)"),
                                fieldWithPath("comment").description("Updated review comment")
                        )
                ));
                
        verify(reviewService).updateReview(eq(reviewId), any(ReviewDTO.class));
    }

    @Test
    void deleteReview_ShouldReturnNoContent() throws Exception {
        // Given
        Long reviewId = 1L;
        doNothing().when(reviewService).deleteReview(reviewId);

        // When/Then
        mockMvc.perform(delete("/api/reviews/" + reviewId))
                .andExpect(status().isNoContent())
                .andDo(document("review-delete",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint())
                ));
                
        verify(reviewService).deleteReview(reviewId);
    }
} 