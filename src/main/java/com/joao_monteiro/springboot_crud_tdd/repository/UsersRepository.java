package com.joao_monteiro.springboot_crud_tdd.repository;

import com.joao_monteiro.springboot_crud_tdd.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UsersRepository extends CrudRepository<Users, Long>, PagingAndSortingRepository<Users, Long> {
}
