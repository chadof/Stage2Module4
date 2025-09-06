package org.example.stage2module4.user.rest;

import com.jayway.jsonpath.JsonPath;
import org.example.stage2module4.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link UserRestController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureDataJpa
public class UserRestControllerTest {
//    @ServiceConnection
//    @Container
//    static final PostgreSQLContainer<?> postgresqlContainer =
//            new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-slpine"))
//                    .waitingFor(Wait.defaultWaitStrategy());
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void getOne() throws Exception {
        mockMvc.perform(get("/rest/users/{0}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Егор"))
                .andExpect(jsonPath("$.age").value("14"))
                .andExpect(jsonPath("$.email").value("egor@gmal.com"));
    }

    @Test
    public void getOneNotFound() throws Exception {
        mockMvc.perform(get("/rest/users/{0}", 0))
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    public void getAll() throws Exception {
        mockMvc.perform(get("/rest/users")
                        .param("nameStarts", "Ег")
                        .param("emailStarts", "")
                        .param("pageNumber", "0")
                        .param("pageSize", "0")
                        .param("sort", ""))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    public void create() throws Exception {
        String dto = """
                {
                    "name": "Егор",
                    "email": "egor@gmail.ru",
                    "age": 52
                }""";

        MvcResult mvcResult = mockMvc.perform(post("/rest/users")
                        .content(dto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        String jsonResponse = mvcResult.getResponse()
                .getContentAsString();
        Integer id = JsonPath.parse(jsonResponse)
                .read("$.id");

        assertThat(id).isNotNull();
    }

    @Test
    public void update() throws Exception {
        String dto = """
                {
                    "id": 1,
                    "name": "Джек",
                    "email": "sss@gmail.com",
                    "age": 15
                }""";

        mockMvc.perform(put("/rest/users/{0}", "1")
                        .content(dto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name").value("Джек"))
                .andExpect(jsonPath("$.email").value("sss@gmail.com"))
                .andExpect(jsonPath("$.age").value(15));
        User user = userRepository.findById(1)
                .orElseThrow();
        assertThat(user.getName()).isEqualTo("Джек");
        assertThat(user.getEmail()).isEqualTo("sss@gmail.com");
        assertThat(user.getAge()).isEqualTo(15);
    }

    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/users/{0}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Егор"))
                .andReturn();
        int size = userRepository.findAll()
                .size();

        assertThat(size).isEqualTo(4);

    }

}
