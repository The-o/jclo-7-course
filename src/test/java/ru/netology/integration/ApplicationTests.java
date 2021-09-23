package ru.netology.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
public class ApplicationTests {

    private static final String IMAGE_NAME = "jclo-7-course-back";
    private static final int IMAGE_PORT = 8080;

    private static String uri;
    private static HttpHeaders headers;

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    private static GenericContainer<?> app = new GenericContainer<>(IMAGE_NAME)
        .withExposedPorts(IMAGE_PORT)
        .withEnv("SERVICE_PORT", String.valueOf(IMAGE_PORT))
        .withEnv("LOGS_ENGINE", "stdout")
        .withEnv("CODE_GENERATOR", "zero")
        .withEnv("CODE_LENGTH", "4");

    @BeforeAll
    public static void initTests() {
        uri = "http://localhost:" + app.getMappedPort(IMAGE_PORT);

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testTransferOK() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
            () -> assertEquals(responseBody.length(), 1),
            () -> assertTrue(responseBody.get("operationId") instanceof String)
        );
    }

    @Test
    public void testTransferNoCardFromNumber() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Не указан номер карты отправителя", responseBody.get("message"))
        );
    }

    @Test
    public void testTransferWrongCardFromNumber() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "asd")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Неверный номер карты отправителя", responseBody.get("message"))
        );
    }

    @Test
    public void testTransferWrongCardFromNumberChecksum() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000001")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Неверный номер карты отправителя", responseBody.get("message"))
        );
    }

    @Test
    public void testTransferNoCardFromValidTill() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Не указана дата действия карты отправителя", responseBody.get("message"))
        );
    }

    @Test
    public void testTransferWrongCardFromValidTill() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "asd")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Неверная дата действия карты отправителя", responseBody.get("message"))
        );
    }

    @Test
    public void testTransferPassedCardFromValidTill() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/01")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Срок действия карты отправителя истёк", responseBody.get("message"))
        );
    }

    @Test
    public void testTransferNoCardFromCVV() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/99")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Не указан CVV2/CVC2 код карты отправителя", responseBody.get("message"))
        );
    }

    @Test
    public void testTransferWrongCardFromCVV() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "asd")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );


        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Неверный CVV2/CVC2 код карты отправителя", responseBody.get("message"))
        );
    }

    @Test
    public void testTransferNoCardToNumber() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Не указан номер карты адресата", responseBody.get("message"))
        );
    }

    @Test
    public void testTransferWrongCardToNumberChecksum() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654321")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Неверный номер карты адресата", responseBody.get("message"))
        );
    }

    @Test
    public void testTransferWrongCardToNumber() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "asd")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Неверный номер карты адресата", responseBody.get("message"))
        );
    }

    @Test
    public void testTransferNoAmount() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327");

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Не указана сумма перевода", responseBody.get("message"))
        );
    }

    @Test
    public void testTransferNoAmountValue() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("currency", "RUR")
            );

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Неверная сумма перевода", responseBody.get("message"))
        );
    }

    @Test
    public void testTransferWrongAmountValue() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", -1)
                .put("currency", "RUR")
            );

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Неверная сумма перевода", responseBody.get("message"))
        );
    }

    @Test
    public void testTransferNoAmountCurrency() throws JSONException {
        JSONObject requestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", 10000)
            );

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/transfer", request, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Не указана валюта перевода", responseBody.get("message"))
        );
    }

    @Test
    public void testConfirmOperationOK() throws JSONException {
        JSONObject transferRequestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> transferRequest = new HttpEntity<String>(transferRequestBody.toString(), headers);
        ResponseEntity<String> transferResponse = restTemplate.postForEntity(uri + "/transfer", transferRequest, String.class);
        JSONObject transferResponseBody = new JSONObject(transferResponse.getBody());

        JSONObject confirmRequestBody = new JSONObject()
            .put("operationId", transferResponseBody.get("operationId"))
            .put("code", "0000");
        HttpEntity<String> confirmRequest = new HttpEntity<String>(confirmRequestBody.toString(), headers);
        ResponseEntity<String> confirmResponse = restTemplate.postForEntity(uri + "/confirmOperation", confirmRequest, String.class);
        JSONObject confirmResponseBody = new JSONObject(confirmResponse.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.OK, confirmResponse.getStatusCode()),
            () -> assertEquals(transferResponseBody.get("operationId"), confirmResponseBody.get("operationId"))
        );
    }

    @Test
    public void testConfirmOperationSentTwice() throws JSONException {
        JSONObject transferRequestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> transferRequest = new HttpEntity<String>(transferRequestBody.toString(), headers);
        ResponseEntity<String> transferResponse = restTemplate.postForEntity(uri + "/transfer", transferRequest, String.class);
        JSONObject transferResponseBody = new JSONObject(transferResponse.getBody());

        JSONObject confirmRequestBody = new JSONObject()
            .put("operationId", transferResponseBody.get("operationId"))
            .put("code", "0000");
        HttpEntity<String> confirmRequest = new HttpEntity<String>(confirmRequestBody.toString(), headers);
        restTemplate.postForEntity(uri + "/confirmOperation", confirmRequest, String.class);
        ResponseEntity<String> confirmResponse = restTemplate.postForEntity(uri + "/confirmOperation", confirmRequest, String.class);
        JSONObject confirmResponseBody = new JSONObject(confirmResponse.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, confirmResponse.getStatusCode()),
            () -> assertEquals("Операция уже выполнена", confirmResponseBody.get("message"))
        );
    }

    @Test
    public void testConfirmOperationNoOperationId() throws JSONException {
        JSONObject transferRequestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> transferRequest = new HttpEntity<String>(transferRequestBody.toString(), headers);
        restTemplate.postForEntity(uri + "/transfer", transferRequest, String.class);

        JSONObject confirmRequestBody = new JSONObject()
            .put("code", "0000");
        HttpEntity<String> confirmRequest = new HttpEntity<String>(confirmRequestBody.toString(), headers);
        ResponseEntity<String> confirmResponse = restTemplate.postForEntity(uri + "/confirmOperation", confirmRequest, String.class);
        JSONObject confirmResponseBody = new JSONObject(confirmResponse.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, confirmResponse.getStatusCode()),
            () -> assertEquals("Не указан ID операции", confirmResponseBody.get("message"))
        );
    }

    @Test
    public void testConfirmOperationNoCode() throws JSONException {
        JSONObject transferRequestBody = new JSONObject()
            .put("cardFromNumber", "0000000000000000")
            .put("cardFromValidTill", "01/99")
            .put("cardFromCVV", "123")
            .put("cardToNumber", "2345678987654327")
            .put("amount", new JSONObject()
                .put("value", 10000)
                .put("currency", "RUR")
            );

        HttpEntity<String> transferRequest = new HttpEntity<String>(transferRequestBody.toString(), headers);
        ResponseEntity<String> transferResponse = restTemplate.postForEntity(uri + "/transfer", transferRequest, String.class);
        JSONObject transferResponseBody = new JSONObject(transferResponse.getBody());

        JSONObject confirmRequestBody = new JSONObject()
            .put("operationId", transferResponseBody.get("operationId"));
        HttpEntity<String> confirmRequest = new HttpEntity<String>(confirmRequestBody.toString(), headers);
        ResponseEntity<String> confirmResponse = restTemplate.postForEntity(uri + "/confirmOperation", confirmRequest, String.class);
        JSONObject confirmResponseBody = new JSONObject(confirmResponse.getBody());

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, confirmResponse.getStatusCode()),
            () -> assertEquals("Не указан код подтверждения операции", confirmResponseBody.get("message"))
        );
    }

}
