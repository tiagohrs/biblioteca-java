package br.com.senai.teste.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.senai.teste.model.Leitor;

public interface LeitorRepository extends JpaRepository<Leitor, Integer> {

}