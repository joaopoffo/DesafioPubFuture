package com.example.desafiopubfuture.controller;

import com.example.desafiopubfuture.model.ContaModel;
import com.example.desafiopubfuture.model.DespesaModel;
import com.example.desafiopubfuture.model.ReceitaModel;
import com.example.desafiopubfuture.model.TransferenciaModel;
import com.example.desafiopubfuture.repository.ContaRepository;
import com.example.desafiopubfuture.repository.DespesaRepository;
import com.example.desafiopubfuture.repository.ReceitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ContaController {
    @Autowired
    private ContaRepository contaRepository;
    @Autowired
    private DespesaRepository despesaRepository;
    @Autowired
    private ReceitaRepository receitaRepository;

    @GetMapping(path = "/api/conta/{idConta}")
    public ResponseEntity consultar(@PathVariable("idConta") Integer idConta) {
            return contaRepository.findById(idConta)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/api/contas")
    public List<ContaModel> consultar() {
        return (List<ContaModel>) contaRepository.findAll();
    }

    @PostMapping(path = "/api/conta/cadastrar")
    public ContaModel salvar(@RequestBody ContaModel contaModel)  {
        return contaRepository.save(contaModel);
    }

    @DeleteMapping(value = "/api/conta/{idConta}")
    public ResponseEntity<Object> deletar(@PathVariable(value = "idConta") Integer idConta)
    {
        Optional<ContaModel> conta = contaRepository.findById(idConta);
        if(conta.isPresent()){
            contaRepository.deleteById(idConta);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "api/conta/{idConta}")
    public ResponseEntity<ContaModel> atualizar(@PathVariable(value = "idConta") Integer idConta,
                                            @RequestBody ContaModel newConta)
    {
        Optional<ContaModel> oldConta = contaRepository.findById(idConta);
        if(oldConta.isPresent()){
            ContaModel conta = oldConta.get();
            conta.setTipoConta(newConta.tipoConta);
            conta.setSaldo(newConta.saldo);
            conta.setInstituicaoFinanceira(newConta.instituicaoFinanceira);
            contaRepository.save(conta);
            return new ResponseEntity<ContaModel>(conta, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(path = "/api/conta/transferencia")
    public ResponseEntity<Object> novaTransferencia(@RequestBody TransferenciaModel transferencia)  {
        Optional<ContaModel> oldContaOrigem = contaRepository.findById(transferencia.getIdContaOrigem());
        Optional<ContaModel> oldContaDestino = contaRepository.findById(transferencia.getIdContaDestino());

        if(oldContaOrigem.isPresent() && oldContaDestino.isPresent()){
            ContaModel contaOrigem = oldContaOrigem.get();
            ContaModel contaDestino = oldContaDestino.get();

            if(contaOrigem.saldo >= transferencia.getValor()){
                contaOrigem.saldo -= transferencia.getValor();
                contaDestino.saldo += transferencia.getValor();
                contaRepository.save(contaOrigem);
                contaRepository.save(contaDestino);
                return new ResponseEntity<Object>(transferencia, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(path = "/api/conta/{idConta}/saldo")
    public ResponseEntity<Object> consultarSaldo(@PathVariable("idConta") Integer idConta) {
        Optional<ContaModel> oldConta = contaRepository.findById(idConta);

        if(!oldConta.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ContaModel conta = oldConta.get();
        List<DespesaModel> despesasTotais = (List<DespesaModel>) despesaRepository.findAll();
        List<ReceitaModel> receitasTotais = (List<ReceitaModel>) receitaRepository.findAll();
        Double saldoTotal = 0.0;

        for (ReceitaModel receita : receitasTotais){
            if(receita.conta.equals(idConta)){
                saldoTotal += receita.getValor();
            }
        }

        for (DespesaModel despesa : despesasTotais){
            if(despesa.conta.equals(idConta)){
                saldoTotal -= despesa.getValor();
            }
        }

        conta.saldo = saldoTotal;
        contaRepository.save(conta);
        return new ResponseEntity<Object>(conta, HttpStatus.OK);
    }
}