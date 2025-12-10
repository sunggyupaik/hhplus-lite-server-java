package kr.hhplus.be.server.domain.concert;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@ToString
@Table(name="concerts")
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long id;
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private AgeLimitStatus ageLimitStatus;

    @Getter
    @RequiredArgsConstructor
    public enum AgeLimitStatus {
        TWELVE("12세"),
        FIFTEEN("15세"),
        NINETEEN("19세"),
        ALL("전체이용가");

        private final String description;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "concert")
    @ToString.Exclude
    private List<Schedule> scheduleList = new ArrayList<>();

    @Builder
    public Concert(Long id, String title, String description, AgeLimitStatus ageLimitStatus, List<Schedule> scheduleList) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.ageLimitStatus = ageLimitStatus;
        this.scheduleList = scheduleList;
    }
}