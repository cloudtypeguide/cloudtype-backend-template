package io.cloudtype.waitlist.model;

import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "guests")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String deptName;

    @Column(nullable = false)
    private String bookerName;

    @Column(nullable = false)
    private String roomName;

    // 🔴 [핵심 수정] DB 예약어 'date' 충돌 방지! 
    // 자바에서는 date라고 부르지만, DB에는 visit_date라고 저장합니다.
    @Column(name = "visit_date", nullable = false)
    private String date;

    @Column(nullable = false)
    private String startTime;

    @Column(nullable = false)
    private String endTime;

    @Column(nullable = false)
    private String timeInfo;
}
