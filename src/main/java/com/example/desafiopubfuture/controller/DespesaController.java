package com.example.desafiopubfuture.controller;

import com.example.desafiopubfuture.model.DespesaModel;
import com.example.desafiopubfuture.repository.DespesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
public class DespesaController {
    @Autowired
    private DespesaRepository repository;

    @GetMapping(path = "/api/despesa/{idDespesa}")
    public ResponseEntity consultar(@PathVariable("idDespesa") Integer idDespesa) {
            return repository.findById(idDespesa)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/api/despesas")
    public List<DespesaModel> consultar() {
        return (List<DespesaModel>) repository.findAll();
    }

    @PostMapping(path = "/api/despesa/cadastrar")
    public DespesaModel salvar(@RequestBody DespesaModel despesaModel)  {
        return repository.save(despesaModel);
}

    @DeleteMapping(value = "/api/despesa/{idDespesa}")
    public ResponseEntity<Object> Delete(@PathVariable(value = "idDespesa") Integer idDespesa)
    {
        Optional<DespesaModel> despesa = repository.findById(idDespesa);
        if(despesa.isPresent()){
            repository.deleteById(idDespesa);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/api/despesa/{idDespesa}/data/{dataInicial}/{dataFinal}")
    public List<DespesaModel> consultarDataInicialDataFinal(@PathVariable(value = "idDespesa") Integer idDespesa,
                                                            @PathVariable(value = "dataInicial") Date dataInicial,
                                                            @PathVariable(value = "dataFinal") Date dataFinal) {

        List<DespesaModel> listaBruta = (List<DespesaModel>) repository.findAll();
        List<DespesaModel> listaFiltrada = new LinkedList<DespesaModel>();

        for (DespesaModel despesa : listaBruta){
            if(despesa.idDespesa.equals(idDespesa) &&
                    despesa.dataPagamento.compareTo(dataInicial) > 0 &&
                    despesa.dataPagamento.compareTo(dataFinal) < 0)
            {
                listaFiltrada.add(despesa);
            }
        }

        return listaFiltrada;
    }

    @GetMapping(value = "/api/despesa/{idDespesa}/tipo/{tipoDespesa}")
    public List<DespesaModel> consultarDataInicialDataFinal(@PathVariable(value = "idDespesa") Integer idDespesa,
                                                            @PathVariable(value = "tipoDespesa") String tipoDespesa){

        List<DespesaModel> listaBruta = (List<DespesaModel>) repository.findAll();
        List<DespesaModel> listaFiltrada = new LinkedList<DespesaModel>();

        for (DespesaModel despesa : listaBruta){
            if(despesa.idDespesa.equals(idDespesa) &&
                    despesa.tipoDespesa.equals(tipoDespesa)){
                listaFiltrada.add(despesa);
            }
        }

        return listaFiltrada;
    }

    @PutMapping(value = "api/despesa/{idDespesa}")
    public ResponseEntity<DespesaModel> Put(@PathVariable(value = "idDespesa") Integer idDespesa,
                                            @RequestBody DespesaModel newDespesa)
    {
        Optional<DespesaModel> oldDespesa = repository.findById(idDespesa);
        if(oldDespesa.isPresent()){
            DespesaModel despesa = oldDespesa.get();
            despesa.setTipoDespesa(newDespesa.tipoDespesa);
            despesa.setDataPagamento(newDespesa.dataPagamento);
            despesa.setDataPagamentoEsperado(newDespesa.dataPagamentoEsperado);
            despesa.setConta(newDespesa.conta);
            despesa.setValor(newDespesa.valor);
            repository.save(despesa);
            return new ResponseEntity<DespesaModel>(despesa, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}