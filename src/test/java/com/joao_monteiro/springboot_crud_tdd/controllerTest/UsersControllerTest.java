package com.joao_monteiro.springboot_crud_tdd.controllerTest;


import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.joao_monteiro.springboot_crud_tdd.Users;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UsersControllerTest {

    @Autowired
    TestRestTemplate testRestTemplate;
    @Test
    void shouldReturnAUserWhenIsSaved(){
        ResponseEntity<String> response = testRestTemplate
                .withBasicAuth("Luis", "abc123")
                .getForEntity("/users/2", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        String userName = documentContext.read("$.userName");
        String email = documentContext.read("$.email");
        assertThat(id).isEqualTo(2);
        assertThat(userName).isEqualTo("Joao");
        assertThat(email).isEqualTo("email@email.com");
    }

    @Test
    void shouldNotReturnWhenIsSaved(){
        ResponseEntity<String> response = testRestTemplate.getForEntity("/users/7", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    @DirtiesContext
    void shouldCreateAUser(){
        Users users = new Users(null, "Marta", "email@email.com");
        ResponseEntity<Void> createResponse = testRestTemplate.postForEntity("/users", users, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewUser = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = testRestTemplate.getForEntity(locationOfNewUser, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String userName = documentContext.read("$.userName");
        String email = documentContext.read("$.email");

        assertThat(id).isNotNull();
        assertThat(userName).isEqualTo("Marta");
        assertThat(email).isEqualTo("email@email.com");
    }

    @Test
    void shouldReturnAllUsers(){
        ResponseEntity<String> response = testRestTemplate.getForEntity("/users", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int usersCount = documentContext.read("$.length()");
        assertThat(usersCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(2,3,4);

        JSONArray userNames = documentContext.read(("$..userName"));
        assertThat(userNames).containsExactlyInAnyOrder("Joao", "Marta", "Pedro");

        JSONArray names = documentContext.read("$..email");
        assertThat(names).containsExactlyInAnyOrder("email@email.com", "marta@email.com", "pedro@email.com");

    }

    @Test
    void shouldReturnAPageOfUsers(){
        ResponseEntity<String> response = testRestTemplate.getForEntity("/users?page=0&size=1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnASortedPageOfUsers(){
        ResponseEntity<String> response = testRestTemplate.getForEntity("/users?page=0&size=1&sort=userName,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray read = documentContext.read("$[*]");
        assertThat(read.size()).isEqualTo(1);

        String name = documentContext.read("$[0].userName");
        assertThat(name).isEqualTo("Pedro");
    }

    @Test
    void shouldReturnASortedPageOfUsersWithNoParametersAndUseDefaultValues(){
        ResponseEntity<String> response = testRestTemplate.getForEntity("/users", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);

        JSONArray userName = documentContext.read("$..userName");
        assertThat(userName).containsExactly("Joao", "Marta", "Pedro");
    }


}
