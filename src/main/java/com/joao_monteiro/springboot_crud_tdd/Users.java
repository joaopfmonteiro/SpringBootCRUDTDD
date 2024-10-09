package com.joao_monteiro.springboot_crud_tdd;

import org.springframework.data.annotation.Id;

public record Users(@Id Long id, String userName, String email) {
}
