package br.com.senai.teste.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import br.com.senai.teste.model.Leitor;
import br.com.senai.teste.service.LeitorService;

@RestController
@RequestMapping("/leitores")
public class LeitorController {

    private final LeitorService leitorService;

    public LeitorController(LeitorService leitorService) {
        this.leitorService = leitorService;
    }

    @PostMapping
    public ResponseEntity<Leitor> cadastrar(
            @Valid @RequestBody Leitor leitor) {

        Leitor leitorCadastrado = leitorService.cadastrar(leitor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(leitorCadastrado);
    }

    @GetMapping
    public ResponseEntity<List<Leitor>> listar() {
        List<Leitor> leitores = leitorService.listar();
        return ResponseEntity.ok(leitores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Leitor> buscarPorId(
            @PathVariable Integer id) {

        Optional<Leitor> leitor = leitorService.buscarPorId(id);

        if (leitor.isPresent()) {
            return ResponseEntity.ok(leitor.get());
        } else {
            return ResponseEntity.notFound().build();

        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<Leitor> atualizar(
            @PathVariable Integer id, @Valid @RequestBody Leitor novosDados) {

        Optional<Leitor> leitorAtualizado = leitorService.atualizar(id, novosDados);

        if (leitorAtualizado.isPresent()) {
            return ResponseEntity.ok(leitorAtualizado.get());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {

        System.out.println(id);
        boolean excluido = leitorService.excluir(id);

        if (excluido) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<Leitor> enviarFoto(
            @PathVariable Integer id,
            @RequestParam("arquivo") MultipartFile arquivo) {

        Leitor leitorAtualizado = leitorService.salvarFoto(id, arquivo);

        return ResponseEntity.ok(leitorAtualizado);
    }

}