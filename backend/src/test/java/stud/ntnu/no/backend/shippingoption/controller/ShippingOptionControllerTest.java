package stud.ntnu.no.backend.shippingoption.controller;

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
import stud.ntnu.no.backend.shippingoption.dto.CreateShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.dto.ShippingOptionDTO;
import stud.ntnu.no.backend.shippingoption.service.ShippingOptionService;

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
class ShippingOptionControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ShippingOptionService shippingOptionService;

    @InjectMocks
    private ShippingOptionController shippingOptionController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(shippingOptionController)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    void getAllShippingOptions_ShouldReturnListOfShippingOptions() throws Exception {
        // Given
        ShippingOptionDTO option1 = new ShippingOptionDTO();
        option1.setId(1L);
        option1.setName("Standard Shipping");
        
        ShippingOptionDTO option2 = new ShippingOptionDTO();
        option2.setId(2L);
        option2.setName("Express Shipping");
        
        List<ShippingOptionDTO> options = Arrays.asList(option1, option2);
        
        when(shippingOptionService.getAllShippingOptions()).thenReturn(options);

        // When/Then
        mockMvc.perform(get("/api/shipping-options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Standard Shipping"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Express Shipping"))
                .andDo(document("shipping-options-get-all",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("Shipping option ID"),
                                fieldWithPath("[].name").description("Shipping option name"),
                                fieldWithPath("[].description").description("Shipping option description"),
                                fieldWithPath("[].estimatedDays").description("Estimated delivery days"),
                                fieldWithPath("[].price").description("Shipping price"),
                                fieldWithPath("[].tracked").description("Whether the shipping is tracked")
                        )
                ));
                
        verify(shippingOptionService).getAllShippingOptions();
    }

    @Test
    void getShippingOption_ShouldReturnShippingOptionById() throws Exception {
        // Given
        Long optionId = 1L;
        
        ShippingOptionDTO option = new ShippingOptionDTO();
        option.setId(optionId);
        option.setName("Standard Shipping");
        
        when(shippingOptionService.getShippingOption(optionId)).thenReturn(option);

        // When/Then
        mockMvc.perform(get("/api/shipping-options/" + optionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Standard Shipping"))
                .andDo(document("shipping-option-get-by-id",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Shipping option ID"),
                                fieldWithPath("name").description("Shipping option name"),
                                fieldWithPath("description").description("Shipping option description"),
                                fieldWithPath("estimatedDays").description("Estimated delivery days"),
                                fieldWithPath("price").description("Shipping price"),
                                fieldWithPath("tracked").description("Whether the shipping is tracked")
                        )
                ));
                
        verify(shippingOptionService).getShippingOption(optionId);
    }

    @Test
    void createShippingOption_ShouldReturnCreatedShippingOption() throws Exception {
        // Given
        CreateShippingOptionDTO createDTO = new CreateShippingOptionDTO();
        createDTO.setName("Premium Shipping");
        
        ShippingOptionDTO createdOption = new ShippingOptionDTO();
        createdOption.setId(3L);
        createdOption.setName("Premium Shipping");
        
        when(shippingOptionService.createShippingOption(any(CreateShippingOptionDTO.class))).thenReturn(createdOption);

        // When/Then
        mockMvc.perform(post("/api/shipping-options")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Premium Shipping"))
                .andDo(document("shipping-option-create",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("Shipping option name"),
                                fieldWithPath("description").description("Shipping option description"),
                                fieldWithPath("estimatedDays").description("Estimated delivery days"),
                                fieldWithPath("price").description("Shipping price"),
                                fieldWithPath("tracked").description("Whether the shipping is tracked")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Created shipping option ID"),
                                fieldWithPath("name").description("Shipping option name"),
                                fieldWithPath("description").description("Shipping option description"),
                                fieldWithPath("estimatedDays").description("Estimated delivery days"),
                                fieldWithPath("price").description("Shipping price"),
                                fieldWithPath("tracked").description("Whether the shipping is tracked")
                        )
                ));
                
        verify(shippingOptionService).createShippingOption(any(CreateShippingOptionDTO.class));
    }

    @Test
    void updateShippingOption_ShouldReturnUpdatedShippingOption() throws Exception {
        // Given
        Long optionId = 1L;
        
        CreateShippingOptionDTO updateDTO = new CreateShippingOptionDTO();
        updateDTO.setName("Updated Standard Shipping");
        
        ShippingOptionDTO updatedOption = new ShippingOptionDTO();
        updatedOption.setId(optionId);
        updatedOption.setName("Updated Standard Shipping");
        
        when(shippingOptionService.updateShippingOption(eq(optionId), any(CreateShippingOptionDTO.class)))
                .thenReturn(updatedOption);

        // When/Then
        mockMvc.perform(put("/api/shipping-options/" + optionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Standard Shipping"))
                .andDo(document("shipping-option-update",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("Updated shipping option name"),
                                fieldWithPath("description").description("Shipping option description"),
                                fieldWithPath("estimatedDays").description("Estimated delivery days"),
                                fieldWithPath("price").description("Shipping price"),
                                fieldWithPath("tracked").description("Whether the shipping is tracked")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Shipping option ID"),
                                fieldWithPath("name").description("Updated shipping option name"),
                                fieldWithPath("description").description("Shipping option description"),
                                fieldWithPath("estimatedDays").description("Estimated delivery days"),
                                fieldWithPath("price").description("Shipping price"),
                                fieldWithPath("tracked").description("Whether the shipping is tracked")
                        )
                ));
                
        verify(shippingOptionService).updateShippingOption(eq(optionId), any(CreateShippingOptionDTO.class));
    }

    @Test
    void deleteShippingOption_ShouldReturnNoContent() throws Exception {
        // Given
        Long optionId = 1L;
        doNothing().when(shippingOptionService).deleteShippingOption(optionId);

        // When/Then
        mockMvc.perform(delete("/api/shipping-options/" + optionId))
                .andExpect(status().isNoContent())
                .andDo(document("shipping-option-delete",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint())
                ));
                
        verify(shippingOptionService).deleteShippingOption(optionId);
    }
} 