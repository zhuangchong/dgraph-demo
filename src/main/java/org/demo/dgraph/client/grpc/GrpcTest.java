package org.demo.dgraph.client.grpc;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc;
import io.dgraph.DgraphProto;
import io.dgraph.Transaction;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GrpcTest {
    private static final String TEST_HOSTNAME = "localhost";
    private static final int TEST_PORT = 9080;

    private static DgraphClient createDgraphClient(boolean withAuthHeader) {

        ManagedChannel channel =
                ManagedChannelBuilder.forAddress(TEST_HOSTNAME, TEST_PORT).usePlaintext().build();
        DgraphGrpc.DgraphStub stub = DgraphGrpc.newStub(channel);

        if (withAuthHeader) {
            Metadata metadata = new Metadata();
            metadata.put(
                    Metadata.Key.of("auth-token", Metadata.ASCII_STRING_MARSHALLER), "the-auth-token-value");
            stub = MetadataUtils.attachHeaders(stub, metadata);
        }

        return new DgraphClient(stub);
    }

    public static void main(final String[] args) {
        DgraphClient dgraphClient = createDgraphClient(false);
        String query = "{\n"
                + "  me(func:allofterms(name, \"Star Wars\")) @filter(ge(release_date, \"1980\")) {\n"
                + "    name\n"
                + "    release_date\n"
                + "    revenue\n"
                + "    running_time\n"
                + "    director {\n"
                + "     name\n"
                + "    }\n"
                + "    starring {\n"
                + "     name\n"
                + "    }\n"
                + "  }\n"
                + "}";
        DgraphProto.Response res = dgraphClient.newTransaction().query(query);
        System.out.println("--grpc client res:"+res.getJson().toStringUtf8());
    }

    private static void test_1(){
        DgraphClient dgraphClient = createDgraphClient(false);

        // Initialize
        dgraphClient.alter(DgraphProto.Operation.newBuilder().setDropAll(true).build());

        // Set schema
        String schema = "name: string @index(exact) .";
        DgraphProto.Operation operation = DgraphProto.Operation.newBuilder().setSchema(schema).build();
        dgraphClient.alter(operation);

        Gson gson = new Gson(); // For JSON encode/decode

        try (Transaction txn = dgraphClient.newTransaction()){
            // Create data
            GrpcTest.Person p = new GrpcTest.Person();
            p.name = "Alice";

            // Serialize it
            String json = gson.toJson(p);

            // Run mutation
            DgraphProto.Mutation mutation =
                    DgraphProto.Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(json.toString())).build();
            txn.mutate(mutation);
            txn.commit();

        }
        // Query
        String query =
                "query all($a: string){\n" + "all(func: eq(name, $a)) {\n" + "    name\n" + "  }\n" + "}";
        Map<String, String> vars = Collections.singletonMap("$a", "Alice");
        DgraphProto.Response res = dgraphClient.newTransaction().queryWithVars(query, vars);

        // Deserialize
        GrpcTest.People ppl = gson.fromJson(res.getJson().toStringUtf8(), GrpcTest.People.class);

        // Print results
        System.out.printf("people found: %d\n", ppl.all.size());
        ppl.all.forEach(person -> System.out.println(person.name));
    }

    static class Person {
        String name;

        Person() {}
    }

    static class People {
        List<GrpcTest.Person> all;

        People() {}
    }
}
