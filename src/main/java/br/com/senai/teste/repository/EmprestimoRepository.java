package br.com.senai.teste.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.senai.teste.model.Emprestimo;
import java.time.LocalDate;


public interface EmprestimoRepository
        extends JpaRepository<Emprestimo, Integer> {

    boolean existsByLivroIdAndDataDevolucaoIsNull(Integer livroId);

    List<Emprestimo> findByDataDevolucaoIsNull();

    List<Emprestimo> findByLeitorId(Integer leitorId);

    List<Emprestimo> findByLivroId(Integer livroId);

    List<Emprestimo> findByDataPrevistaDevolucaoBeforeAndDataDevolucaoIsNull(LocalDate dataAtual);
    List<Emprestimo> findByLeitorIdAndDataPrevistaDevolucaoBeforeAndDataDevolucaoIsNull(Integer leitorId, LocalDate dataAtual);

}   
