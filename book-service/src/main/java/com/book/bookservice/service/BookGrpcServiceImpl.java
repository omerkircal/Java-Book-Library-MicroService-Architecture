package com.book.bookservice.service;

import com.book.bookservice.dto.BookId;
import com.book.bookservice.dto.BookIdDto;
import com.book.bookservice.dto.BookServiceGrpc;
import com.book.bookservice.dto.Isbn;
import com.book.bookservice.exception.BookNotFoundException;
import com.book.bookservice.repository.BookRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BookGrpcServiceImpl extends BookServiceGrpc.BookServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(BookGrpcServiceImpl.class);
    private final BookRepository bookRepository;

    public BookGrpcServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void getBookIdByIsbn(Isbn isbn, StreamObserver<BookId> responseObserver) {
        logger.info("Grpc call received: " + isbn.getIsbn());
        BookIdDto bookId = bookRepository.getBookByIsbn(isbn.getIsbn())
                .map(book -> new BookIdDto(book.getId(), book.getIsbn()))
                .orElseThrow(() -> new BookNotFoundException("Book could not found by isbn: " + isbn));
        responseObserver.onNext(
                BookId.newBuilder()
                        .setBookId(bookId.getBookId())
                        .setIsbn(bookId.getIsbn())
                        .build());
        responseObserver.onCompleted();
    }
}
