package fact.it.artistservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import fact.it.artistservice.model.Artist;
import fact.it.artistservice.repository.ArtistRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ArtistControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    private Artist artist1Event1 = new Artist("Event1", "Artist1", 20, 30);
    private Artist artist2Event1 = new Artist("Event1", "Artist2", 21, 30);
    private Artist artist1Event2 = new Artist("Event2", "Artist1", 20, 30);
    private Artist artistToBeDeleted = new Artist("Event99", "Artist99", 20, 00);

    @BeforeEach
    public void beforeAllTests(){
        artistRepository.deleteAll();
        artistRepository.save(artist1Event1);
        artistRepository.save(artist2Event1);
        artistRepository.save(artist1Event2);
        artistRepository.save(artistToBeDeleted);
    }

    @AfterEach
    public void afterAllTests(){
        artistRepository.deleteAll();
    }

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void givenArtist_whenGetArtistsByEventName_thenReturnToJsonArtists() throws Exception{
        List<Artist> artistList = new ArrayList<>();
        artistList.add(artist1Event1);
        artistList.add(artist2Event1);

        mockMvc.perform(get("/artists/event/{eventName}", "Event1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].event", is("Event1")))
                .andExpect(jsonPath("$[0].artist", is("Artist1")))
                .andExpect(jsonPath("$[0].hour", is(20)))
                .andExpect(jsonPath("$[0].minute", is(30)))
                .andExpect(jsonPath("$[1].event", is("Event1")))
                .andExpect(jsonPath("$[1].artist", is("Artist2")))
                .andExpect(jsonPath("$[1].hour", is(21)))
                .andExpect(jsonPath("$[1].minute", is(30)));
    }

    @Test
    public void givenArtist_whenGetArtistsByArtist_thenReturnToJsonArtists() throws Exception{
        List<Artist> artistList = new ArrayList<>();
        artistList.add(artist1Event1);
        artistList.add(artist1Event2);

        mockMvc.perform(get("/artists/{artisttName}", "Artist1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].event", is("Event1")))
                .andExpect(jsonPath("$[0].artist", is("Artist1")))
                .andExpect(jsonPath("$[0].hour", is(20)))
                .andExpect(jsonPath("$[0].minute", is(30)))
                .andExpect(jsonPath("$[1].event", is("Event2")))
                .andExpect(jsonPath("$[1].artist", is("Artist1")))
                .andExpect(jsonPath("$[1].hour", is(20)))
                .andExpect(jsonPath("$[1].minute", is(30)));
    }

    @Test
    public void givenArtist_whenGetArtistByArtistAndEvent_thenReturnToJsonArtists() throws  Exception{
        mockMvc.perform(get("/artists/{artistName}/event/{eventName}", "Artist1", "Event1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event", is("Event1")))
                .andExpect(jsonPath("$.artist", is("Artist1")))
                .andExpect(jsonPath("$.hour", is(20)))
                .andExpect(jsonPath("$.minute", is(30)));
    }

    @Test
    public void whenPostArtist_thenReturnJsonArtist() throws Exception{
        Artist artist2Event2 = new Artist("Event2", "Artist2", 21, 30);

        mockMvc.perform(post("/artists")
                .content(mapper.writeValueAsString(artist2Event2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event", is("Event2")))
                .andExpect(jsonPath("$.artist", is("Artist2")))
                .andExpect(jsonPath("$.hour", is(21)))
                .andExpect(jsonPath("$.minute", is(30)));
    }

    @Test
    public void givenArtist_whenPutArtist_thenReturnJsonArtist() throws Exception {
        Artist updatedArtist = new Artist("Event1", "Artist1", 22, 30);

        mockMvc.perform(put("/artists")
                .content(mapper.writeValueAsString(updatedArtist))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event", is("Event1")))
                .andExpect(jsonPath("$.artist", is("Artist1")))
                .andExpect(jsonPath("$.hour", is(22)))
                .andExpect(jsonPath("$.minute", is(30)));
    }

    @Test
    public void givenArtist_whenDeleteArtist_thenStatusOk() throws Exception {
        mockMvc.perform(delete("/artists/{artistName}/event/{eventName}", "Artist99", "Event99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenNoArtist_whenDeleteArtist_thenStatusNotFound() throws Exception {
        mockMvc.perform(delete("/artists/{artistName}/event/{eventName}", "Event98", "Artist98")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
