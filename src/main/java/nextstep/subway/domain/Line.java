package nextstep.subway.domain;

import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private LineContent lineContent;

    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(final LineContent lineContent, final Section section) {
        this.lineContent = lineContent;
        this.sections = new Sections();
        addSection(section);
    }

    public void addSection(Section section) {
        sections.add(section);
        section.setLine(this);
    }

    public Long getId() {
        return id;
    }

    public String name() {
        return lineContent.name();
    }

    public String color() {
        return lineContent.color();
    }

    public LineContent getLineContent() {
        return lineContent;
    }

    public Sections getSections() {
        return sections;
    }

    public List<Station> stations() {
        return sections.stations();
    }

    public Station beforeDownStation(final Long newUpStationId) {
        List<Station> stations = stations();
        final Station station = stations.get(stations.size() - 1);
        station.isSameStationId(newUpStationId);

        return station;
    }

    public void updateNameAndColor(final String name, final String color) {
        this.lineContent = new LineContent(name, color);
    }

    public void removeLastSection(final long stationId) {
        sections.removeLastSection(stationId);
    }

    public void removeSection(final Section section) {
        sections.removeSection(section);
    }

}
