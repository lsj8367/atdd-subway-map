package nextstep.subway.applicaion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.LineSaveRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(final LineRepository lineRepository, final StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse createLines(final LineSaveRequest request) {
        final List<StationResponse> endStations = stationService.findEndStations(List.of(request.getUpStationId(), request.getDownStationId()));

        final Line savedLine = lineRepository.save(new Line(request.getName(),
            request.getColor(), request.getUpStationId(), request.getDownStationId(), new Distance(request.getDistance())));

        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor(), endStations);
    }

    public List<LineResponse> getLines() {
        final List<Line> linesList = lineRepository.findAll();

        return linesList.stream()
            .map(line -> new LineResponse(
                line.getId(), line.getName(), line.getColor(),
                stationService.findEndStations(List.of(line.getUpStationId(), line.getDownStationId())))
            ).collect(Collectors.toList());
    }

}
