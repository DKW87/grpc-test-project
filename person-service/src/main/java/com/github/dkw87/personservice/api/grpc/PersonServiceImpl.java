package com.github.dkw87.personservice.api.grpc;

import com.github.dkw87.grpc.proto.person.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Locale;

@GrpcService
@Slf4j
public class PersonServiceImpl extends PersonServiceGrpc.PersonServiceImplBase {

    @Override
    public void getPerson(PersonRequest request, StreamObserver<PersonResponse> responseObserver) {
        log.info("Received request for getPerson() for id {}...", request.getId());
        Faker faker = new Faker(Locale.ENGLISH);

        List<String> hobbys = List.of(faker.hobby().activity(), faker.hobby().activity(), faker.hobby().activity());

        Person person = Person.newBuilder()
                .setName(faker.name().fullName())
                .setGender(faker.gender().binaryTypes())
                .setDob(faker.timeAndDate().birthday("dd-MM-yyyy"))
                .setPob(faker.address().city())
                .addAllHobbys(hobbys) // dont use setter but addAll to add whole List<>, setter can overwrite specific index w/ value
                .build();

        PersonResponse response = PersonResponse.newBuilder()
                .setPerson(person)
                .build();

        responseObserver.onNext(response);
        log.info("Successfully sent PersonResponse for id {}", request.getId());
        responseObserver.onCompleted();
    }

}
