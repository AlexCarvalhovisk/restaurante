package dev.alexcarvalho.restaurante.service;

import dev.alexcarvalho.restaurante.domain.entity.CategoriaProduto;
import dev.alexcarvalho.restaurante.domain.entity.Produto;
import dev.alexcarvalho.restaurante.dto.ProdutoRequest;
import dev.alexcarvalho.restaurante.dto.ProdutoResponse;
import dev.alexcarvalho.restaurante.repository.CategoriaProdutoRepository;
import dev.alexcarvalho.restaurante.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaProdutoRepository categoriaProdutoRepository;

    //Criei um contrutor, pois aí o Spring, instancia as informações automaticamente
    public ProdutoService(ProdutoRepository produtoRepository, CategoriaProdutoRepository categoriaProdutoRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaProdutoRepository = categoriaProdutoRepository;
    }

    public ProdutoResponse cadastrar(ProdutoRequest request) {
        //Primeiro eu busco a categoria para ter a informação dela
        CategoriaProduto categoriaProduto = buscarCategoriaPorId(request.categoriaId());
        //Uso o toEntity para transformar em entidade
        Produto produto = request.toEntity(categoriaProduto);
        //Depois eu salvo
        Produto produtoSalvo = produtoRepository.save(produto);
        //Aqui eu retorno o DTO
        return ProdutoResponse.fromEntity(produtoSalvo);
    }

    public Page<ProdutoResponse> listar(Pageable pageable) {
        return produtoRepository.findAll(pageable).map(ProdutoResponse::fromEntity);
    }

    public ProdutoResponse buscarPorId(Long id) {
        Produto produto = buscarProdutoPorId(id);
        return ProdutoResponse.fromEntity(produto);
    }

    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarProdutoPorId(id);
        CategoriaProduto categoriaProduto = buscarCategoriaPorId(request.categoriaId());
        request.preencher(produto, categoriaProduto);
        Produto produtoAtualizado = produtoRepository.save(produto);
        return ProdutoResponse.fromEntity(produtoAtualizado);
    }

    public void excluir(Long id) {
        Produto produto = buscarProdutoPorId(id);
        produtoRepository.delete(produto);
    }

    private Produto buscarProdutoPorId(Long id) {
        return produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado."));
    }

    private CategoriaProduto buscarCategoriaPorId(Long id) {
        return categoriaProdutoRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada."));
    }
}
