package com.example.desafiopubfuture.controller;

import com.example.desafiopubfuture.model.ReceitaModel;
import com.example.desafiopubfuture.repository.ReceitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
public class ReceitaController {
    @Autowired
    private ReceitaRepository repository;

    @GetMapping(path = "/api/receita/{idReceita}")
    public ResponseEntity consultar(@PathVariable("idReceita") Integer idReceita) {
            return repository.findById(idReceita)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/api/receitas")
    public List<ReceitaModel> consultar() {
        return (List<ReceitaModel>) repository.findAll();
    }

    @PostMapping(path = "/api/receita/cadastrar")
    public ReceitaModel salvar(@RequestBody ReceitaModel receitaModel)  {
        return repository.save(receitaModel);
    }

    @DeleteMapping(value = "/api/receita/{idReceita}")
    public ResponseEntity<Object> Delete(@PathVariable(value = "idReceita") Integer idReceita)
    {
        Optional<ReceitaModel> receita = repository.findById(idReceita);
        if(receita.isPresent()){
            repository.deleteById(idReceita);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/api/receita/{idReceita}/data/{dataInicial}/{dataFinal}")
    public List<ReceitaModel> consultarDataInicialDataFinal(@PathVariable(value = "idReceita") Integer idReceita,
                                                            @PathVariable(value = "dataInicial") Date dataInicial,
                                                            @PathVariable(value = "dataFinal") Date dataFinal) {

        List<ReceitaModel> listaBruta = (List<ReceitaModel>) repository.findAll();
        List<ReceitaModel> listaFiltrada = new LinkedList<ReceitaModel>();
        
        for (ReceitaModel receita : listaBruta){
            if(receita.idReceita.equals(idReceita) &&
                    receita.dataRecebimento.compareTo(dataInicial) > 0 &&
                    receita.dataRecebimento.compareTo(dataFinal) < 0)
            {
                listaFiltrada.add(receita);
            }
        }

        return listaFiltrada;
    }

    @GetMapping(value = "/api/receita/{idReceita}/tipo/{tipoReceita}")
    public List<ReceitaModel> consultarDataInicialDataFinal(@PathVariable(value = "idReceita") Integer idReceita,
                                                            @PathVariable(value = "tipoReceita") String tipoReceita){

        List<ReceitaModel> listaBruta = (List<ReceitaModel>) repository.findAll();
        List<ReceitaModel> listaFiltrada = new LinkedList<ReceitaModel>();

        for (ReceitaModel receita : listaBruta){
            if(receita.idReceita.equals(idReceita) &&
                    receita.tipoReceita.equals(tipoReceita)){
                listaFiltrada.add(receita);
            }
        }

        return listaFiltrada;
    }

    @PutMapping(value = "api/receita/{idReceita}")
    public ResponseEntity<ReceitaModel> Put(@PathVariable(value = "idReceita") Integer idReceita,
                                            @RequestBody ReceitaModel newReceita)
    {
        Optional<ReceitaModel> oldReceita = repository.findById(idReceita);
        if(oldReceita.isPresent()){
            ReceitaModel receita = oldReceita.get();
            receita.setTipoReceita(newReceita.tipoReceita);
            receita.setDataRecebimento(newReceita.dataRecebimento);
            receita.setDataRecebimentoEsperado(newReceita.dataRecebimentoEsperado);
            receita.setDescricao(newReceita.descricao);
            receita.setValor(newReceita.valor);
            repository.save(receita);
            return new ResponseEntity<ReceitaModel>(receita, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}