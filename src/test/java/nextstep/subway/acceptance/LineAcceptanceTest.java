package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("노선 관련 인수 테스트")
class LineAcceptanceTest extends BaseAcceptance {

    final StationAcceptanceTest stationAcceptanceTest = new StationAcceptanceTest();

    @BeforeEach
    void setUp() {
        RestAssured.port = super.port;
        dataBaseCleaner.afterPropertiesSet();
        dataBaseCleaner.tableClear();
    }

    /*
    When 지하철 노선을 생성하면
    Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철 노선 생성")
    void createLines() {
        //when
        shinbundangLine();

        //then
        final ExtractableResponse<Response> lineResponse = findOneLine();

        final String name = lineResponse.jsonPath().getString("name");
        assertThat(name).isEqualTo("신분당선");
    }

    /*
    Given 2개의 지하철 노선을 생성하고
    When 지하철 노선 목록을 조회하면
    Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 목록 조회")
    void getLines() {
        shinbundangLine();
        stationAcceptanceTest.createSubwayStation("까치울역");
        createSubwayLines("분당선", "bg-red-600", 1, 3, 10);

        //when
        final ExtractableResponse<Response> getLinesResponse = RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .extract();

        //then
        final List<String> name = getLinesResponse.jsonPath().getList("name", String.class);
        assertThat(name).hasSize(2)
            .containsExactly("신분당선", "분당선");

    }

    /*
    Given 지하철 노선을 생성하고
    When 생성한 지하철 노선을 조회하면
    Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 조회")
    void getOneLine() {
        //given
        shinbundangLine();

        //when
        final ExtractableResponse<Response> getLinesResponse = findOneLine();

        //then
        String lineName = getLinesResponse.jsonPath().get("name");
        String startStation = getLinesResponse.jsonPath().get("stations[0].name");
        String endStation = getLinesResponse.jsonPath().get("stations[1].name");
        assertThat(lineName).isEqualTo("신분당선");
        assertThat(startStation).isEqualTo("강남역");
        assertThat(endStation).isEqualTo("양재역");
    }

    /*
    Given 지하철 노선을 생성하고
    When 생성한 지하철 노선을 수정하면
    Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철 노선 수정")
    void updateLines() {
        shinbundangLine();

        //when
        final Map<String, String> param = Map.of("name", "다른분당선", "color", "bg-red-600");
        RestAssured.given().log().all()
            .body(param)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/1")
            .then().log().all();

        //then
        final ExtractableResponse<Response> getLinesResponse = findOneLine();
        assertThat(getLinesResponse.jsonPath().getString("name")).isEqualTo("다른분당선");
        assertThat(getLinesResponse.jsonPath().getString("color")).isEqualTo("bg-red-600");
    }

    /*
    Given 지하철 노선을 생성하고
    When 생성한 지하철 노선을 삭제하면
    Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철 노선 삭제")
    void removeLines() {
        //given
        shinbundangLine();

        //when
        final ExtractableResponse<Response> removeResponse = RestAssured.given().log().all()
            .when().delete("/lines/1")
            .then().log().all()
            .extract();

        //then
        assertThat(removeResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void shinbundangLine() {
        stationAcceptanceTest.createSubwayStation("강남역");
        stationAcceptanceTest.createSubwayStation("양재역");

        createSubwayLines("신분당선", "bg-red-600", 1, 2, 10);
    }

    private void createSubwayLines(final String name, final String color,
        final long upStationId, final long downStationId, final int distance) {
        final Map<String, Object> param = Map.of(
            "name", name,
            "color", color,
            "upStationId", upStationId,
            "downStationId", downStationId,
            "distance", distance
        );

        RestAssured.given().log().all()
            .body(param)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then();
    }

    private ExtractableResponse<Response> findOneLine() {
        return RestAssured.given().log().all()
            .when().get("/lines/1")
            .then().log().all()
            .extract();
    }
}
