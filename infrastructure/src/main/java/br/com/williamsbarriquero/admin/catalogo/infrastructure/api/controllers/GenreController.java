package br.com.williamsbarriquero.admin.catalogo.infrastructure.api.controllers;

import br.com.williamsbarriquero.admin.catalogo.application.genre.create.CreateGenreCommand;
import br.com.williamsbarriquero.admin.catalogo.application.genre.create.CreateGenreUseCase;
import br.com.williamsbarriquero.admin.catalogo.application.genre.delete.DeleteGenreUseCase;
import br.com.williamsbarriquero.admin.catalogo.application.genre.retrieve.get.GetGenreByIdUseCase;
import br.com.williamsbarriquero.admin.catalogo.application.genre.retrieve.list.ListGenreUseCase;
import br.com.williamsbarriquero.admin.catalogo.application.genre.update.UpdateGenreCommand;
import br.com.williamsbarriquero.admin.catalogo.application.genre.update.UpdateGenreUseCase;
import br.com.williamsbarriquero.admin.catalogo.domain.pagination.Pagination;
import br.com.williamsbarriquero.admin.catalogo.domain.pagination.SearchQuery;
import br.com.williamsbarriquero.admin.catalogo.infrastructure.api.GenreAPI;
import br.com.williamsbarriquero.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import br.com.williamsbarriquero.admin.catalogo.infrastructure.genre.models.GenreListResponse;
import br.com.williamsbarriquero.admin.catalogo.infrastructure.genre.models.GenreResponse;
import br.com.williamsbarriquero.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import br.com.williamsbarriquero.admin.catalogo.infrastructure.genre.presenters.GenreApiPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class GenreController implements GenreAPI {

    private final CreateGenreUseCase createGenreUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final UpdateGenreUseCase updateGenreUseCase;
    private final DeleteGenreUseCase deleteGenreUseCase;
    private final ListGenreUseCase listGenreUseCase;

    public GenreController(
            final CreateGenreUseCase createGenreUseCase,
            final GetGenreByIdUseCase getGenreByIdUseCase,
            final UpdateGenreUseCase updateGenreUseCase,
            final DeleteGenreUseCase deleteGenreUseCase,
            final ListGenreUseCase listGenreUseCase
    ) {
        this.createGenreUseCase = createGenreUseCase;
        this.getGenreByIdUseCase = getGenreByIdUseCase;
        this.updateGenreUseCase = updateGenreUseCase;
        this.deleteGenreUseCase = deleteGenreUseCase;
        this.listGenreUseCase = listGenreUseCase;
    }

    @Override
    public ResponseEntity<?> createGenre(final CreateGenreRequest input) {
        final var aCommand = CreateGenreCommand.with(
                input.name(),
                input.isActive(),
                input.categories()
        );

        final var output = this.createGenreUseCase.execute(aCommand);

        return ResponseEntity.created(URI.create("/genres/" + output.id())).body(output);
    }

    @Override
    public Pagination<GenreListResponse> listGenres(
            final String search,
            final int page,
            final int perPage,
            final String sort,
            final String direction
    ) {
        return this.listGenreUseCase.execute(new SearchQuery(page, perPage, search, sort, direction))
                .map(GenreApiPresenter::present);
    }

    @Override
    public GenreResponse getById(final String id) {
        return GenreApiPresenter.present(this.getGenreByIdUseCase.execute(id));
    }

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateGenreRequest input) {
        final var aCommand = UpdateGenreCommand.with(
                id,
                input.name(),
                input.isActive(),
                input.categories()
        );

        final var output = this.updateGenreUseCase.execute(aCommand);

        return ResponseEntity.ok(output);
    }

    @Override
    public void deleteById(final String id) {
        this.deleteGenreUseCase.execute(id);
    }
}
