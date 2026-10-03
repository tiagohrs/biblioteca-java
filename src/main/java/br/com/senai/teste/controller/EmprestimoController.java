package br.com.senai.teste.controller;

import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;

import br.com.senai.teste.dto.EmprestimoRequest;
import br.com.senai.teste.dto.RenovacaoRequest;
import br.com.senai.teste.model.Emprestimo;
import br.com.senai.teste.service.EmprestimoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @PostMapping
    public ResponseEntity<Emprestimo> cadastrar(
            @Valid @RequestBody EmprestimoRequest dados) {

        Integer leitorId = dados.getLeitorId();
        Integer livroId = dados.getLivroId();
        LocalDate dataPrevistaDevolucao = dados.getDataPrevistaDevolucao();

        Optional<Emprestimo> emprestimo = emprestimoService.cadastrar(
                leitorId,
                livroId,
                dataPrevistaDevolucao);

        if (emprestimo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(emprestimo.get());
    }

    @GetMapping
    public ResponseEntity<List<Emprestimo>> listar() {
        List<Emprestimo> emprestimos = emprestimoService.listar();
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Emprestimo> buscarPorId(@PathVariable Integer id) {
        Optional<Emprestimo> emprestimo = emprestimoService.buscarPorId(id);
        if (emprestimo.isPresent()) {
            return ResponseEntity.ok(emprestimo.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/devolucao")
    public ResponseEntity<Emprestimo> devolver(@PathVariable Integer id) {
        Optional<Emprestimo> emprestimo = emprestimoService.devolver(id);

        if (emprestimo.isPresent()) {
            return ResponseEntity.ok(emprestimo.get());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Emprestimo>> listarAtivos() {
        List<Emprestimo> emprestimos = emprestimoService.listarAtivos();
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/leitor/{leitorId}")
    public ResponseEntity<List<Emprestimo>> listarPorLeitor(@PathVariable Integer leitorId) {
        List<Emprestimo> emprestimos = emprestimoService.listarPorLeitor(leitorId);
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/livro/{livroId}")
    public ResponseEntity<List<Emprestimo>> listarPorLivro(@PathVariable Integer livroId) {
        List<Emprestimo> emprestimos = emprestimoService.listarPorLivro(livroId);
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/atrasados")
    public ResponseEntity<List<Emprestimo>> listarAtrasados() {
        List<Emprestimo> emprestimos = emprestimoService.listarAtrasados();
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/leitor/{leitorId}/atrasados")
    public ResponseEntity<List<Emprestimo>> listarAtrasadosPorLeitor(@PathVariable Integer leitorId) {
        List<Emprestimo> emprestimos = emprestimoService.listarAtrasadosPorLeitor(leitorId);
        return ResponseEntity.ok(emprestimos);
    }

    @PatchMapping("/{id}/renovacao")
    public ResponseEntity<Emprestimo> renovar(@PathVariable Integer id, @Valid @RequestBody RenovacaoRequest dados) {

        Optional <Emprestimo> emprestimo = emprestimoService.renovar(id, dados.getNovaDataPrevista());
        if (emprestimo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(emprestimo.get());
    }

}
