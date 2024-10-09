package com.joao_monteiro.springboot_crud_tdd;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserJsonTest {

    @Autowired
    private JacksonTester<Users> json;

    @Autowired
    private JacksonTester<Users[]> jsonList;

    private Users[] users;

    @BeforeEach
    void setUp(){
        users = Arrays.array(
                new Users(2L, "Joao", "email@email.com"),
                new Users(3L,"Marta", "marta@email.com"),
                new Users(4L, "Pedro","pedro@email.com")
        );
    }

    @Test
    void userSerializationTest() throws IOException {
        Users user = new Users(2L, "Joao", "email@email.com");
        assertThat(json.write(user)).isStrictlyEqualToJson(new ClassPathResource("singleUser.json"));
        assertThat(json.write(user)).hasJsonPathValue("@.id");
        assertThat(json.write(user)).hasJsonPathValue("@.userName");
        assertThat(json.write(user)).hasJsonPathValue("@.email");
        assertThat(json.write(user)).extractingJsonPathNumberValue("@.id").isEqualTo(2);
        assertThat(json.write(user)).extractingJsonPathStringValue("@.userName").isEqualTo("Joao");
        assertThat(json.write(user)).extractingJsonPathStringValue("@.email").isEqualTo("email@email.com");
    }

    @Test
    void userDeserializationTest() throws  IOException{
        String expected = """
                {
                    "id": 2,
                    "userName": "Joao",
                    "email": "email@email.com"
                }
                """;
        assertThat(json.parse(expected)).isEqualTo(new Users(2L, "Joao", "email@email.com"));
        assertThat(json.parseObject(expected).id()).isEqualTo(2l);
        assertThat(json.parseObject(expected).userName()).isEqualTo("Joao");
        assertThat(json.parseObject(expected).email()).isEqualTo("email@email.com");
    }

    @Test
    void usersListSerialization() throws IOException{
        assertThat(jsonList.write(users)).isStrictlyEqualToJson(new ClassPathResource("listUsers.json"));
    }

    @Test
    void usersListDeserialization() throws IOException{
        String expected = """
                    [
                        {"id": 2, "userName": "Joao", "email": "email@email.com"},
                        {"id": 3, "userName": "Marta", "email": "marta@email.com"},
                        {"id": 4, "userName": "Pedro", "email": "pedro@email.com"}
                    ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(users);
    }

}
