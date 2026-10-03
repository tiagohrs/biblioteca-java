package br.com.senai.teste.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "emprestimo")
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate dataDevolucao;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private static final BigDecimal MULTA_POR_DIA = new BigDecimal("2.00");

    @ManyToOne
    @JoinColumn(name = "leitor_id", nullable = false)
    private Leitor leitor;

    @ManyToOne
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    public Emprestimo() {
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public Leitor getLeitor() {
        return leitor;
    }

    public void setLeitor(Leitor leitor) {
        this.leitor = leitor;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public void setDataPrevistaDevolucao(LocalDate dataPrevistaDevolucao) {
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
    }

    public String getSituacao() {
        if (dataDevolucao != null) {
            return "DEVOLVIDO";
        }

        if (dataPrevistaDevolucao == null) {
            return "SEM_PREVISAO";
        }

        if (dataPrevistaDevolucao.isBefore(LocalDate.now())) {
            return "ATRASADO";
        }

        return "ATIVO";
    }

    public long getDiasAtraso() {
    if (dataPrevistaDevolucao == null) {
        return 0;
    }

    LocalDate dataFinal;

    if (dataDevolucao == null) {
        dataFinal = LocalDate.now();
    } else {
        dataFinal = dataDevolucao;
    }

    if (dataFinal.isAfter(dataPrevistaDevolucao)) {
        return ChronoUnit.DAYS.between(dataPrevistaDevolucao, dataFinal);
    }

    return 0;
}



}
