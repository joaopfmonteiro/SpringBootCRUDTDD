package com.joao_monteiro.springboot_crud_tdd.controller;

import com.joao_monteiro.springboot_crud_tdd.Users;
import com.joao_monteiro.springboot_crud_tdd.repository.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersRepository usersRepository;

    private UsersController(UsersRepository usersRepository){
        this.usersRepository = usersRepository;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<Users> findById(@PathVariable Long requestedId){

        Optional<Users> optionalUsers = usersRepository.findById(requestedId);

        if (optionalUsers.isPresent()){
            return ResponseEntity.ok(optionalUsers.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping
    private ResponseEntity<Void> createUser(@RequestBody Users newUser, UriComponentsBuilder ucb){
        Users savedUser = usersRepository.save(newUser);
        URI locationOfUser = ucb
                .path("users/{id}")
                .buildAndExpand(savedUser.id())
                .toUri();
        return ResponseEntity.created(locationOfUser).build();
    }

    @GetMapping()
    private ResponseEntity<Iterable<Users>> findAll(Pageable pageable){
        Page<Users> page = usersRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "userName"))
                )
        );
        return ResponseEntity.ok(page.getContent());
    }
}