package com.example.ZZfishing.api.fish.controller;

import com.example.ZZfishing.api.fish.exception.FishNotFoundException;
import com.example.ZZfishing.api.fish.repository.entity.Fish;
import com.example.ZZfishing.api.fish.service.FishService;
import com.example.ZZfishing.model.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("junit")
@WebMvcTest({FishController.class})
class FishControllerTest {

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = -1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FishService fishService;

    private List<Fish> fishList;

    @BeforeEach
    void setUp() {
        this.fishList = new ArrayList<>();
        this.fishList.add(new Fish(5, 10, "Salmon"));
        this.fishList.add(new Fish(2, 4, "Tuna"));
        this.fishList.add(new Fish(3, 5, "Pike"));
    }

    @Test
    void canCreateMockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    void canGetAllFishes() throws Exception {
        given(fishService.getFishes()).willReturn(fishList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/fish"))
                .andExpect(status().isOk()).andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(fishList);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void canGetFishById() throws Exception {
        Fish fish = getFish(VALID_ID);

        given(fishService.fetchFishById(VALID_ID)).willReturn(fish);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/fish/{id}", VALID_ID))
                .andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(fish);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void canSaveNewFish() throws Exception {
        //object
        Fish fish = getFish(VALID_ID);

        //JSON representation
        String fishAsJson = objectMapper.writeValueAsString(fish);

        //Mocking fish
        when(fishService.addNewFish(any(Fish.class))).thenReturn(fish);

        //Calling service
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/fish")
                        .content(fishAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isCreated());

        //Verify the call to the service
        verify(fishService).addNewFish(any(Fish.class));
    }

    @Test
    void canUpdateFish() throws Exception {
        Fish fish = getFish(VALID_ID);
        String fishAsJson = objectMapper.writeValueAsString(fish);

        when(fishService.updateFish(anyLong(), any())).thenReturn(fish);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/fish/{id}", VALID_ID)
                        .content(fishAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());

        verify(fishService,times(1)).updateFish(anyLong(), any());
    }

    @Test
    void canDeleteFish() throws Exception {
        doNothing().when(fishService).deleteFish(VALID_ID);

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/fish/{id}", VALID_ID))
                .andExpect(status().isOk());

        verify(fishService, times(1)).deleteFish(VALID_ID);
    }

    private Fish getFish(long id) {
        Fish fish = new Fish();
        fish.setId(VALID_ID);
        fish.setFishSpecies("Salmon");
        fish.setWeight(5);
        fish.setLength(10);
        return fish;
    }
}