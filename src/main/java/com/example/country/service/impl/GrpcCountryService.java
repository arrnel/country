package com.example.country.service.impl;

import com.example.country.data.entity.Country;
import com.example.country.ex.CountryNotFoundException;
import com.example.country.service.CountryService;
import com.example.country.util.DateUtil;
import com.example.grpc.country.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class GrpcCountryService extends CountryServiceGrpc.CountryServiceImplBase {

    private final CountryService countryService;

    @Transactional(readOnly = true)
    @Override
    public void findById(final IdRequest request, final StreamObserver<CountryGrpcResponseDTO> responseObserver) {

        Country country = countryService.findById(request.getId())
                .orElseThrow(() ->
                        new CountryNotFoundException("Country with id = [%d] not found".formatted(request.getId())));

        responseObserver.onNext(
                CountryGrpcResponseDTO.newBuilder()
                        .setId(country.getId())
                        .setName(country.getName())
                        .setCode(country.getCode())
                        .setCreatedDate(
                                DateUtil.timestampToGrpcDate(country.getDateCreated()))
                        .setUpdatedDate(
                                DateUtil.timestampToGrpcDate(country.getDateUpdated()))
                        .build()
        );

        responseObserver.onCompleted();

    }

    @Override
    public void add(final AddCountryGrpcRequestDTO request, final StreamObserver<CountryGrpcResponseDTO> responseObserver) {

        final var now = DateUtil.getCurrentTimestamp();
        Country country = countryService.add(
                Country.builder()
                        .name(request.getName())
                        .code(request.getCode())
                        .dateCreated(now)
                        .dateUpdated(now)
                        .build());

        responseObserver.onNext(CountryGrpcResponseDTO.newBuilder()
                .setId(country.getId())
                .setName(country.getName())
                .setCode(country.getCode())
                .setCreatedDate(DateUtil.timestampToGrpcDate(country.getDateCreated()))
                .setUpdatedDate(DateUtil.timestampToGrpcDate(country.getDateUpdated()))
                .build());

        responseObserver.onCompleted();

    }

    @Transactional(readOnly = true)
    @Override
    public void randomCountries(final CountRequest request, final StreamObserver<CountriesResponseDTO> responseObserver) {

        List<Country> allCountries = countryService.findAll();
        Collections.shuffle(allCountries);

        responseObserver.onNext(CountriesResponseDTO.newBuilder()
                .addAllAllCountries(
                        allCountries.subList(0, Math.min(request.getCount(), allCountries.size())).stream()
                                .map(country -> CountryGrpcResponseDTO.newBuilder()
                                        .setId(country.getId())
                                        .setName(country.getName())
                                        .setName(country.getName())
                                        .setCreatedDate(
                                                DateUtil.timestampToGrpcDate(country.getDateCreated()))
                                        .setUpdatedDate(
                                                DateUtil.timestampToGrpcDate(
                                                        country.getDateUpdated()))
                                        .build())
                                .toList()
                )
                .build()
        );
        responseObserver.onCompleted();

    }

}
