package com.github.dkw87.personservice.api.grpc;

import com.github.dkw87.grpc.proto.person.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Locale;

@GrpcService
@Slf4j
public class PersonServiceImpl extends PersonServiceGrpc.PersonServiceImplBase {

    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    @Override
    public void getPerson(PersonRequest request, StreamObserver<PersonResponse> responseObserver) {
        log.info("Received request for getPerson() for id {}...", request.getId());

        if (simulatePersonNotFound(request, responseObserver)) return;

        final List<String> hobbies = List.of(
                FAKER.hobby().activity(),
                FAKER.hobby().activity(),
                FAKER.hobby().activity()
        );
        final String gender = FAKER.gender().binaryTypes();
        final String fullName = gender.equals("Male")
                ? FAKER.name().maleFirstName() + " " + FAKER.name().lastName()
                : FAKER.name().femaleFirstName() + " " + FAKER.name().lastName();

        final Person person = Person.newBuilder()
                .setName(fullName)
                .setGender(gender)
                .setDob(FAKER.timeAndDate().birthday("dd-MM-yyyy"))
                .setPob(FAKER.address().city())
                .addAllHobbies(hobbies) // dont use setter but addAll to add whole List<>, setter can overwrite specific index w/ value
                .build();

        final PersonResponse response = PersonResponse.newBuilder()
                .setPerson(person)
                .build();

        responseObserver.onNext(response);
        log.info("Successfully sent PersonResponse for id {}", request.getId());
        responseObserver.onCompleted();
    }

    private static boolean simulatePersonNotFound(PersonRequest request, StreamObserver<PersonResponse> responseObserver) {
        if (FAKER.number().numberBetween(0, 10) == 0) {
            log.warn("Person with id {} not found", request.getId());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(
                            String.format("Person with id %d not found", request.getId()))
                    .asRuntimeException());
            return true;
        }
        return false;
    }

}
