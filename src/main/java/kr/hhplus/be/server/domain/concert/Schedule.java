package kr.hhplus.be.server.domain.concert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@ToString
@Table(name="schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;
    private Long basePrice;
    private Integer concertDate;
    private String startTime;
    private String endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "schedule")
    @ToString.Exclude
    private List<Seat> seatList = new ArrayList<>();

    @Builder
    public Schedule(Long id, Long basePrice, Integer concertDate, String startTime, String endTime, Concert concert,
                    List<Seat> seatList) {
        this.id = id;
        this.basePrice = basePrice;
        this.concertDate = concertDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.concert = concert;
        this.seatList = seatList;
    }
}
