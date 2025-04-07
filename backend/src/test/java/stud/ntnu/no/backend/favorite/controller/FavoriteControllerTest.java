package stud.ntnu.no.backend.favorite.controller;

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
import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.service.FavoriteService;

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
class FavoriteControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FavoriteService favoriteService;

    @InjectMocks
    private FavoriteController favoriteController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(favoriteController)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    void getAllFavorites_ShouldReturnListOfFavorites() throws Exception {
        // Given
        FavoriteDTO favorite1 = new FavoriteDTO();
        favorite1.setId(1L);
        favorite1.setUserId("user1");
        favorite1.setItemId(101L);
        
        FavoriteDTO favorite2 = new FavoriteDTO();
        favorite2.setId(2L);
        favorite2.setUserId("user2");
        favorite2.setItemId(102L);
        
        List<FavoriteDTO> favorites = Arrays.asList(favorite1, favorite2);
        
        when(favoriteService.getAllFavorites()).thenReturn(favorites);

        // When/Then
        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value("user1"))
                .andExpect(jsonPath("$[0].itemId").value(101))
                .andDo(document("favorites-get-all",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("Favorite ID"),
                                fieldWithPath("[].userId").description("User ID"),
                                fieldWithPath("[].itemId").description("Item ID"),
                                fieldWithPath("[].active").description("Whether the favorite is active"),
                                fieldWithPath("[].createdAt").description("Date and time when favorite was created"),
                                fieldWithPath("[].updatedAt").description("Date and time when favorite was last updated")
                        )
                ));
                
        verify(favoriteService).getAllFavorites();
    }

    @Test
    void getFavoriteById_ShouldReturnFavorite() throws Exception {
        // Given
        FavoriteDTO favorite = new FavoriteDTO();
        favorite.setId(1L);
        favorite.setUserId("user1");
        favorite.setItemId(101L);
        
        when(favoriteService.getFavoriteById(1L)).thenReturn(favorite);

        // When/Then
        mockMvc.perform(get("/api/favorites/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.itemId").value(101))
                .andDo(document("favorites-get-by-id",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Favorite ID"),
                                fieldWithPath("userId").description("User ID"),
                                fieldWithPath("itemId").description("Item ID"),
                                fieldWithPath("active").description("Whether the favorite is active"),
                                fieldWithPath("createdAt").description("Date and time when favorite was created"),
                                fieldWithPath("updatedAt").description("Date and time when favorite was last updated")
                        )
                ));
                
        verify(favoriteService).getFavoriteById(1L);
    }

    @Test
    void getFavoritesByUserId_ShouldReturnUserFavorites() throws Exception {
        // Given
        FavoriteDTO favorite1 = new FavoriteDTO();
        favorite1.setId(1L);
        favorite1.setUserId("user1");
        favorite1.setItemId(101L);
        
        FavoriteDTO favorite2 = new FavoriteDTO();
        favorite2.setId(2L);
        favorite2.setUserId("user1");
        favorite2.setItemId(102L);
        
        List<FavoriteDTO> favorites = Arrays.asList(favorite1, favorite2);
        
        when(favoriteService.getFavoritesByUserId("user1")).thenReturn(favorites);

        // When/Then
        mockMvc.perform(get("/api/favorites/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value("user1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].userId").value("user1"))
                .andDo(document("favorites-get-by-user-id",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("Favorite ID"),
                                fieldWithPath("[].userId").description("User ID"),
                                fieldWithPath("[].itemId").description("Item ID"),
                                fieldWithPath("[].active").description("Whether the favorite is active"),
                                fieldWithPath("[].createdAt").description("Date and time when favorite was created"),
                                fieldWithPath("[].updatedAt").description("Date and time when favorite was last updated")
                        )
                ));
                
        verify(favoriteService).getFavoritesByUserId("user1");
    }

    @Test
    void createFavorite_ShouldReturnCreatedFavorite() throws Exception {
        // Given
        CreateFavoriteRequest request = new CreateFavoriteRequest();
        request.setUserId("user1");
        request.setItemId(101L);
        
        FavoriteDTO createdFavorite = new FavoriteDTO();
        createdFavorite.setId(1L);
        createdFavorite.setUserId("user1");
        createdFavorite.setItemId(101L);
        
        when(favoriteService.createFavorite(any(CreateFavoriteRequest.class))).thenReturn(createdFavorite);

        // When/Then
        mockMvc.perform(post("/api/favorites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.itemId").value(101))
                .andDo(document("favorites-create",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("userId").description("User ID for the favorite"),
                                fieldWithPath("itemId").description("Item ID for the favorite"),
                                fieldWithPath("active").description("Whether the favorite is active").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").description("Created favorite ID"),
                                fieldWithPath("userId").description("User ID"),
                                fieldWithPath("itemId").description("Item ID"),
                                fieldWithPath("active").description("Whether the favorite is active"),
                                fieldWithPath("createdAt").description("Date and time when favorite was created"),
                                fieldWithPath("updatedAt").description("Date and time when favorite was last updated")
                        )
                ));
                
        verify(favoriteService).createFavorite(any(CreateFavoriteRequest.class));
    }

    @Test
    void updateFavorite_ShouldReturnUpdatedFavorite() throws Exception {
        // Given
        CreateFavoriteRequest request = new CreateFavoriteRequest();
        request.setUserId("user1");
        request.setItemId(102L);
        
        FavoriteDTO updatedFavorite = new FavoriteDTO();
        updatedFavorite.setId(1L);
        updatedFavorite.setUserId("user1");
        updatedFavorite.setItemId(102L);
        
        when(favoriteService.updateFavorite(eq(1L), any(CreateFavoriteRequest.class))).thenReturn(updatedFavorite);

        // When/Then
        mockMvc.perform(put("/api/favorites/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.itemId").value(102))
                .andDo(document("favorites-update",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("userId").description("User ID for the favorite"),
                                fieldWithPath("itemId").description("Updated item ID for the favorite"),
                                fieldWithPath("active").description("Whether the favorite is active").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").description("Favorite ID"),
                                fieldWithPath("userId").description("User ID"),
                                fieldWithPath("itemId").description("Updated item ID"),
                                fieldWithPath("active").description("Whether the favorite is active"),
                                fieldWithPath("createdAt").description("Date and time when favorite was created"),
                                fieldWithPath("updatedAt").description("Date and time when favorite was last updated")
                        )
                ));
                
        verify(favoriteService).updateFavorite(eq(1L), any(CreateFavoriteRequest.class));
    }

    @Test
    void deleteFavorite_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(favoriteService).deleteFavorite(1L);

        // When/Then
        mockMvc.perform(delete("/api/favorites/1"))
                .andExpect(status().isNoContent())
                .andDo(document("favorites-delete",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint())
                ));
                
        verify(favoriteService).deleteFavorite(1L);
    }
} 